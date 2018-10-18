import Vue from 'vue'
import Router from 'vue-router'
import HomePage from 'src/components/HomePage'
import AboutPage from 'src/components/AboutPage'
import DirectoryPage from 'src/components/directory/DirectoryPage'
import Products from 'src/components/directory/Products'
import ProductCategories from 'src/components/directory/ProductCategories'

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
          path: 'categories',
          name: 'ProductCategoriesPage',
          component: ProductCategories
        }
      ]
    }
  ]
})
