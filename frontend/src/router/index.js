import Vue from 'vue'
import Router from 'vue-router'
import HomePage from 'src/components/HomePage'
import AboutPage from 'src/components/AboutPage'
import DirectoryPage from 'src/components/directory/DirectoryPage'
import Products from 'src/components/directory/Products'
import Dishes from 'src/components/directory/Dishes'
import EditDish from 'src/components/directory/dish/EditDish'
import ProductCategories from 'src/components/directory/ProductCategories'
import DishCategories from 'src/components/directory/DishCategories'
import MealTypes from 'src/components/directory/MealTypes'
import FoodPlan from 'src/components/plan/FoodPlan'
import FoodDay from 'src/components/plan/FoodDay'
import EditMeal from 'src/components/meal/EditMeal'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'HomePage',
      component: HomePage
    },
    {
      path: '/about',
      name: 'AboutPage',
      component: AboutPage
    },
    {
      path: '/directory',
      component: DirectoryPage,
      redirect: {
        name: 'ProductsPage'
      },
      children: [
        {
          path: 'products',
          name: 'ProductsPage',
          component: Products
        },
        {
          path: 'dishes',
          name: 'DishesPage',
          component: Dishes
        },
        {
          path: 'dish-edit',
          name: 'EditDishPage',
          component: EditDish,
          props: true
        },
        {
          path: 'product-categories',
          name: 'ProductCategoriesPage',
          component: ProductCategories
        },
        {
          path: 'dish-categories',
          name: 'DishCategoriesPage',
          component: DishCategories
        },
        {
          path: 'meal-types',
          name: 'MealTypesPage',
          component: MealTypes
        }
      ]
    },
    // I didn't manage to do proper nested structure for plan / days / meals pages routes
    {
      path: '/plan/:planId',
      name: 'FoodPlanPage',
      component: FoodPlan
    },
    {
      path: '/plan/:planId/day/:dayId',
      name: 'FoodDayPage',
      component: FoodDay
    },
    {
      path: '/plan/:planId/day/:dayId/meal/:mealId',
      name: 'MealPage',
      component: EditMeal
    }
  ]
})
