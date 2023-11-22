package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.model.product.ProductView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductService} class
 *
 * @author Anton Borovyk.
 */
public class ProductServiceTest {

    private static final double DELTA = 0.00001;
    private static final long CATEGORY_1_ID = 12345;
    private static final long CATEGORY_2_ID = 54321;
    private static final long CATEGORY_3_ID = 33321;
    private static final String CATEGORY_1_NAME = "First category";
    private static final String CATEGORY_2_NAME = "Second category";
    private static final String CATEGORY_3_NAME = "Empty";

    private static final ProductCategory CATEGORY1 = new ProductCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
    private static final ProductCategory CATEGORY2 = new ProductCategory(CATEGORY_2_ID, CATEGORY_2_NAME);
    private static final ProductCategory CATEGORY3 = new ProductCategory(CATEGORY_3_ID, CATEGORY_3_NAME);

    private static final Product DOMAIN_PRODUCT_1 = Product.builder().productId(12344).name("first prod")
            .category(CATEGORY1).calorific(1.7f)
            .proteins(5).fats(7.5f).carbs(2).defaultWeight(333).build();
    private static final Product DOMAIN_PRODUCT_2 = Product.builder().productId(12355).name("second prod")
            .category(CATEGORY1).calorific(1.1f)
            .proteins(3).fats(4.5f).carbs(7).defaultWeight(110).build();
    private static final Product DOMAIN_PRODUCT_3 = Product.builder().productId(12366).name("third prod")
            .category(CATEGORY2).calorific(13)
            .proteins(11.5f).fats(7).carbs(32.2f).defaultWeight(55).build();

    private static final ProductView PRODUCT_VIEW_1 = ProductView.builder()
            .id(12344).name("first prod").categoryId(CATEGORY1.getCategoryId())
            .calorific(1.7f).proteins(5).fats(7.5f).carbs(2).weight(33.3f).build();
    private static final ProductView PRODUCT_VIEW_2 = ProductView.builder()
            .id(12355).name("second prod").categoryId(CATEGORY1.getCategoryId())
            .calorific(1.1f).proteins(3).fats(4.5f).carbs(7).weight(11f).build();
    private static final ProductView PRODUCT_VIEW_3 = ProductView.builder()
            .id(12366).name("third prod").categoryId(CATEGORY2.getCategoryId())
            .calorific(13).proteins(11.5f).fats(7).carbs(32.2f).weight(5.5f).build();

    @InjectMocks
    private ProductService productService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    private ProductDomainService productDomainService;
    @Mock
    private ProductCategoryDomainService categoryDomainService;

    @Test
    public void getAllProductsTest() {
        List<ProductCategory> domainCategories = Arrays.asList(CATEGORY1, CATEGORY2, CATEGORY3);
        List<Product> domainProducts = Arrays.asList(DOMAIN_PRODUCT_1, DOMAIN_PRODUCT_2, DOMAIN_PRODUCT_3);
        CategoryWithProducts categoryWithProducts1 = CategoryWithProducts.builder().id(CATEGORY1.getCategoryId())
                .name(CATEGORY1.getName()).products(Arrays.asList(PRODUCT_VIEW_1, PRODUCT_VIEW_2)).build();
        CategoryWithProducts categoryWithProducts2 = CategoryWithProducts.builder().id(CATEGORY2.getCategoryId())
                .name(CATEGORY2.getName()).products(Collections.singletonList(PRODUCT_VIEW_3)).build();
        CategoryWithProducts categoryWithProducts3 = CategoryWithProducts.builder().id(CATEGORY3.getCategoryId())
                .name(CATEGORY3.getName()).products(Collections.emptyList()).build();
        List<CategoryWithProducts> expected = Arrays.asList(
                categoryWithProducts1, categoryWithProducts2, categoryWithProducts3);

        when(categoryDomainService.getCategories()).thenReturn(domainCategories);
        when(productDomainService.getAllProducts()).thenReturn(domainProducts);

        List<CategoryWithProducts> actual = productService.getAllProducts();
        assertNotNull(actual);
        assertEquals(domainCategories.size(), actual.size());
        assertEquals(expected, actual);

        verify(categoryDomainService).getCategories();
        verify(productDomainService).getAllProducts();
    }

    @Test
    public void getProductTest() {
        Product domainProduct = DOMAIN_PRODUCT_2;

        when(productDomainService.getProduct(domainProduct.getProductId())).thenReturn(Optional.of(domainProduct));

        ProductView actual = productService.getProduct(domainProduct.getProductId());
        assertEquals(PRODUCT_VIEW_2, actual);

        verify(productDomainService).getProduct(domainProduct.getProductId());
    }

    @Test(expected = NotFoundException.class)
    public void getNotExistingProductTest() {
        when(productDomainService.getProduct(1)).thenReturn(Optional.empty());

        productService.getProduct(1);
    }

    @Test
    public void addProductTest() {
        ProductView productView = PRODUCT_VIEW_1;

        Product domainProduct = Product.builder().productId(-1).name(productView.getName())
                .category(CATEGORY1).calorific(productView.getCalorific()).proteins(productView.getProteins())
                .fats(productView.getFats()).carbs(productView.getCarbs())
                .defaultWeight(Math.round(productView.getWeight() *10)).build();

        Product returnedProduct = Product.builder().productId(productView.getId()).name(productView.getName())
                .category(CATEGORY1).calorific(productView.getCalorific())
                .proteins(productView.getProteins()).fats(productView.getFats()).carbs(productView.getCarbs())
                .defaultWeight(Math.round(productView.getWeight() *10)).build();

        when(categoryDomainService.getCategory(productView.getCategoryId())).thenReturn(Optional.of(CATEGORY1));
        when(productDomainService.addProduct(domainProduct)).thenReturn(returnedProduct);

        ProductView actual = productService.addProduct(productView);
        assertEquals(productView, actual);

        verify(categoryDomainService).getCategory(productView.getCategoryId());
        verify(productDomainService).addProduct(domainProduct);
    }

    @Test(expected = NotFoundException.class)
    public void addProductWithoutCategoryTest() {
        ProductView productView = PRODUCT_VIEW_1;

        when(categoryDomainService.getCategory(productView.getCategoryId())).thenReturn(Optional.empty());

        productService.addProduct(productView);
    }

    @Test
    public void updateProductTest() {
        ProductView productView = PRODUCT_VIEW_1;

        Product domainProduct = Product.builder().productId(productView.getId()).name(productView.getName())
                .category(CATEGORY1).calorific(productView.getCalorific())
                .proteins(productView.getProteins()).fats(productView.getFats()).carbs(productView.getCarbs())
                .defaultWeight(Math.round(productView.getWeight() *10)).build();

        when(categoryDomainService.getCategory(productView.getCategoryId())).thenReturn(Optional.of(CATEGORY1));
        when(productDomainService.updateProduct(domainProduct)).thenReturn(true);

        assertTrue(productService.updateProduct(productView));

        verify(categoryDomainService).getCategory(productView.getCategoryId());
        verify(productDomainService).updateProduct(domainProduct);
    }

    @Test(expected = NotFoundException.class)
    public void updateProductWithoutCategoryTest() {
        ProductView productView = PRODUCT_VIEW_1;

        when(categoryDomainService.getCategory(productView.getCategoryId())).thenReturn(Optional.empty());

        productService.updateProduct(productView);
    }

    @Test
    public void deleteProductTest() {
        when(productDomainService.deleteProduct(DOMAIN_PRODUCT_2.getProductId())).thenReturn(true);

        assertTrue(productService.deleteProduct(DOMAIN_PRODUCT_2.getProductId()));

        verify(productDomainService).deleteProduct(DOMAIN_PRODUCT_2.getProductId());
    }
}
