package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.SimpleProductCategory;
import com.outdoor.foodcalc.service.ProductCategoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
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
@RunWith(MockitoJUnitRunner.class)
public class ProductCategoryEndpointTest {

    private static final long CATEGORY_ID = 12345;
    private static final String CATEGORY_NAME = "Test category";

    private SimpleProductCategory dummyCategory;

    private ProductCategoryEndpoint endpoint;

    @Mock
    private ProductCategoryService service;

    @Before
    public void setUp() throws Exception {
        endpoint = new ProductCategoryEndpoint(service);
        dummyCategory = new SimpleProductCategory();
        dummyCategory.id = CATEGORY_ID;
        dummyCategory.name = CATEGORY_NAME;
    }

    @Test
    public void getCategoriesTest() throws Exception {
        List<SimpleProductCategory> expected = Collections.singletonList(dummyCategory);

        when(service.getCategories()).thenReturn(expected);

        List<SimpleProductCategory> actual = endpoint.getCategories();
        assertEquals(expected, actual);

        verify(service, times(1)).getCategories();
    }

    @Test
    public void getCategoryTest() throws Exception {
        Optional<SimpleProductCategory> expected = Optional.of(dummyCategory);

        when(service.getCategory(CATEGORY_ID)).thenReturn(expected);

        Response response = endpoint.getCategory(CATEGORY_ID);
        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof SimpleProductCategory);
        assertEquals(dummyCategory, response.getEntity());

        verify(service, times(1)).getCategory(CATEGORY_ID);
    }

    @Test
    public void getCategoryNotFoundTest() throws Exception {
        Optional<SimpleProductCategory> expected = Optional.empty();

        when(service.getCategory(CATEGORY_ID)).thenReturn(expected);

        Response response = endpoint.getCategory(CATEGORY_ID);
        assertEquals(404, response.getStatus());

        verify(service, times(1)).getCategory(CATEGORY_ID);
    }

    @Test
    public void addCategoryTest() throws Exception {
        SimpleProductCategory category = new SimpleProductCategory();
        category.name = CATEGORY_NAME;
        long newId = 54321;
        URI uri = new URI("https://foodcalc.com/product-category");
        URI expectedUri = new URI("https://foodcalc.com/product-category/54321");

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePath()).thenReturn(uri);
        when(service.addCategory(CATEGORY_NAME)).thenReturn(newId);

        Response response = endpoint.addCategory(category, uriInfo);
        assertEquals(201, response.getStatus());
        assertEquals(expectedUri, response.getLocation());

        verify(uriInfo, times(1)).getAbsolutePath();
        verify(service, times(1)).addCategory(CATEGORY_NAME);
    }

    @Test
    public void updateCategoryTest() throws Exception {
        when(service.updateCategory(dummyCategory)).thenReturn(true);

        Response response = endpoint.updateCategory(dummyCategory);
        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof SimpleProductCategory);
        assertEquals(dummyCategory, response.getEntity());

        verify(service, times(1)).updateCategory(dummyCategory);
    }

    @Test
    public void updateCategoryNotFoundTest() throws Exception {
        when(service.updateCategory(dummyCategory)).thenReturn(false);

        Response response = endpoint.updateCategory(dummyCategory);
        assertEquals(404, response.getStatus());

        verify(service, times(1)).updateCategory(dummyCategory);
    }

    @Test
    public void deleteCategoryTest() throws Exception {
        when(service.deleteCategory(CATEGORY_ID)).thenReturn(true);

        Response response = endpoint.deleteCategory(CATEGORY_ID);
        assertEquals(204, response.getStatus());

        verify(service, times(1)).deleteCategory(CATEGORY_ID);
    }

    @Test
    public void deleteCategoryNotFoundTest() throws Exception {
        when(service.deleteCategory(CATEGORY_ID)).thenReturn(false);

        Response response = endpoint.deleteCategory(CATEGORY_ID);
        assertEquals(404, response.getStatus());

        verify(service, times(1)).deleteCategory(CATEGORY_ID);
    }

}