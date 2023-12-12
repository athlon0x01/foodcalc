<template>
  <div>
    <template v-if="this.addMode">
      <h3 class="food-day-header">New Day will be here</h3>
    </template>
    <template v-else>
      <h3 class="food-day-header">{{planDate}}</h3>
    </template>
    <div class="container">
      <div class="row justify-content-md-center" style="padding-bottom:5px">
        <div class="col-md-2">
          <b-button variant="outline-success" size="sm" v-on:click="applyChanges">Do it!</b-button>
        </div>
        <div class="col-md-4"/>
        <div class="col-md-2">
          <b-button variant="outline-danger" size="sm" v-on:click="goBack">Cancel</b-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'FoodDay',
  data () {
    return {
      planDayEndpointUrl: '/api/plans/',
      addMode: true,
      planDate: null,
      errorMessage: null
    }
  },

  methods: {
    applyChanges () {
      console.log('Will do later')
    },

    goBack () {
      this.$router.push({path: '/plan/' + this.$route.params.planId})
    }
  },

  mounted () {
    console.log('food day id - ' + this.$route.params.dayId)
    this.addMode = this.$route.params.dayId === undefined
    this.planDayEndpointUrl = '/api/plans/' + this.$route.params.planId + '/days/'
    if (!this.addMode) {
      // load food plan full preview on page init
      axios.get(this.planDayEndpointUrl + this.$route.params.dayId)
        .then(response => {
          let planDay = response.data
          this.planDate = planDay.date
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to load Food plan day...')
        })
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
