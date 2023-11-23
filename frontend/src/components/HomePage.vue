<template>
  <div>
    <h1 class="calc-header">Outdoor Food Calculator</h1>
    <h2 class="food-plan-header">Food plans:</h2>
    <!--Food plans list-->
    <div v-if="foodPlans.length > 0" class="container">
      <!--Header-->
      <div class="row headerRow bg-light">
        <div class="col-md-7 border"><strong>Name</strong></div>
        <div class="col-md-2 border"><strong>Members</strong></div>
        <div class="col-md-2 border"><strong>Duration</strong></div>
      </div>
      <div v-for="plan in foodPlans" :key="plan.id">
        <div class="row">
          <div class="col-md-7 text-left border"><router-link :to="{path: '/plan/' + plan.id}">{{plan.name}}</router-link></div>
          <div class="col-md-2 border">{{plan.members}}</div>
          <div class="col-md-2 border">{{plan.duration}}</div>
          <div class="col-md-1">
            <b-button variant="outline-danger" size="sm" v-on:click="deletePlan(plan.id)">Delete</b-button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="foodPlans.length === 0 && errorMessage === null">
      <p><i>No Food Plans yet...</i></p>
    </div>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'HomePage',

  data () {
    return {
      foodPlansEndpointUrl: '/api/plans/',
      foodPlans: [],
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

    getAllFoodPlans () {
      axios.get(this.foodPlansEndpointUrl)
        .then(response => {
          this.foodPlans = response.data
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to load Food plans...')
        })
    },

    deletePlan (id) {
      axios.delete(this.foodPlansEndpointUrl + id)
        .then(() => {
          this.foodPlans = this.foodPlans.filter(plan => plan.id !== id)
          this.errorMessage = null
        })
        .catch(e => {
          let plan = this.foodPlans.find(plan => plan.id === id)
          this.getErrorMessage(e, 'Failed to delete ' + plan.name)
        })
    }
  },

  mounted () {
    // load food plans brief preview on page init
    this.getAllFoodPlans()
  }
}
</script>

<style scoped>
.calc-header{
  text-align: center;
  padding-top:20px;
  color: #4183c4;
}
</style>
