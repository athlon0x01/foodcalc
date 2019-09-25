<template>
  <div>
    <h2 class="directory-header">Products list:</h2>

    <!--Products list-->
    <div v-if="categoriesWithProducts.length > 0" class="container border">
      <!--Header-->
      <div class="row headerRow bg-light">
        <div class="col-md-5 border"><strong>Name</strong></div>
        <div class="col-md-1 border"><strong>Calorific</strong></div>
        <div class="col-md-1 border"><strong>Proteins</strong></div>
        <div class="col-md-1 border"><strong>Fats</strong></div>
        <div class="col-md-1 border"><strong>Carbs</strong></div>
        <div class="col-md-1 border"><strong>Weight</strong></div>
      </div>
      <!--Content-->
      <div v-for="category in categoriesWithProducts" :key="category.id">
        <category-with-products v-bind:category="category"
                                v-bind:categories="simpleCategories"
                                v-on:update="updateProduct"
                                v-on:remove="removeProduct"/>
      </div>
    </div>
    <div v-if="categoriesWithProducts.length === 0 && errorMessage === null">
      <p><i>No Products yet...</i></p>
    </div>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>

    <!--Add new item section-->
    <b-button variant="link" size="sm" v-on:click="addMode = !addMode">Add new</b-button>
    <div v-if="addMode !== undefined && addMode" class="container">
      <new-product v-bind:categories="simpleCategories" v-on:addNew="addProduct" v-on:cancelAdd="addMode = false"/>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import CategoryWithProducts from 'src/components/directory/product/CategoryWithProducts'
import NewProduct from 'src/components/directory/product/NewProduct'

export default {
  name: 'Products',
  components: {NewProduct, CategoryWithProducts},
  data () {
    return {
      productsEndpointUrl: '/api/products/',
      categoriesWithProducts: [],
      simpleCategories: [],
      addMode: false,
      errorMessage: null
    }
  },

  methods: {
    getErrorMessage (error, defaultMessage) {
      if (error.response !== undefined && error.response.data !== undefined &&
        (typeof error.response.data === 'string' || error.response.data instanceof String)) {
        this.errorMessage = error.response.data
      } else {
        console.log(error)
        this.errorMessage = defaultMessage
      }
    },

    clearErrors () {
      this.errorMessage = null
      this.errors.clear()
    },

    getAllProducts () {
      axios.get(this.productsEndpointUrl)
        .then(response => {
          this.categoriesWithProducts = response.data
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to load Products...')
        })
    },

    addProduct (product) {
      this.addMode = false
      console.log('Adding product - ' + JSON.stringify(product))
      // axios.post(this.productsEndpointUrl, product)
      //   .then(response => {
      //     this.addMode = false
      //     this.clearErrors()
      //     this.getAllProducts()
      //   })
      //   .catch(e => {
      //     this.getErrorMessage(e, 'Failed to add product ' + JSON.stringify(product))
      //   })
    },

    updateProduct (product) {
      console.log('Editing product - ' + JSON.stringify(product))
    },

    removeProduct (productId) {
      console.log('Removing product - ' + productId)
    }
  },

  mounted () {
    // load products on page init
    this.getAllProducts()
    // load product categories for adding \ editing
    axios.get('/api/product-categories')
      .then(response => {
        this.simpleCategories = response.data
      })
      .catch(e => {
        this.getErrorMessage(e, 'Failed to load Product Categories...')
      })
  }
}
</script>

<style scoped>

</style>
