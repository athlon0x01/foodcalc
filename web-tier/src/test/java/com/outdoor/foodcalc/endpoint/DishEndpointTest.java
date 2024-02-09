package com.outdoor.foodcalc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.endpoint.impl.DishEndpoint;
import com.outdoor.foodcalc.model.dish.*;
import com.outdoor.foodcalc.model.product.ProductItem;
import com.outdoor.foodcalc.model.product.ProductView;
import com.outdoor.foodcalc.service.DishService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * JUnit tests for {@link DishEndpoint} class
 *
 * @author Olga Borovyk.
 */
@ExtendWith(MockitoExtension.class)
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
     private static final long DISH_3_ID = 44444;


     private static final ProductView productView1 = ProductView.builder()
             .id(productRef1.getProductId()).name(productRef1.getName()).categoryId(productRef1.getProductCategoryId())
             .calorific(1.123f).proteins(1.123f).fats(1.123f).carbs(1.123f).weight(20f).build();
     private static final ProductView productView2 = ProductView.builder()
             .id(productRef2.getProductId()).name(productRef2.getName()).categoryId(productRef2.getProductCategoryId())
             .calorific(1.123f).proteins(1.123f).fats(1.123f).carbs(1.123f).weight(20f).build();

     private static final DishView dishView1 = DishView.builder()
             .id(DISH_1_ID).name("dishView1").categoryId(CATEGORY_1_ID)
             .calorific(1.123f).proteins(1.123f).fats(1.123f).carbs(1.123f).weight(20f)
             .products(Arrays.asList(productView1, productView2)).build();
     private static final DishView dishView2 = DishView.builder()
             .id(DISH_2_ID).name("dishView2").categoryId(CATEGORY_1_ID)
             .calorific(1.123f).proteins(1.123f).fats(1.123f).carbs(1.123f).weight(20f)
             .products(Collections.emptyList()).build();
     private static final DishView dishView3 = DishView.builder()
             .id(DISH_3_ID).name("dishView3").categoryId(CATEGORY_2_ID)
             .calorific(1.999f).proteins(1.123f).fats(1.123f).carbs(1.123f).weight(20f)
             .products(Collections.emptyList()).build();

     @BeforeEach
     public void setUp() {
          setMockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
     }

     private DishInfo createSimpleDish(long id) {
          ProductItem product1 = ProductItem.builder().productId(1000).weight(100.1f).build();
          ProductItem product2 = ProductItem.builder().productId(2000).weight(200.2f).build();
          DishInfo dishInfo = DishInfo.builder()
                  .id(id).name("test simpleDish").categoryId(CATEGORY_1_ID)
                  .products(List.of(product1, product2)).build();
          return dishInfo;
     }

     @Test
     public void getAllDishesTest() throws Exception {
          CategoryWithDishes categoryWithDishes1 = CategoryWithDishes.builder()
                  .id(CATEGORY_1_ID).name(CATEGORY_1_NAME).dishes(Arrays.asList(dishView1, dishView2)).build();
          CategoryWithDishes categoryWithDishes2 = CategoryWithDishes.builder()
                  .id(CATEGORY_2_ID).name(CATEGORY_2_NAME).dishes(Collections.singletonList(dishView3)).build();
          List<CategoryWithDishes> expected = List.of(categoryWithDishes1, categoryWithDishes2);

          when(service.getAllDishes()).thenReturn(expected);
          get("/dishes")
                  .andExpect(jsonPath("$", hasSize(2)))
                  .andExpect(jsonPath("$[0].id", is(((int)CATEGORY_1_ID))))
                  .andExpect(jsonPath("$[0].name", is(CATEGORY_1_NAME)))
                  .andExpect(jsonPath("$[0].dishes[0].id", is((int)DISH_1_ID)))
                  .andExpect(jsonPath("$[0].dishes[0].name", is("dishView1")))
                  .andExpect(jsonPath("$[0].dishes[0].categoryId", is((int)CATEGORY_1_ID)))
                  .andExpect(jsonPath("$[0].dishes[0].calorific", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[0].proteins", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[0].fats", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[0].carbs", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[0].weight", equalTo(20.0)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].id", is(101010)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].name", is("first prod")))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].categoryId", is(77777)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].calorific", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].proteins", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].fats", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].carbs", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[0].products[0].weight", equalTo(20.0)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].id", is(121212)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].name", is("second prod")))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].categoryId", is(77777)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].calorific", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].proteins", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].fats", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].carbs", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[0].products[1].weight", equalTo(20.0)))
                  .andExpect(jsonPath("$[0].id", is((int)CATEGORY_1_ID)))
                  .andExpect(jsonPath("$[0].name", is(CATEGORY_1_NAME)))
                  .andExpect(jsonPath("$[0].dishes[1].id", is((int)DISH_2_ID)))
                  .andExpect(jsonPath("$[0].dishes[1].name", is("dishView2")))
                  .andExpect(jsonPath("$[0].dishes[1].categoryId", is((int)CATEGORY_1_ID)))
                  .andExpect(jsonPath("$[0].dishes[1].calorific", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[1].proteins", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[1].fats", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[1].carbs", equalTo(1.12)))
                  .andExpect(jsonPath("$[0].dishes[1].weight", equalTo(20.0)))
                  .andExpect(jsonPath("$[1].id", is((int) CATEGORY_2_ID)))
                  .andExpect(jsonPath("$[1].name", is(CATEGORY_2_NAME)))
                  .andExpect(jsonPath("$[1].dishes[0].id", is((int)DISH_3_ID)))
                  .andExpect(jsonPath("$[1].dishes[0].name", is("dishView3")))
                  .andExpect(jsonPath("$[1].dishes[0].categoryId", is((int) CATEGORY_2_ID)))
                  .andExpect(jsonPath("$[1].dishes[0].calorific", equalTo(2.0)))
                  .andExpect(jsonPath("$[1].dishes[0].proteins", equalTo(1.12)))
                  .andExpect(jsonPath("$[1].dishes[0].fats", equalTo(1.12)))
                  .andExpect(jsonPath("$[1].dishes[0].carbs", equalTo(1.12)))
                  .andExpect(jsonPath("$[1].dishes[0].weight", equalTo(20.0)));

          verify(service).getAllDishes();
     }

     @Test
     public void getDishTest() throws Exception {
          DishView expected = dishView1;

          when(service.getDish(DISH_1_ID)).thenReturn(expected);

          MvcResult mvcResult = get("/dishes/" + DISH_1_ID).andReturn();

          DishView actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), DishView.class);
          assertEquals(expected, actual);

          verify(service).getDish(DISH_1_ID);
     }

     @Test
     public void addDishTest() throws Exception {
          DishInfo dishInfoToAdd = createSimpleDish(DISH_1_ID);

          DishInfo expected = createSimpleDish(DISH_2_ID);

          when(service.addDish(dishInfoToAdd)).thenReturn(expected);
          MvcResult mvcResult = post("/dishes", dishInfoToAdd).andReturn();

          DishInfo actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), DishInfo.class);
          assertEquals(expected, actual);

          verify(service).addDish(dishInfoToAdd);
     }

     @Test
     public void addDishValidationErrorTest() throws Exception {
          DishInfo dishInfoToAdd = DishInfo.builder().build();
          post400("/dishes", dishInfoToAdd);

          verify(service, never()).addDish(dishInfoToAdd);
     }

     @Test
     public void updateDishTest() throws Exception {
          DishInfo dishInfo = createSimpleDish(DISH_1_ID);

          doNothing().when(service).updateDish(dishInfo);

          put("/dishes/" + DISH_1_ID, dishInfo).andReturn();

          verify(service).updateDish(dishInfo);
     }

     @Test
     public void updateDishIdValidationTest() throws Exception {
          String message = "Path variable Id = 55 doesn't match with request body Id = " + DISH_1_ID;
          DishInfo dishInfoToUpdate = createSimpleDish(DISH_1_ID);

          put400("/dishes/55", dishInfoToUpdate).andExpect(jsonPath("$", is(message)));

          verify(service, never()).updateDish(dishInfoToUpdate);
     }

     @Test
     public void updateDishNotFoundTest() throws Exception {
          DishInfo dishInfoToUpdate = createSimpleDish(DISH_1_ID);
          doThrow(NotFoundException.class).when(service).updateDish(dishInfoToUpdate);

          put404("/dishes/" + DISH_1_ID, dishInfoToUpdate).andReturn();

          verify(service).updateDish(dishInfoToUpdate);
     }

     @Test
     public void deleteDishTest() throws Exception {
          doNothing().when(service).deleteDish(DISH_1_ID);

          delete("/dishes/" + DISH_1_ID).andReturn();

          verify(service).deleteDish(DISH_1_ID);
     }

     @Test
     public void deleteDishNotFoundTest() throws Exception {
          doThrow(NotFoundException.class).when(service).deleteDish(DISH_1_ID);

          delete404("/dishes/" + DISH_1_ID).andReturn();

          verify(service).deleteDish(DISH_1_ID);
     }
}
