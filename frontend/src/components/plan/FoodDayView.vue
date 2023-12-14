<template>
  <div>
    <h3 class="food-day-header">{{foodDay.date}}</h3>
    <div class="container">
      <div class="row">
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
export default {
  name: 'FoodDayView',
  props: {
    foodDay: {
      type: Object,
      required: true
    }
  },

  methods: {
    editDay () {
      this.$router.push({path: '/plan/' + this.$route.params.planId + '/day/' + this.foodDay.id})
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
