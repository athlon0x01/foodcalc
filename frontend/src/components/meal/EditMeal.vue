<template>
  <div>
    <h4 class="meal-header">Day {{this.$route.query.dateTitle}} {{mealTitle}}</h4>
    <div class="container">
      <div class="row">
        <div class="col-md-2 border bg-light"><strong>Date</strong></div>
        <div>
          <select v-model="mealTypeId" name="typeId" style="width: 100%">
            <option v-for="mealType in mealTypes" :key="mealType.id" :value="mealType.id">
              {{ mealType.name }}
            </option>
          </select>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2 border bg-light"><strong>Description</strong></div>
        <div class="col-md-10 border">
          <textarea v-model="mealDescription" name="dayDescription" style="width: 100%"/>
        </div>
      </div>
      <div class="row justify-content-md-center" style="padding-top:5px">
        <div class="col-md-2">
          <b-button variant="outline-success" size="sm" v-on:click="updateMeal">Update</b-button>
        </div>
        <div class="col-md-4"/>
        <div class="col-md-2">
          <b-button variant="outline-danger" size="sm" v-on:click="goBack">Cancel</b-button>
        </div>
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
  name: 'EditMeal',
  data () {
    return {
      mealsEndpointUrl: '/api/plans/',
      mealTypesEndpointUrl: '/api/meal-types/',
      mealTypes: [],
      mealTitle: null,
      mealTypeId: null,
      mealDescription: null,
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

    updateMeal () {
      if (this.mealTypeId > 0) {
        let newMeal = {
          id: this.$route.params.mealId,
          typeId: this.mealTypeId,
          description: this.mealDescription
        }
        axios.put(this.mealsEndpointUrl + this.$route.params.mealId, newMeal)
          .then(() => {
            this.goBack()
          })
          .catch(e => {
            this.getErrorMessage(e, 'Failed to update meal with typeId ' + this.mealTypeId)
          })
      } else {
        this.errorMessage = 'Please define proper meal type'
      }
    },

    goBack () {
      this.$router.push({path: '/plan/' + this.$route.params.planId + '/day/' + this.$route.params.dayId})
    },

    getMeal () {
      axios.get(this.mealsEndpointUrl + this.$route.params.mealId)
        .then(response => {
          let meal = response.data
          this.mealTitle = meal.type.name
          this.mealTypeId = meal.type.id
          this.mealDescription = meal.description
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to load Meal...')
        })
    }
  },

  mounted () {
    this.mealsEndpointUrl = '/api/plans/' + this.$route.params.planId + '/days/' + this.$route.params.dayId + '/meals/'
    // load Meal full preview on page init
    this.getMeal()
    // load meal types for adding new meals
    axios.get(this.mealTypesEndpointUrl)
      .then(response => {
        this.mealTypes = response.data
      })
      .catch(e => {
        this.getErrorMessage(e, 'Failed to load Meal types...')
      })
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
