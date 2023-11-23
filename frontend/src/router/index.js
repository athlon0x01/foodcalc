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
    {
      path: '/plan/:planId',
      name: 'FoodPlanPage',
      component: FoodPlan
    }
  ]
})
