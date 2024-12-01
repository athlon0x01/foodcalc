<template>
  <div>
    <h3 class="food-day-header">{{foodDay.date}}</h3>
    <div class="container border border-primary">
      <div class="row" style="padding-left: 10px">
        <p>{{foodDay.description}}</p>
      </div>
      <div class="row justify-content-md-center" style="padding-bottom:5px">
        <div class="col-md-2">
          <b-button variant="outline-success" size="sm" v-on:click="editDay">Edit</b-button>
        </div>
        <div class="col-md-4"/>
        <div class="col-md-2">
          <b-button variant="outline-danger" size="sm" v-on:click="deleteDay">Delete</b-button>
        </div>
      </div>
      <!--day meals-->
      <template v-if="foodDay.meals.length > 0">
        <div v-for="meal in foodDay.meals" :key="'dayMeal-' + meal.id">
          <meal-view v-bind:meal="meal"/>
        </div>
        <div style="padding-bottom: 5px"/>
      </template>
      <!--Dishes section-->
      <template v-if="foodDay.dishes.length > 0" class="row">
        <div class="container border border-success">
          <!--Header-->
          <h5 style="padding-top:10px">Dishes</h5>
          <div class="row headerRow bg-light">
            <div class="col-md-5 border"><strong>Name</strong></div>
            <div class="col-md-1 border"><strong>Calorific</strong></div>
            <div class="col-md-1 border"><strong>Proteins</strong></div>
            <div class="col-md-1 border"><strong>Fats</strong></div>
            <div class="col-md-1 border"><strong>Carbs</strong></div>
            <div class="col-md-1 border"><strong>Weight</strong></div>
          </div>
          <div v-for="dish in foodDay.dishes" :key="'dayDish-' + dish.id">
            <dish-view v-bind:dish="dish"/>
          </div>
        </div>
      </template>
      <!--Products section-->
      <template v-if="foodDay.products.length > 0" class="row">
        <!--Header-->
        <h5 style="padding-top:10px">Products</h5>
        <div class="row headerRow bg-light">
          <div class="col-md-5 border"><strong>Name</strong></div>
          <div class="col-md-1 border"><strong>Calorific</strong></div>
          <div class="col-md-1 border"><strong>Proteins</strong></div>
          <div class="col-md-1 border"><strong>Fats</strong></div>
          <div class="col-md-1 border"><strong>Carbs</strong></div>
          <div class="col-md-1 border"><strong>Weight</strong></div>
          <div class="col-md-2 border"><strong>Package</strong></div>
        </div>
        <!--Content-->
        <div v-for="product in foodDay.products" :key="'dayProduct-' + product.id">
          <product-with-package-view v-bind:product="product"
                                     v-bind:manage-mode="false"/>
        </div>
        <div style="padding-bottom: 10px"/>
      </template>
      <div class="row headerRow bg-light">
        <div class="col-md-2"/>
        <div class="col-md-2 border"><em>Calorific</em></div>
        <div class="col-md-2 border"><em>Proteins</em></div>
        <div class="col-md-2 border"><em>Fats</em></div>
        <div class="col-md-2 border"><em>Carbs</em></div>
        <div class="col-md-2 border"><em>Weight</em></div>
      </div>
      <div class="row">
        <div class="col-md-2 border"><em><strong>Day Total</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{foodDay.calorific}}</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{foodDay.proteins}}</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{foodDay.fats}}</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{foodDay.carbs}}</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{foodDay.weight}}</strong></em></div>
      </div>
    </div>
  </div>
</template>

<script>
import MealView from 'src/components/meal/MealView'
import ProductWithPackageView from 'src/components/directory/product/ProductWithPackageView'
import DishView from 'src/components/directory/dish/DishView'

export default {
  name: 'FoodDayView',
  components: {MealView, ProductWithPackageView, DishView},
  props: {
    foodDay: {
      type: Object,
      required: true
    },
    planTitle: {
      type: String,
      required: false
    }
  },

  methods: {
    editDay () {
      this.$router.push({
        path: '/plan/' + this.$route.params.planId + '/day/' + this.foodDay.id,
        query: {planTitle: this.planTitle}
      })
    },

    deleteDay () {
      this.$emit('remove', this.foodDay.id)
    }
  }
}
</script>

<style scoped>
.food-day-header{
  text-align: center;
  color: #445588;
  padding-top:10px;
}
</style>
