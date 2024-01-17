<template>
  <div>
    <h4 class="meal-header">{{meal.type}}</h4>
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
export default {
  name: 'MealView',
  props: {
    meal: {
      type: Object,
      required: true
    },
    editable: {
      type: Boolean,
      required: false
    }
  },

  methods: {
    editMeal () {
      // this.$router.push({path: '/plan/' + this.$route.params.planId + '/day/' + this.meal.id})
      console.log('Going to edit meal, id - ' + this.meal.id)
    },

    deleteMeal () {
      // this.$emit('remove', this.meal.id)
      console.log('Going to delete meal, id - ' + this.meal.id)
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
