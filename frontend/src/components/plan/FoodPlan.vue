<template>
  <div>
    <h2 class="food-plan-header">Food plan</h2>
    <div v-if="foodPlan !== null" class="container">
      <div class="row">
        <div class="col-md-2 border bg-light"><strong>Name</strong></div>
        <div class="col-md-10 border">{{foodPlan.name}}</div>
      </div>
      <div class="row">
        <div class="col-md-2 border bg-light"><strong>Members</strong></div>
        <div class="col-md-10 border">{{foodPlan.members}}</div>
      </div>
      <div class="row">
        <div class="col-md-2 border bg-light"><strong>Duration</strong></div>
        <div class="col-md-10 border">{{foodPlan.duration}}</div>
      </div>
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
  name: 'FoodPlan',

  data () {
    return {
      foodPlansEndpointUrl: '/api/plans/',
      foodPlan: null,
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
    }
  },

  mounted () {
    // load food plan full preview on page init
    axios.get(this.foodPlansEndpointUrl + this.$route.params.planId)
      .then(response => {
        this.foodPlan = response.data
      })
      .catch(e => {
        this.getErrorMessage(e, 'Failed to load Food plan...')
      })
  }
}
</script>

<style scoped>

</style>
