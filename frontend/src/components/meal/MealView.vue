<template>
  <div>
    <h4 class="meal-header">{{meal.type.name}}</h4>
    <div class="container">
      <div class="row">
        <p>{{meal.description}}</p>
      </div>
      <template v-if="editable">
        <div class="row justify-content-md-center" style="padding-bottom:5px">
          <div class="col-md-2">
            <b-button variant="outline-success" size="sm" v-on:click="editMeal">Edit</b-button>
          </div>
          <div class="col-md-4"/>
          <div class="col-md-2">
            <b-button variant="outline-danger" size="sm" v-on:click="deleteMeal">Delete</b-button>
          </div>
        </div>
      </template>
      <!--Products section-->
      <template v-if="meal.products.length > 0" class="row">
        <!--Header-->
        <h5 style="padding-top:10px">Products</h5>
        <div class="row headerRow bg-light">
          <div class="col-md-5 border"><strong>Name</strong></div>
          <div class="col-md-1 border"><strong>Calorific</strong></div>
          <div class="col-md-1 border"><strong>Proteins</strong></div>
          <div class="col-md-1 border"><strong>Fats</strong></div>
          <div class="col-md-1 border"><strong>Carbs</strong></div>
          <div class="col-md-1 border"><strong>Weight</strong></div>
        </div>
        <!--Content-->
        <div v-for="product in meal.products" :key="product.id">
          <product-view v-bind:product="product"
                        v-bind:select-mode="false"
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
        <div class="col-md-2 border"><em><strong>Meal Total</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{meal.calorific}}</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{meal.proteins}}</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{meal.fats}}</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{meal.carbs}}</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{meal.weight}}</strong></em></div>
      </div>
    </div>
  </div>
</template>

<script>
import ProductView from 'src/components/directory/product/ProductView'

export default {
  name: 'MealView',
  components: {ProductView},
  props: {
    meal: {
      type: Object,
      required: true
    },
    editable: {
      type: Boolean,
      required: false
    },
    dateTitle: {
      type: String,
      required: false
    }
  },

  methods: {
    editMeal () {
      this.$router.push({
        path: '/plan/' + this.$route.params.planId + '/day/' + this.$route.params.dayId + '/meal/' + this.meal.id,
        query: {dateTitle: this.dateTitle}
      })
    },

    deleteMeal () {
      this.$emit('remove', this.meal.id)
    }
  }
}
</script>

<style scoped>
.meal-header{
  text-align: center;
  color: #666600;
  padding-top:10px;
}
</style>
