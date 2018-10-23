package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.SimpleProductCategory;
import com.outdoor.foodcalc.service.ProductCategoryService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductCategoryEndpoint} class
 *
 * @author Anton Borovyk.
 */
public class ProductCategoryEndpointTest {

    private static final long CATEGORY_ID = 12345;
    private static final String CATEGORY_NAME = "Test category";

    private SimpleProductCategory dummyCategory;

    @InjectMocks
    private ProductCategoryEndpoint endpoint;

    @Mock
    private ProductCategoryService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        dummyCategory = new SimpleProductCategory();
        dummyCategory.id = CATEGORY_ID;
        dummyCategory.name = CATEGORY_NAME;
    }

    @Test
    public void getCategoriesTest() {
        List<SimpleProductCategory> expected = Collections.singletonList(dummyCategory);

        when(service.getCategories()).thenReturn(expected);

        List<SimpleProductCategory> actual = endpoint.getCategories();
        assertEquals(expected, actual);

        verify(service, times(1)).getCategories();
    }

    @Test
    public void getCategoryTest() {
        Optional<SimpleProductCategory> expected = Optional.of(dummyCategory);

        when(service.getCategory(CATEGORY_ID)).thenReturn(expected);

        Response response = endpoint.getCategory(CATEGORY_ID);
        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof SimpleProductCategory);
        assertEquals(dummyCategory, response.getEntity());

        verify(service, times(1)).getCategory(CATEGORY_ID);
    }

    @Test
    public void getCategoryNotFoundTest() {
        Optional<SimpleProductCategory> expected = Optional.empty();

        when(service.getCategory(CATEGORY_ID)).thenReturn(expected);

        Response response = endpoint.getCategory(CATEGORY_ID);
        assertEquals(404, response.getStatus());

        verify(service, times(1)).getCategory(CATEGORY_ID);
    }

    @Test
    public void addCategoryTest() {
        SimpleProductCategory category = new SimpleProductCategory();
        category.name = CATEGORY_NAME;

        UriInfo uriInfo = mock(UriInfo.class);
        when(service.addCategory(CATEGORY_NAME)).thenReturn(dummyCategory);

        Response response = endpoint.addCategory(category, uriInfo);
        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof SimpleProductCategory);
        assertEquals(dummyCategory, response.getEntity());

        verify(service, times(1)).addCategory(CATEGORY_NAME);
    }

    @Test
    public void updateCategoryTest() {
        when(service.updateCategory(dummyCategory)).thenReturn(true);

        Response response = endpoint.updateCategory(dummyCategory);
        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof SimpleProductCategory);
        assertEquals(dummyCategory, response.getEntity());

        verify(service, times(1)).updateCategory(dummyCategory);
    }

    @Test
    public void updateCategoryNotFoundTest() {
        when(service.updateCategory(dummyCategory)).thenReturn(false);

        Response response = endpoint.updateCategory(dummyCategory);
        assertEquals(404, response.getStatus());

        verify(service, times(1)).updateCategory(dummyCategory);
    }

    @Test
    public void deleteCategoryTest() {
        when(service.deleteCategory(CATEGORY_ID)).thenReturn(true);

        Response response = endpoint.deleteCategory(CATEGORY_ID);
        assertEquals(204, response.getStatus());

        verify(service, times(1)).deleteCategory(CATEGORY_ID);
    }

    @Test
    public void deleteCategoryNotFoundTest() {
        when(service.deleteCategory(CATEGORY_ID)).thenReturn(false);

        Response response = endpoint.deleteCategory(CATEGORY_ID);
        assertEquals(404, response.getStatus());

        verify(service, times(1)).deleteCategory(CATEGORY_ID);
    }

}