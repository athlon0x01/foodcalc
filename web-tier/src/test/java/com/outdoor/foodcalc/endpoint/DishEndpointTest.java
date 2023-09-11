package com.outdoor.foodcalc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.endpoint.impl.DishEndpoint;
import com.outdoor.foodcalc.model.dish.*;
import com.outdoor.foodcalc.model.product.ProductView;
import com.outdoor.foodcalc.service.DishService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * JUnit tests for {@link DishEndpoint} class
 *
 * @author Olga Borovyk.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DishEndpointTest extends ApiUnitTest{

     @MockBean
     private DishService service;

     @Autowired
     private WebApplicationContext webApplicationContext;

     @Autowired
     @Override
     public void setMapper(ObjectMapper mapper) {
         super.setMapper(mapper);
     }

     private static final long CATEGORY_1_ID = 12345;
     private static final long CATEGORY_2_ID = 33321;
     private static final String CATEGORY_1_NAME = "First category";
     private static final String CATEGORY_2_NAME = "Empty";

     private static final Product product1 = new Product(101010, "first prod", "",
             new ProductCategory(77777, "test product cat1"),
             1.1f, 3, 4.5f, 7, 110);
     private static final Product product2 = new Product(121212, "second prod", "",
             new ProductCategory(77777, "test product cat1"),
             1.1f, 3, 4.5f, 7, 110);


     private static final ProductRef productRef1 = new ProductRef(product1, 200);
     private static final ProductRef productRef2 = new ProductRef(product2, 300);

     private static final long DISH_1_ID = 22222;
     private static final long DISH_2_ID = 33333;

     @Before
     public void setUp() {
          setMockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
     }

     private ProductView createProductView(ProductRef productRef) {
          ProductView productView = new ProductView();
          productView.id = productRef.getProductId();
          productView.name = productRef.getName();
          productView.categoryId = productRef.getProductCategoryId();
          productView.calorific = 1.1f;
          productView.proteins = 1.1f;
          productView.fats = 1.1f;
          productView.carbs = 1.1f;
          productView.weight = 20f;
          return productView;
     }


     private DishView createDishView(long id, String name, long categoryId, List<ProductView> products) {
          DishView dishView = new DishView();
          dishView.id = id;
          dishView.name = name;
          dishView.categoryId = categoryId;
          dishView.calorific = 0.1f;
          dishView.carbs = 0.1f;
          dishView.fats = 0.1f;
          dishView.proteins = 0.1f;
          dishView.weight = 0.1f;
          dishView.products = products;
          return dishView;
     }

     private SimpleDish createSimpleDish(long id) {
          SimpleDish simpleDish = new SimpleDish();
          simpleDish.id = id;
          simpleDish.name = "test simpleDish";
          simpleDish.categoryId = CATEGORY_1_ID;
          DishProduct product1 = new DishProduct();
          product1.productId = 1000;
          product1.weight = 100.1f;
          DishProduct product2 = new DishProduct();
          product2.productId = 2000;
          product2.weight = 200.2f;
          simpleDish.products = List.of(product1, product2);
          return simpleDish;
     }
     private boolean equalDishViews(DishView dw1, DishView dw2) {
          if (dw1 == dw2) return true;
          if (dw2 == null || dw1 == null || dw1.getClass() != dw2.getClass()) return false;
          return dw1.id == dw2.id &&
                  dw1.categoryId == dw2.categoryId &&
                  Float.compare(dw2.calorific, dw1.calorific) == 0 &&
                  Float.compare(dw2.proteins, dw1.proteins) == 0 &&
                  Float.compare(dw2.fats, dw1.fats) == 0 &&
                  Float.compare(dw2.carbs, dw1.carbs) == 0 &&
                  Float.compare(dw2.weight, dw1.weight) == 0 &&
                  dw1.name.equals(dw2.name);
     }

     @Test
     public void getAllDishesTest() throws Exception {
          CategoryWithDishes categoryWithDishes1 = new CategoryWithDishes();
          CategoryWithDishes categoryWithDishes2 = new CategoryWithDishes();

          categoryWithDishes1.id = CATEGORY_1_ID;
          categoryWithDishes1.name = CATEGORY_1_NAME;
          DishView dishView1 = createDishView(11, "dishView11", CATEGORY_1_ID,
                  Arrays.asList(
                          createProductView(productRef1),
                          createProductView(productRef2)));
          DishView dishView2 = createDishView(12, "dishView12", CATEGORY_1_ID,
                  Collections.emptyList());
          categoryWithDishes1.dishes = Arrays.asList(dishView1, dishView2);

          categoryWithDishes2.id = CATEGORY_2_ID;
          categoryWithDishes2.name = CATEGORY_2_NAME;
          categoryWithDishes2.dishes = Collections.singletonList(createDishView(31,"dishView31", CATEGORY_2_ID,
                  Collections.emptyList()));

          List<CategoryWithDishes> expected = List.of(categoryWithDishes1, categoryWithDishes2);

          when(service.getAllDishes()).thenReturn(expected);
          get("/dishes")
                  .andExpect(jsonPath("$", hasSize(2)))
                  .andExpect(jsonPath("$[0].id", is(((int)CATEGORY_1_ID))))
                  .andExpect(jsonPath("$[0].name", is(CATEGORY_1_NAME)))
                  .andExpect(jsonPath("$[0].dishes[0].id", is(11)))
                  .andExpect(jsonPath("$[0].dishes[0].name", is("dishView11")))
                  .andExpect(jsonPath("$[0].dishes[0].categoryId", is((int)CATEGORY_1_ID)))
                  .andExpect(jsonPath("$[0].dishes[0].calorific", equalTo(0.1)))
                  .andExpect(jsonPath("$[0].dishes[0].proteins", equalTo(0.1)))
                  .andExpect(jsonPath("$[0].dishes[0].fats", equalTo(0.1)))
                  .andExpect(jsonPath("$[0].dishes[0].carbs", equalTo(0.1)))
                  .andExpect(jsonPath("$[0].dishes[0].weight", equalTo(0.1)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].id", is(101010)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].name", is("first prod")))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].categoryId", is(77777)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].calorific", equalTo(1.1)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].proteins", equalTo(1.1)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].fats", equalTo(1.1)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].carbs", equalTo(1.1)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].weight", equalTo(20.0)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].id", is(121212)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].name", is("second prod")))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].categoryId", is(77777)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].calorific", equalTo(1.1)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].proteins", equalTo(1.1)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].fats", equalTo(1.1)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].carbs", equalTo(1.1)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].weight", equalTo(20.0)))
                  .andExpect(jsonPath("$[0].id", is((int)CATEGORY_1_ID)))
                  .andExpect(jsonPath("$[0].name", is(CATEGORY_1_NAME)))
                  .andExpect(jsonPath("$[0].dishes[1].id", is(12)))
                  .andExpect(jsonPath("$[0].dishes[1].name", is("dishView12")))
                  .andExpect(jsonPath("$[0].dishes[1].categoryId", is((int)CATEGORY_1_ID)))
                  .andExpect(jsonPath("$[0].dishes[1].calorific", equalTo(0.1)))
                  .andExpect(jsonPath("$[0].dishes[1].proteins", equalTo(0.1)))
                  .andExpect(jsonPath("$[0].dishes[1].fats", equalTo(0.1)))
                  .andExpect(jsonPath("$[0].dishes[1].carbs", equalTo(0.1)))
                  .andExpect(jsonPath("$[0].dishes[1].weight", equalTo(0.1)))
                  .andExpect(jsonPath("$[1].id", is((int) CATEGORY_2_ID)))
                  .andExpect(jsonPath("$[1].name", is(CATEGORY_2_NAME)))
                  .andExpect(jsonPath("$[1].dishes[0].id", is(31)))
                  .andExpect(jsonPath("$[1].dishes[0].name", is("dishView31")))
                  .andExpect(jsonPath("$[1].dishes[0].categoryId", is((int) CATEGORY_2_ID)))
                  .andExpect(jsonPath("$[1].dishes[0].calorific", equalTo(0.1)))
                  .andExpect(jsonPath("$[1].dishes[0].proteins", equalTo(0.1)))
                  .andExpect(jsonPath("$[1].dishes[0].fats", equalTo(0.1)))
                  .andExpect(jsonPath("$[1].dishes[0].carbs", equalTo(0.1)))
                  .andExpect(jsonPath("$[1].dishes[0].weight", equalTo(0.1)));

          verify(service).getAllDishes();
     }

     @Test
     public void getDishTest() throws Exception {
          DishView expected = createDishView(DISH_1_ID, "dishView1", CATEGORY_1_ID,
                  Arrays.asList(
                          createProductView(productRef1),
                          createProductView(productRef2)));

          when(service.getDish(DISH_1_ID)).thenReturn(expected);

          MvcResult mvcResult = get("/dishes/" + DISH_1_ID).andReturn();

          DishView actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), DishView.class);
          assertTrue(equalDishViews(expected, actual));

          verify(service).getDish(DISH_1_ID);
     }

     @Test
     public void addDishTest() throws Exception {
          SimpleDish simpleDishToAdd = createSimpleDish(DISH_1_ID);

          SimpleDish expected = createSimpleDish(DISH_2_ID);

          when(service.addDish(simpleDishToAdd)).thenReturn(expected);
          MvcResult mvcResult = post("/dishes", simpleDishToAdd).andReturn();

          SimpleDish actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), SimpleDish.class);
          assertEquals(expected, actual);

          verify(service).addDish(simpleDishToAdd);
     }

     @Test
     public void addDishValidationErrorTest() throws Exception {
          SimpleDish simpleDishToAdd = new SimpleDish();
          post400("/dishes", simpleDishToAdd);

          verify(service, never()).addDish(simpleDishToAdd);
     }

     @Test
     public void updateDishTest() throws Exception {
          long id = DISH_1_ID;
          SimpleDish simpleDish = createSimpleDish(id);

          doNothing().when(service).updateDish(simpleDish);

          put("/dishes/" + id, simpleDish).andReturn();

          verify(service).updateDish(simpleDish);
     }

     @Test
     public void updateDishIdValidationTest() throws Exception {
          String message = "Path variable Id = 55 doesn't match with request body Id = " + DISH_1_ID;
          SimpleDish simpleDishToUpdate = createSimpleDish(DISH_1_ID);

          put400("/dishes/55", simpleDishToUpdate).andExpect(jsonPath("$", is(message)));

          verify(service, never()).updateDish(simpleDishToUpdate);
     }

     @Test
     public void updateDishNotFoundTest() throws Exception {
          long id = DISH_1_ID;
          SimpleDish simpleDishToUpdate = createSimpleDish(id);
          doThrow(NotFoundException.class).when(service).updateDish(simpleDishToUpdate);

          put404("/dishes/" + id, simpleDishToUpdate).andReturn();

          verify(service).updateDish(simpleDishToUpdate);
     }

     @Test
     public void deleteDishTest() throws Exception {
          long id = DISH_1_ID;
          doNothing().when(service).deleteDish(id);

          delete("/dishes/" + id).andReturn();

          verify(service).deleteDish(id);
     }

     @Test
     public void deleteDishNotFoundTest() throws Exception {
          long id = DISH_1_ID;
          doThrow(NotFoundException.class).when(service).deleteDish(id);

          delete404("/dishes/" + id).andReturn();

          verify(service).deleteDish(id);
     }
}
