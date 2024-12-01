<template>
  <div>
    <h2 class="directory-header">Dishes list</h2>

    <!--Dishes list-->
    <div v-if="hasDishes()" class="container border">
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
      <div v-for="category in categoriesWithDishes" :key="category.id">
        <category-with-dishes v-bind:category="category"
                              v-bind:manage-mode="true"
                              v-bind:update-dish-endpoint="dishesEndpointUrl"
                              v-on:remove="removeDish"/>
      </div>
    </div>
    <div v-if="!hasDishes() && errorMessage === null">
      <p><em>No Dishes yet...</em></p>
    </div>
    <router-link :to="{ name : 'EditDishPage' }">Add new</router-link>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import CategoryWithDishes from 'src/components/directory/dish/CategoryWithDishes'

export default {
  name: 'Dishes',
  components: {CategoryWithDishes},
  data () {
    return {
      dishesEndpointUrl: '/api/dishes/',
      categoriesWithDishes: [],
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

    hasDishes () {
      for (const category of this.categoriesWithDishes) {
        if (category.dishes.length > 0) {
          return true
        }
      }
      return false
    },

    getAllDishes () {
      axios.get(this.dishesEndpointUrl)
        .then(response => {
          this.categoriesWithDishes = response.data
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to load Products...')
        })
    },

    removeDish (dishId) {
      axios.delete(this.dishesEndpointUrl + dishId)
        .then(() => {
          this.clearErrors()
          this.getAllDishes()
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to delete dish with id = ' + dishId)
        })
    }
  },

  mounted () {
    // load dishes on page init
    this.getAllDishes()
  }
}
</script>

<style scoped>

</style>
