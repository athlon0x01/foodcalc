<template>
  <div>
    <div class="container" style="text-align: left">
      <b-button variant="link" v-on:click="goBack">Back to plan</b-button>
    </div>
    <h3 class="food-plan-header">Food packages</h3>
    <div v-if="packages.length > 0" class="container">
      <!--Header-->
      <div class="row headerRow bg-light">
        <div class="col-md-3 border"><strong>Name</strong></div>
        <div class="col-md-5 border"><strong>Description</strong></div>
        <div class="col-md-1 border"><strong>Volume</strong></div>
        <div class="col-md-1 border"><strong>Weight</strong></div>
      </div>
      <div v-for="foodPackage in packages" :key="foodPackage.id">
        <food-package-item v-bind:food-package="foodPackage"
                           v-on:update="updateFoodPackage"
                           v-on:remove="removeFoodPackage"/>
      </div>
    </div>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>

    <!--Add new food package section-->
    <b-button variant="link" size="sm" v-on:click="addMode = !addMode">Add new</b-button>
    <div v-if="addMode !== undefined && addMode" class="container">
      <div class="row">
        <div class="col-md-4"/>
        <div class="col-md-2 border bg-light"><strong>Name</strong></div>
        <div>
          <input v-validate="'required'" v-model="newPackageName" name="newPackage"
                 v-bind:class="{ validationError: errors.has('newPackage')}"
                 placeholder='Enter member name here..' style="width: 100%"/>
          <p v-if="errors.has('newPackage') > 0" class="alert">{{errors.first('newPackage')}}</p>
        </div>
      </div>
      <div class="row">
        <div class="col-md-4"/>
        <div class="col-md-2 border bg-light"><strong>Volume Coefficient</strong></div>
        <div>
          <input type="number" min="0" step="0.1" v-model="newPackageVolume" name="packageVolume" style="width: 100%"/>
        </div>
      </div>
      <div class="row">
        <div class="col-md-4"/>
        <div class="col-md-2 border bg-light"><strong>Additional Weight</strong></div>
        <div>
          <input type="number" min="0" step="0.1" v-model="newPackageWeight" name="packageWeight" style="width: 100%"/>
        </div>
      </div>
      <div class="row" style="padding-top:10px;padding-bottom:15px">
        <div class="col-md-5"/>
        <div class="col-md-1">
          <b-button variant="outline-success" size="sm" v-on:click="addNewPackage">Add</b-button>
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
import FoodPackageItem from 'src/components/plan/pack/FoodPackageItem'

export default {
  name: 'FoodPackageView',
  components: {FoodPackageItem},
  data () {
    return {
      foodPackagesEndpointUrl: '/api/plans/',
      addMode: false,
      newPackageName: null,
      newPackageVolume: 1.0,
      newPackageWeight: 10,
      packages: [],
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

    goBack () {
      this.$router.push({path: '/plan/' + this.$route.params.planId})
    },

    updateFoodPackage (foodPackage) {
      axios.put(this.foodPackagesEndpointUrl + foodPackage.id, foodPackage)
        .then(() => {
          this.errorMessage = null
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to update food package ' + JSON.stringify(foodPackage))
        })
    },

    removeFoodPackage (packageId) {
      axios.delete(this.foodPackagesEndpointUrl + packageId)
        .then(() => {
          this.packages = this.packages.filter(foodPack => foodPack.id !== packageId)
          this.errorMessage = null
        })
        .catch(e => {
          let foodPack = this.packages.find(thePack => thePack.id === packageId)
          this.getErrorMessage(e, 'Failed to delete ' + foodPack.name)
        })
    },

    addNewPackage () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let newPackage = {
            name: this.newPackageName,
            volumeCoefficient: this.newPackageVolume,
            additionalWeight: this.newPackageWeight
          }
          axios.post(this.foodPackagesEndpointUrl, newPackage)
            .then(response => {
              console.log('New food package - ' + JSON.stringify(response.data))
              this.addMode = false
              this.errorMessage = null
              this.newPackageName = ''
              this.newPackageVolume = 1.0
              this.newPackageWeight = 10
              this.packages.push(response.data)
            })
            .catch(e => {
              this.getErrorMessage(e, 'Failed to add food package ' + JSON.stringify(newPackage))
            })
        } else {
          console.log('Couldn\'t add new food package due to validation errors')
        }
      })
    }
  },

  mounted () {
    this.foodPackagesEndpointUrl = '/api/plans/' + this.$route.params.planId + '/packages/'
    // load food plan packages on page init
    axios.get(this.foodPackagesEndpointUrl)
      .then(response => {
        this.packages = response.data
      })
      .catch(e => {
        this.getErrorMessage(e, 'Failed to load Food plan.packages..')
      })
  }
}
</script>

<style scoped>

</style>
