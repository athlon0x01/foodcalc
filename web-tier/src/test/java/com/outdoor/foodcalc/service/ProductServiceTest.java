package com.outdoor.foodcalc.service;

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
    @Ignore
    public void getAllProductsTest() {
        ProductCategory category1 = new ProductCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
        ProductCategory category2 = new ProductCategory(CATEGORY_2_ID, CATEGORY_2_NAME);
        ProductCategory category3 = new ProductCategory(CATEGORY_3_ID, CATEGORY_3_NAME);
        List<ProductCategory> domainCategories = Arrays.asList(category1, category2, category3);

        Product product1 = new Product(12367, "first prod", category1);
        Product product2 = new Product(12367, "second prod", category1, 1.1f, 3, 4.5f, 7, 11);
        Product product3 = new Product(12367, "third prod", category2, 13, 11.5f, 7, 32.2f, 5.5f);
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
}
