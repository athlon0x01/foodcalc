<template>
  <div>
    <h2 class="directory-header">Продукти</h2>

    <!--Products list-->
    <div v-if="hasProducts()" class="container border">
      <!--Header-->
      <div class="row headerRow bg-light">
        <div class="col-md-5 border"><strong>Назва</strong></div>
        <div class="col-md-1 border"><strong>Калорії</strong></div>
        <div class="col-md-1 border"><strong>Протеіни</strong></div>
        <div class="col-md-1 border"><strong>Жири</strong></div>
        <div class="col-md-1 border"><strong>Вуглвд.</strong></div>
        <div class="col-md-1 border"><strong>Вага</strong></div>
      </div>
      <!--Content-->
      <div v-for="category in categoriesWithProducts" :key="category.id">
        <category-with-products v-bind:category="category"
                                v-bind:categories="simpleCategories"
                                v-on:update="updateProduct"
                                v-on:remove="removeProduct"/>
      </div>
    </div>
    <div v-if="!hasProducts() && errorMessage === null">
      <p><em>Немає продуктів...</em></p>
    </div>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>

    <!--Add new item section-->
    <b-button variant="link" size="sm" v-on:click="addMode = !addMode">Додати новий продукт</b-button>
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

    hasProducts () {
      for (const category of this.categoriesWithProducts) {
        if (category.products.length > 0) {
          return true
        }
      }
      return false
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
      axios.post(this.productsEndpointUrl, product)
        .then(() => {
          this.addMode = false
          this.clearErrors()
          this.getAllProducts()
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to add product ' + JSON.stringify(product))
        })
    },

    updateProduct (product) {
      axios.put(this.productsEndpointUrl + product.id, product)
        .then(() => {
          this.clearErrors()
          this.getAllProducts()
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to update product ' + product.name)
        })
    },

    removeProduct (productId) {
      axios.delete(this.productsEndpointUrl + productId)
        .then(() => {
          this.clearErrors()
          this.getAllProducts()
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to delete product with id = ' + productId)
        })
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
