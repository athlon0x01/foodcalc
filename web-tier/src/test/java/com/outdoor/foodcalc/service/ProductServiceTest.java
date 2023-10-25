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

    private static final Product DOMAIN_PRODUCT_1 = new Product(12344, "first prod","",
            CATEGORY1, 1.7f, 5, 7.5f, 2, 333);
    private static final Product DOMAIN_PRODUCT_2 = new Product(12355, "second prod", "",
            CATEGORY1, 1.1f, 3, 4.5f, 7, 110);
    private static final Product DOMAIN_PRODUCT_3 = new Product(12366, "third prod", "",
            CATEGORY2, 13, 11.5f, 7, 32.2f, 55);

    private static final ProductView PRODUCT_VIEW_1 = new ProductView(12344, "first prod",
            CATEGORY1.getCategoryId(), 1.7f, 5, 7.5f, 2, 33.3f);
    private static final ProductView PRODUCT_VIEW_2 = new ProductView(12355, "second prod",
            CATEGORY1.getCategoryId(), 1.1f, 3, 4.5f, 7, 11f);
    private static final ProductView PRODUCT_VIEW_3 = new ProductView(12366, "third prod",
            CATEGORY2.getCategoryId(), 13, 11.5f, 7, 32.2f, 5.5f);

    private static final ProductView DUMMY_PRODUCT_VIEW = new ProductView(12345, "second prod",
            CATEGORY_2_ID, 1.1f, 3f, 4.5f, 7f, 110f);

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

        Product domainProduct = new Product(-1, productView.getName(), "",
                CATEGORY1, productView.getCalorific(), productView.getProteins(),
                productView.getFats(), productView.getCarbs(), Math.round(productView.getWeight() *10));

        Product returnedProduct = new Product(productView.getId(), productView.getName(), "",
                CATEGORY1, productView.getCalorific(), productView.getProteins(),
                productView.getFats(), productView.getCarbs(), Math.round(productView.getWeight() *10));

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

        Product domainProduct = new Product(productView.getId(), productView.getName(), "",
                CATEGORY1, productView.getCalorific(), productView.getProteins(),
                productView.getFats(), productView.getCarbs(), Math.round(productView.getWeight() *10));

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
        Product domainProduct = new Product(12367, "second prod", "", CATEGORY2,
                1.1f,3, 4.5f, 7, 110);
        when(productDomainService.deleteProduct(domainProduct.getProductId())).thenReturn(true);

        assertTrue(productService.deleteProduct(domainProduct.getProductId()));

        verify(productDomainService).deleteProduct(domainProduct.getProductId());
    }
}
