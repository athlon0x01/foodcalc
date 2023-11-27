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
    <!--Add new food plan section-->
    <b-button variant="link" size="sm" v-on:click="addMode = !addMode">Add new</b-button>
    <div v-if="addMode !== undefined && addMode" class="container">
      <div class="row">
        <div class="col-md-4"/>
        <div class="col-md-2 border bg-light"><strong>Name</strong></div>
        <div>
          <input v-validate="'required'" v-model="planName" name="planName"
                 v-bind:class="{ validationError: errors.has('planName')}"
                 placeholder='Enter food plan name here..' style="width: 100%"/>
          <p v-if="errors.has('planName') > 0" class="alert">{{errors.first('planName')}}</p>
        </div>
      </div>
      <div class="row">
        <div class="col-md-4"/>
        <div class="col-md-2 border bg-light"><strong>Members</strong></div>
        <input type="number" min="0" step="1" v-model="planMembers" name="planMembers"/>
      </div>
      <div class="row" style="padding-top:10px;">
        <div class="col-md-5"/>
        <div class="col-md-1">
          <b-button variant="outline-success" size="sm" v-on:click="addNewPlan">Add</b-button>
        </div>
        <div class="col-md-1">
          <b-button variant="outline-danger" size="sm" v-on:click="addMode = false">Cancel</b-button>
        </div>
      </div>
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
      addMode: false,
      planName: '',
      planMembers: 2,
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

    addNewPlan () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let foodPlan = {
            name: this.planName,
            members: this.planMembers
          }
          axios.post(this.foodPlansEndpointUrl, foodPlan)
            .then(() => {
              this.addMode = false
              this.errorMessage = null
              this.planName = ''
              this.planMembers = 2
              this.getAllFoodPlans()
            })
            .catch(e => {
              this.getErrorMessage(e, 'Failed to add food plan ' + JSON.stringify(foodPlan))
            })
        } else {
          console.log('Couldn\'t add new food plan due to validation errors')
        }
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
