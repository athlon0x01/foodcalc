package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.model.product.ProductView;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductDomainService productDomainService;
    @Mock
    private ProductCategoryDomainService categoryDomainService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void getAllProductsTest() {
        List<ProductCategory> domainCategories = Arrays.asList(CATEGORY1, CATEGORY2, CATEGORY3);

        Product product1 = new Product(12367, "first prod", CATEGORY1);
        Product product2 = new Product(12367, "second prod", "", CATEGORY1, 1.1f, 3, 4.5f, 7, 110);
        Product product3 = new Product(12367, "third prod", "", CATEGORY2, 13, 11.5f, 7, 32.2f, 55);
        List<Product> domainProducts = Arrays.asList(product1, product2, product3);

        when(categoryDomainService.getCategories()).thenReturn(domainCategories);
        when(productDomainService.getAllProducts()).thenReturn(domainProducts);

        List<CategoryWithProducts> actual = productService.getAllProducts();
        assertNotNull(actual);
        assertEquals(domainCategories.size(), actual.size());
        CategoryWithProducts model1 = actual.get(0);
        assertEquals(CATEGORY_1_ID, model1.id);
        assertEquals(CATEGORY_1_NAME, model1.name);
        assertEquals(2, model1.products.size());
        assertProducts(product1, model1.products.get(0));
        assertProducts(product2, model1.products.get(1));

        CategoryWithProducts model2 = actual.get(1);
        assertEquals(CATEGORY_2_ID, model2.id);
        assertEquals(CATEGORY_2_NAME, model2.name);
        assertEquals(1, model2.products.size());
        assertProducts(product3, model2.products.get(0));

        CategoryWithProducts model3 = actual.get(2);
        assertEquals(CATEGORY_3_ID, model3.id);
        assertEquals(CATEGORY_3_NAME, model3.name);
        assertTrue(model3.products.isEmpty());

        verify(categoryDomainService).getCategories();
        verify(productDomainService).getAllProducts();
    }

    private void assertProducts(Product product, ProductView model) {
        assertEquals(product.getProductId(), model.id);
        assertEquals(product.getName(), model.name);
        assertEquals(product.getCategory().getCategoryId(), model.categoryId);
        assertEquals(product.getCalorific(), model.calorific, DELTA);
        assertEquals(product.getProteins(), model.proteins, DELTA);
        assertEquals(product.getFats(), model.fats, DELTA);
        assertEquals(product.getCarbs(), model.carbs, DELTA);
        assertEquals(product.getDefaultWeight(), model.weight, DELTA);
    }

    @Test
    public void getProductTest() {
        Product domainProduct = new Product(12367, "second prod", "", CATEGORY2,
                1.1f,3, 4.5f, 7, 110);

        when(productDomainService.getProduct(domainProduct.getProductId())).thenReturn(Optional.of(domainProduct));

        ProductView simpleProduct = productService.getProduct(domainProduct.getProductId());
        assertProducts(domainProduct, simpleProduct);

        verify(productDomainService).getProduct(domainProduct.getProductId());
    }

    @Test(expected = NotFoundException.class)
    public void getNotExistingProductTest() {
        when(productDomainService.getProduct(1)).thenReturn(Optional.empty());

        productService.getProduct(1);
    }

    @Test
    public void addProductTest() {
        ProductView simpleProduct = getSimpleProduct();

        Product domainProduct = new Product(-1, simpleProduct.name, "",
                CATEGORY2, simpleProduct.calorific,
                simpleProduct.proteins, simpleProduct.fats, simpleProduct.carbs, (int) simpleProduct.weight);

        Product returnedProduct = new Product(simpleProduct.id, simpleProduct.name, "",
                CATEGORY2, simpleProduct.calorific,
                simpleProduct.proteins, simpleProduct.fats, simpleProduct.carbs, (int) simpleProduct.weight);

        when(categoryDomainService.getCategory(simpleProduct.categoryId)).thenReturn(Optional.of(CATEGORY2));
        when(productDomainService.addProduct(domainProduct)).thenReturn(returnedProduct);

        ProductView actual = productService.addProduct(simpleProduct);
        assertProducts(returnedProduct, actual);

        verify(categoryDomainService).getCategory(simpleProduct.categoryId);
        verify(productDomainService).addProduct(domainProduct);
    }

    private ProductView getSimpleProduct() {
        ProductView simpleProduct = new ProductView();
        simpleProduct.id = 12345;
        simpleProduct.name = "second prod";
        simpleProduct.categoryId = CATEGORY_2_ID;
        simpleProduct.calorific = 1.1f;
        simpleProduct.proteins = 3f;
        simpleProduct.fats = 4.5f;
        simpleProduct.carbs = 7f;
        simpleProduct.weight = 110f;
        return simpleProduct;
    }

    @Test(expected = NotFoundException.class)
    public void addProductWithoutCategoryTest() {
        ProductView simpleProduct = getSimpleProduct();

        when(categoryDomainService.getCategory(simpleProduct.categoryId)).thenReturn(Optional.empty());

        productService.addProduct(simpleProduct);
    }

    @Test
    public void updateProductTest() {
        ProductView simpleProduct = getSimpleProduct();

        Product domainProduct = new Product(simpleProduct.id, simpleProduct.name, "",
                CATEGORY2, simpleProduct.calorific,
                simpleProduct.proteins, simpleProduct.fats, simpleProduct.carbs, (int) simpleProduct.weight);

        when(categoryDomainService.getCategory(simpleProduct.categoryId)).thenReturn(Optional.of(CATEGORY2));
        when(productDomainService.updateProduct(domainProduct)).thenReturn(true);

        assertTrue(productService.updateProduct(simpleProduct));

        verify(categoryDomainService).getCategory(simpleProduct.categoryId);
        verify(productDomainService).updateProduct(domainProduct);
    }

    @Test(expected = NotFoundException.class)
    public void updateProductWithoutCategoryTest() {
        ProductView simpleProduct = getSimpleProduct();

        when(categoryDomainService.getCategory(simpleProduct.categoryId)).thenReturn(Optional.empty());

        productService.updateProduct(simpleProduct);
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
