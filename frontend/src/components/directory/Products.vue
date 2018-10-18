<template>
  <div>
    <h2 class="directory-header">Products list:</h2>

    <!--Products table-->
    <div v-if="categories.length > 0" class="container border">
      <div class="row headerRow bg-light">
        <div class="col-md-5 border"><strong>Name</strong></div>
        <div class="col-md-2 border"><strong>Calorific</strong></div>
        <div class="col-md-1 border"><strong>Proteins</strong></div>
        <div class="col-md-1 border"><strong>Fats</strong></div>
        <div class="col-md-1 border"><strong>Carbs</strong></div>
        <div class="col-md-2 border"><strong>Default weight</strong></div>
      </div>
      <template v-for="category in categories">
        <div v-bind:key="category.id" class="row bg-light">
          <div class="col-md-12 border"><i>{{category.name}}</i></div>
        </div>
        <template v-if="category.products.length > 0">
          <div v-for="product in category.products" :key="product.id" class="row">
            <div class="col-md-5 text-left border">{{product.name}}</div>
            <div class="col-md-2 border">{{product.calorific}}</div>
            <div class="col-md-1 border">{{product.proteins}}</div>
            <div class="col-md-1 border">{{product.fats}}</div>
            <div class="col-md-1 border">{{product.carbs}}</div>
            <div class="col-md-2 border">{{product.defaultWeight}}</div>
          </div>
        </template>
      </template>
    </div>
    <div v-else>
      <p><i>No Products loaded</i></p>
    </div>

    <!--Errors output-->
    <div v-if="errors.length > 0" class="alert">
      <p v-for="(error, index) in errors" :key="index">{{error}}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Products',
  data () {
    return {
      categories: [],
      errors: []
    }
  },

  mounted () {
    // load products on page init
    axios.get('/api/products')
      .then(response => {
        this.categories = response.data
      })
      .catch(e => {
        this.errors.push(e)
      })
  }
}
</script>

<style scoped>

</style>
