import Vue from 'vue'
import Router from 'vue-router'
import HomePage from '@/components/HomePage'
import AboutPage from '@/components/AboutPage'
import DirectoryPage from '@/components/directory/DirectoryPage'
import Products from '@/components/directory/Products'
import ProductCategories from '@/components/directory/ProductCategories'

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
        name: 'Products'
      },
      children: [
        {
          path: 'products',
          name: 'Products',
          component: Products
        },
        {
          path: 'categories',
          name: 'ProductCategories',
          component: ProductCategories
        }
      ]
    }
  ]
})
