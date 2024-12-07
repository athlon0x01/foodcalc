<template>
  <div style="padding:10px">
    <p style="text-align: center"><em>Оберіть страву для додавання</em></p>
    <!--Dishes list-->
    <div v-if="hasDishes()" class="container border">
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
      <div v-for="category in categoriesWithDishes" :key="category.id">
        <category-with-dishes v-bind:category="category"
                              v-bind:select-mode="true"
                              v-on:select="selectDish"/>
      </div>
    </div>
    <div v-if="!hasDishes() && errorMessage === null">
      <p><em>Страв немає...</em></p>
      <router-link :to="{ name : 'EditDishPage' }">Додати нову</router-link>
    </div>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>
    <div style="padding-top:10px">
      <b-button variant="outline-info" size="sm" v-on:click="closeMe" >Приховати страви</b-button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import CategoryWithDishes from 'src/components/directory/dish/CategoryWithDishes'

export default {
  name: 'SelectDishView',
  components: {CategoryWithDishes},
  data () {
    return {
      dishesEndpointUrl: '/api/dishes/',
      categoriesWithDishes: [],
      errorMessage: null
    }
  },

  methods: {
    closeMe () {
      this.$emit('hideDishes')
    },

    selectDish (dishId) {
      this.$emit('dishSelected', dishId)
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
          this.getErrorMessage(e, 'Failed to load Dishes...')
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
