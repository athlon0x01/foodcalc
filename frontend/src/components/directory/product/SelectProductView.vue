<template>
  <div style="padding:10px">
    <p style="text-align: center"><em>Select products to de added</em></p>

    <!--Products list-->
    <div v-if="hasProducts()" class="container border">
      <!--Header-->
      <div class="row headerRow bg-light">
        <div class="col-md-6 border"><strong>Name</strong></div>
        <div class="col-md-1 border"><strong>Calorific</strong></div>
        <div class="col-md-1 border"><strong>Proteins</strong></div>
        <div class="col-md-1 border"><strong>Fats</strong></div>
        <div class="col-md-1 border"><strong>Carbs</strong></div>
        <div class="col-md-1 border"><strong>Weight</strong></div>
      </div>
      <!--Content-->
      <div v-for="category in categoriesWithProducts" :key="category.id">
        <category-with-product-views v-bind:category="category"
                                     v-on:productSelected="selectProduct"/>
      </div>
    </div>
    <div v-if="!hasProducts()">
      <p><em>No Products yet...</em></p>
      <router-link :to="{ name : 'ProductsPage' }"><b-button variant="outline-success" size="sm" >Add Products</b-button></router-link>
    </div>
    <div style="padding-top:10px">
      <b-button variant="outline-info" size="sm" v-on:click="closeMe" >Hide Products to be added</b-button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import CategoryWithProductViews from 'src/components/directory/product/CategoryWithProductViews'

export default {
  name: 'SelectProductView',
  components: {CategoryWithProductViews},

  data () {
    return {
      productsEndpointUrl: '/api/products/',
      categoriesWithProducts: []
    }
  },

  methods: {
    hasProducts () {
      for (const category of this.categoriesWithProducts) {
        if (category.products.length > 0) {
          return true
        }
      }
      return false
    },

    closeMe () {
      this.$emit('hideProducts')
    },

    selectProduct (product) {
      this.$emit('productSelected', product)
    },

    getAllProducts () {
      axios.get(this.productsEndpointUrl)
        .then(response => {
          this.categoriesWithProducts = response.data
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to load Products...')
        })
    }
  },

  mounted () {
    // load products on page init
    this.getAllProducts()
  }
}
</script>

<style scoped>

</style>
