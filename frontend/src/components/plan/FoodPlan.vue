<template>
  <div>
    <h2 class="food-plan-header">Food plan</h2>
    <div v-if="foodPlan !== null" class="container">
      <div class="row">
        <div class="col-md-2 border bg-light"><strong>Name</strong></div>
        <div class="col-md-9 border">
          <input v-validate="'required'" v-model="foodPlan.name" name="planName"
                 v-bind:class="{ validationError: errors.has('planName')}"
                 placeholder='Enter food plan name here..' style="width: 100%"/>
          <p v-if="errors.has('planName') > 0" class="alert">{{errors.first('planName')}}</p>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2 border bg-light"><strong>Description</strong></div>
        <div class="col-md-9 border">
          <input v-model="foodPlan.description" name="planDescription" style="width: 100%"/>
        </div>
        <div class="col-md-1">
          <b-button variant="outline-success" size="sm" v-on:click="updatePlanInfo">Update Info</b-button>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2 border bg-light"><strong>Members</strong></div>
        <div class="col-md-9 border">
          <input type="number" min="0" step="1" v-model="foodPlan.members" name="planMembers"/>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2 border bg-light"><strong>Duration</strong></div>
        <div class="col-md-9 border">{{foodPlan.duration}}</div>
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
    },

    updatePlanInfo () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          axios.put(this.foodPlansEndpointUrl + this.$route.params.planId, this.foodPlan)
            .then(() => {
              this.errorMessage = null
            })
            .catch(e => {
              this.getErrorMessage(e, 'Failed to update food plan Info ' + JSON.stringify(this.foodPlan))
            })
        } else {
          console.log('Couldn\'t update food plan info due to validation errors')
        }
      })
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
