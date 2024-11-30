<template>
  <div>
    <h2 class="food-plan-header">Food plan</h2>
    <template v-if="foodPlan !== null">
      <div class="container">
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
            <textarea v-model="foodPlan.description" name="planDescription" style="width: 100%"/>
          </div>
          <div class="col-md-1">
            <b-button variant="outline-success" size="sm" v-on:click="updatePlanInfo">Update Plan</b-button>
          </div>
        </div>
        <div class="row">
          <div class="col-md-2 border bg-light"><strong>Duration</strong></div>
          <div class="col-md-9 border">{{foodPlan.days.length}}</div>
          <div class="col-md-1">
            <b-button variant="outline-primary" size="sm" v-on:click="goToPackages">Packages</b-button>
          </div>
        </div>
        <div class="row">
          <div class="col-md-2 border bg-light"><strong>Created On</strong></div>
          <div class="col-md-9 border">{{foodPlan.createdOn}}</div>
        </div>
        <div class="row">
          <div class="col-md-2 border bg-light"><strong>Last Updated</strong></div>
          <div class="col-md-9 border">{{foodPlan.lastUpdated}}</div>
          <div class="col-md-1">
            <b-button variant="outline-primary" size="sm" v-on:click="exportPlan">Export</b-button>
          </div>
        </div>
        <div>
          <hikers-view v-bind:hikers="foodPlan.members" />
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
          <div class="col-md-2 border"><em><strong>Total</strong></em></div>
          <div class="col-md-2 border"><em><strong>{{foodPlan.calorific}}</strong></em></div>
          <div class="col-md-2 border"><em><strong>{{foodPlan.proteins}}</strong></em></div>
          <div class="col-md-2 border"><em><strong>{{foodPlan.fats}}</strong></em></div>
          <div class="col-md-2 border"><em><strong>{{foodPlan.carbs}}</strong></em></div>
          <div class="col-md-2 border"><em><strong>{{foodPlan.weight}}</strong></em></div>
        </div>
      </div>
      <template v-if="foodPlan.days.length > 0">
        <!--Food plan days-->
        <div v-for="foodDay in foodPlan.days" :key="'day-' + foodDay.id">
          <food-day-view v-bind:food-day="foodDay"
                         v-bind:plan-title="foodPlan.name"
                         v-on:remove="removeFoodDay"/>
        </div>
      </template>
      <!--Add new food plan day section-->
      <b-button variant="link" size="sm" v-on:click="addMode = true">Add new day</b-button>
      <div v-if="addMode" class="container">
        <div class="row">
          <div class="col-md-4"/>
          <div class="col-md-2 border bg-light"><em>Date</em></div>
          <div><input type="date" v-model="newDayDate" name="newDayDate"/></div>
        </div>
        <div class="row" style="padding-top:10px;">
          <div class="col-md-5"/>
          <div class="col-md-1">
            <b-button variant="outline-success" size="sm" v-on:click="addNewDay">Add</b-button>
          </div>
          <div class="col-md-1">
            <b-button variant="outline-danger" size="sm" v-on:click="cancelAddMode">Cancel</b-button>
          </div>
        </div>
      </div>
    </template>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import saveAs from 'file-saver'
import FoodDayView from 'src/components/plan/FoodDayView'
import HikersView from 'src/components/plan/HikersView.vue'

export default {
  name: 'FoodPlan',
  components: {FoodDayView, HikersView},
  data () {
    return {
      foodPlansEndpointUrl: '/api/plans/',
      foodPlan: null,
      addMode: false,
      newDayDate: null,
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

    mapDays () {
      return this.foodPlan.days.map(d => d.id)
    },

    goToPackages () {
      this.$router.push({path: '/plan/' + this.$route.params.planId + '/packages'})
    },

    exportPlan () {
      axios.get(this.foodPlansEndpointUrl + this.$route.params.planId + '/export',
        {responseType: 'blob'})
        .then((response) => {
          // extracting filename from the headers
          let contentDisposition = response.headers['content-disposition']
          let startFileNameIndex = contentDisposition.indexOf('"') + 1
          let endFileNameIndex = contentDisposition.lastIndexOf('"')
          let filename = contentDisposition.substring(startFileNameIndex, endFileNameIndex)
          saveAs(new Blob([response.data], {type: 'application/octet-stream'}), filename, {autoBOM: false})
          this.errorMessage = null
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to export food plan ' + JSON.stringify(this.foodPlan))
        })
    },

    updatePlanInfo () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let newPlan = {
            id: this.foodPlan.id,
            name: this.foodPlan.name,
            description: this.foodPlan.description,
            duration: this.foodPlan.duration,
            days: this.mapDays()
          }
          axios.put(this.foodPlansEndpointUrl + this.$route.params.planId, newPlan)
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
    },

    addNewDay () {
      if (this.newDayDate != null) {
        let newDateObj = new Date(this.newDayDate)
        let day = Number(newDateObj.getDate())
        if (newDateObj.getDate() < 9) {
          day = '0' + day
        }
        let month = Number(newDateObj.getMonth() + 1)
        if (newDateObj.getMonth() < 9) {
          month = '0' + month
        }
        let newDateString = day + '-' + month + '-' + newDateObj.getFullYear()
        let planDay = {
          date: newDateString
        }
        axios.post(this.foodPlansEndpointUrl + this.$route.params.planId + '/days/', planDay)
          .then(() => {
            this.addMode = false
            this.errorMessage = null
            this.getFoodPlan()
          })
          .catch(e => {
            this.getErrorMessage(e, 'Failed to add food plan day ' + JSON.stringify(planDay))
          })
      } else {
        this.errorMessage = 'Please define proper date'
      }
    },

    cancelAddMode () {
      this.addMode = false
      this.errorMessage = null
    },

    removeFoodDay (dayId) {
      axios.delete(this.foodPlansEndpointUrl + this.$route.params.planId + '/days/' + dayId)
        .then(() => {
          this.foodPlan.days = this.foodPlan.days.filter(day => day.id !== dayId)
          this.errorMessage = null
        })
        .catch(e => {
          let foodDay = this.foodPlan.days.find(day => day.id === dayId)
          this.getErrorMessage(e, 'Failed to delete ' + foodDay.date)
        })
    },

    getFoodPlan () {
      axios.get(this.foodPlansEndpointUrl + this.$route.params.planId)
        .then(response => {
          this.foodPlan = response.data
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to load Food plan...')
        })
    }
  },

  mounted () {
    // load food plan full preview on page init
    this.getFoodPlan()
  }
}
</script>

<style scoped>

</style>
