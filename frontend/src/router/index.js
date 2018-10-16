import Vue from 'vue'
import Router from 'vue-router'
// import HelloWorld from '@/components/HelloWorld'
import HomeView from '@/components/HomeView'
import Products from '@/components/directory/Products'
import ProductCategories from '@/components/directory/ProductCategories'

Vue.use(Router)

export default new Router({
  routes: [
    // {
    //   path: '/',
    //   name: 'HelloWorld',
    //   component: HelloWorld
    // },
    {
      path: '/',
      name: 'HomeView',
      component: HomeView
    },
    {
      path: '/products',
      name: 'Products',
      component: Products
    },
    {
      path: '/categories',
      name: 'ProductCategories',
      component: ProductCategories
    }
  ]
})
