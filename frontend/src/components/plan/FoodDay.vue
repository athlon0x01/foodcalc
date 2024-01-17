<template>
  <div>
    <h3 class="food-day-header">Food Plan Day {{dateTitle}}</h3>
    <div class="container">
      <div class="row">
        <div class="col-md-2 border bg-light"><strong>Date</strong></div>
        <div><input type="date" v-model="dayDate" name="dayDate"/></div>
      </div>
      <div class="row">
        <div class="col-md-2 border bg-light"><strong>Description</strong></div>
        <div class="col-md-10 border">
          <textarea v-model="dayDescription" name="dayDescription" style="width: 100%"/>
        </div>
      </div>
      <div class="row justify-content-md-center" style="padding-top:5px">
        <div class="col-md-2">
          <b-button variant="outline-success" size="sm" v-on:click="updateDay">Update</b-button>
        </div>
        <div class="col-md-4"/>
        <div class="col-md-2">
          <b-button variant="outline-danger" size="sm" v-on:click="goBack">Cancel</b-button>
        </div>
      </div>
      <!--day meals-->
      <template v-if="dayMeals.length > 0">
        <div v-for="meal in dayMeals" :key="meal.id">
          <meal-view v-bind:meal="meal"
                     v-bind:editable="true"/>
        </div>
        <div style="padding-bottom: 5px"/>
      </template>
    </div>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import MealView from 'src/components/meal/MealView'

export default {
  name: 'FoodDay',
  components: {MealView},
  data () {
    return {
      planDayEndpointUrl: '/api/plans/',
      dateTitle: null,
      dayDate: null,
      dayDescription: null,
      dayMeals: [],
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

    updateDay () {
      if (this.dayDate != null) {
        let newDateObj = new Date(this.dayDate)
        let newDateString = newDateObj.getDate() + '-' + Number(newDateObj.getMonth() + 1) + '-' + newDateObj.getFullYear()
        let planDay = {
          date: newDateString,
          description: this.dayDescription
        }
        axios.put(this.planDayEndpointUrl + this.$route.params.dayId, planDay)
          .then(() => {
            this.goBack()
          })
          .catch(e => {
            this.getErrorMessage(e, 'Failed to update food plan day ' + JSON.stringify(planDay))
          })
      } else {
        this.errorMessage = 'Please define proper date'
      }
    },

    goBack () {
      this.$router.push({path: '/plan/' + this.$route.params.planId})
    }
  },

  mounted () {
    this.planDayEndpointUrl = '/api/plans/' + this.$route.params.planId + '/days/'
    // load food plan day full preview on page init
    axios.get(this.planDayEndpointUrl + this.$route.params.dayId)
      .then(response => {
        let planDay = response.data
        this.dateTitle = planDay.date
        this.dayDescription = planDay.description
        this.dayMeals = planDay.meals
        let dateParts = planDay.date.split('-')
        this.dayDate = dateParts[2] + '-' + dateParts[1] + '-' + dateParts[0]
      })
      .catch(e => {
        this.getErrorMessage(e, 'Failed to load Food plan day...')
      })
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
