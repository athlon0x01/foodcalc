<template>
  <div>
    <div class="container" style="text-align: left">
      <b-button variant="link" v-on:click="goBack">{{this.$route.query.planTitle}}</b-button>
    </div>
    <h3 class="food-day-header">Day {{dateTitle}}</h3>
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
      <!--Dishes section-->
      <template v-if="dayDishes.length > 0" class="row">
        <!--Header-->
        <h5 style="padding-top:10px">Dishes</h5>
        <div class="row headerRow bg-light">
          <div class="col-md-5 border"><strong>Name</strong></div>
          <div class="col-md-1 border"><strong>Calorific</strong></div>
          <div class="col-md-1 border"><strong>Proteins</strong></div>
          <div class="col-md-1 border"><strong>Fats</strong></div>
          <div class="col-md-1 border"><strong>Carbs</strong></div>
          <div class="col-md-1 border"><strong>Weight</strong></div>
        </div>
        <div v-for="dish in dayDishes" :key="'dayDish-' + dish.id">
          <dish-view v-bind:dish="dish"
                     v-bind:go-back-path="goBackPath"
                     v-bind:manage-mode="true"
                     v-bind:plan-id="planId"
                     v-on:remove="removeDish"/>
        </div>
      </template>
      <!--Products section-->
      <template v-if="dayProducts.length > 0" class="row">
        <!--Header-->
        <h5 style="padding-top:10px">Products</h5>
        <div class="row headerRow bg-light">
          <div class="col-md-5 border"><strong>Name</strong></div>
          <div class="col-md-1 border"><strong>Calorific</strong></div>
          <div class="col-md-1 border"><strong>Proteins</strong></div>
          <div class="col-md-1 border"><strong>Fats</strong></div>
          <div class="col-md-1 border"><strong>Carbs</strong></div>
          <div class="col-md-1 border"><strong>Weight</strong></div>
        </div>
        <div class="row headerRow bg-light">
          <div class="col-md-5 border"><em>Total</em></div>
          <div class="col-md-1 border"><em>{{ productCalorific }}</em></div>
          <div class="col-md-1 border"><em>{{ productProteins }}</em></div>
          <div class="col-md-1 border"><em>{{ productFats }}</em></div>
          <div class="col-md-1 border"><em>{{ productCarbs }}</em></div>
          <div class="col-md-1 border"><em>{{ productWeight }}</em></div>
        </div>
        <!--Content-->
        <div v-for="product in dayProducts" :key="'dayProduct-' + product.id">
          <product-view v-bind:product="product"
                        v-bind:select-mode="false"
                        v-bind:manage-mode="true"
                        v-on:productUp="moveProductUp"
                        v-on:productDown="moveProductDown"
                        v-on:weightUpdated="updateProductWeight"
                        v-on:productRemoved="removeProduct"/>
        </div>
      </template>
    </div>
    <b-button variant="link" size="sm" v-on:click="addDishesModeChange">{{ dishesTitle }}</b-button>
    <div v-if="addDishesMode !== undefined && addDishesMode" class="border border-dark">
      <select-dish-view v-on:hideDishes="hideDishesToAdd"
                        v-on:dishSelected="addDish"/>
    </div>
    <b-button variant="link" size="sm" v-on:click="addProductsModeChange">{{ productsTitle }}</b-button>
    <div v-if="addProductsMode !== undefined && addProductsMode" class="border border-dark">
      <select-product-view v-on:hideProducts="hideProductsToAdd"
                           v-on:productSelected="addProduct"/>
    </div>
    <!--day meals-->
    <template v-if="dayMeals.length > 0">
      <div v-for="meal in dayMeals" :key="meal.id">
        <meal-view v-bind:meal="meal"
                   v-bind:editable="true"
                   v-bind:plan-title="planTitle"
                   v-bind:date-title=dateTitle
                   v-on:remove="removeMeal"/>
      </div>
      <div style="padding-bottom: 5px"/>
    </template>
    <!--Add new meal section-->
    <b-button variant="link" size="sm" v-on:click="addMode = true">Add new meal</b-button>
    <div v-if="addMode" class="container">
      <div class="row">
        <div class="col-md-4"/>
        <div class="col-md-2 border bg-light"><em>Meal type</em></div>
        <div>
          <select v-model="typeId" name="typeId" style="width: 100%">
            <option v-for="mealType in mealTypes" :key="mealType.id" :value="mealType.id">
              {{ mealType.name }}
            </option>
          </select>
        </div>
      </div>
      <div class="row" style="padding-top:10px;">
        <div class="col-md-5"/>
        <div class="col-md-1">
          <b-button variant="outline-success" size="sm" v-on:click="addNewMeal">Add</b-button>
        </div>
        <div class="col-md-1">
          <b-button variant="outline-danger" size="sm" v-on:click="cancelAddMode">Cancel</b-button>
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
import MealView from 'src/components/meal/MealView'
import SelectProductView from 'src/components/directory/product/SelectProductView'
import ProductView from 'src/components/directory/product/ProductView'
import DishView from 'src/components/directory/dish/DishView'
import SelectDishView from 'src/components/dish/SelectDishView.vue'

export default {
  name: 'FoodDay',
  components: {SelectProductView, ProductView, MealView, DishView, SelectDishView},
  data () {
    return {
      planDayEndpointUrl: '/api/plans/',
      mealTypesEndpointUrl: '/api/meal-types/',
      planId: null,
      goBackPath: null,
      mealTypes: [],
      planTitle: null,
      dateTitle: null,
      dayDate: null,
      dayDescription: null,
      dayMeals: [],
      dayDishes: [],
      dayProducts: [],
      productCalorific: 0,
      productProteins: 0,
      productFats: 0,
      productCarbs: 0,
      productWeight: 0,
      addProductsMode: false,
      productsTitle: 'Show Products to add',
      addDishesMode: false,
      dishesTitle: 'Show Dishes to add',
      addMode: false,
      typeId: 0,
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

    addDishesModeChange () {
      this.addDishesMode = !this.addDishesMode
      this.dishesTitle = this.addDishesMode ? 'Hide Dishes to be added' : 'Show Dishes to add'
    },

    hideDishesToAdd () {
      this.addDishesMode = false
      this.dishesTitle = 'Show Dishes to add'
    },

    addDish (dishId) {
      axios.post(this.planDayEndpointUrl + this.$route.params.dayId + '/dishes/' + dishId)
        .then(response => {
          let newDish = response.data
          this.dayDishes.push(newDish)
          // this.updateProductsTotal()
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to add dish id - ' + dishId)
        })
    },

    removeDish (dishId) {
      axios.delete(this.planDayEndpointUrl + this.$route.params.dayId + '/dishes/' + dishId)
        .then(() => {
          this.dayDishes = this.dayDishes.filter(d => d.id !== dishId)
          // this.updateProductsTotal()
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to delete dish id - ' + dishId)
        })
    },

    addProductsModeChange () {
      this.addProductsMode = !this.addProductsMode
      this.productsTitle = this.addProductsMode ? 'Hide Products to be added' : 'Show Products to add to day'
    },

    hideProductsToAdd () {
      this.addProductsMode = false
      this.productsTitle = 'Show Products to add to day'
    },

    addProduct (product) {
      if (this.dayProducts.find(p => p.id === product.id) === undefined) {
        this.dayProducts.push(product)
        this.updateProductsTotal()
      } else {
        console.log('Day already contains ' + JSON.stringify(product))
      }
    },

    removeProduct (productId) {
      this.dayProducts = this.dayProducts.filter(p => p.id !== productId)
      this.updateProductsTotal()
    },

    moveProductUp (productId) {
      const ndx = this.dayProducts.findIndex(p => p.id === productId)
      if (ndx > 0) {
        let product = this.dayProducts[ndx]
        this.dayProducts[ndx] = this.dayProducts[ndx - 1]
        this.dayProducts[ndx - 1] = product
        this.$forceUpdate()
      }
    },

    moveProductDown (productId) {
      const ndx = this.dayProducts.findIndex(p => p.id === productId)
      if (ndx < this.dayProducts.length - 1) {
        let product = this.dayProducts[ndx]
        this.dayProducts[ndx] = this.dayProducts[ndx + 1]
        this.dayProducts[ndx + 1] = product
        this.$forceUpdate()
      }
    },

    updateProductWeight (productId, weight) {
      let product = this.dayProducts.find(p => p.id === productId)
      if (product !== undefined) {
        product.weight = weight
        this.productWeight = this.calculateProductsTotal(this.dayProducts, p => Number(p.weight))
      }
    },

    calculateProductsTotal (products, propertyGetter) {
      let total = products.reduce((total, product) => propertyGetter(product) + total, 0)
      return total.toFixed(1)
    },

    updateProductsTotal () {
      this.productCalorific = this.calculateProductsTotal(this.dayProducts, p => Number(p.calorific))
      this.productProteins = this.calculateProductsTotal(this.dayProducts, p => Number(p.proteins))
      this.productFats = this.calculateProductsTotal(this.dayProducts, p => Number(p.fats))
      this.productCarbs = this.calculateProductsTotal(this.dayProducts, p => Number(p.carbs))
      this.productWeight = this.calculateProductsTotal(this.dayProducts, p => Number(p.weight))
    },

    mapProducts () {
      return this.dayProducts.map(p => {
        return {
          productId: p.id,
          weight: p.weight
        }
      })
    },

    mapDishes () {
      return this.dayDishes.map(d => d.id)
    },

    mapMeals () {
      return this.dayMeals.map(m => m.id)
    },

    updateDay () {
      if (this.dayDate != null) {
        let newDateObj = new Date(this.dayDate)
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
          id: this.$route.params.dayId,
          date: newDateString,
          description: this.dayDescription,
          products: this.mapProducts(),
          dishes: this.mapDishes(),
          meals: this.mapMeals()
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
    },

    addNewMeal () {
      if (this.typeId > 0) {
        let newMeal = {
          typeId: this.typeId
        }
        axios.post(this.planDayEndpointUrl + this.$route.params.dayId + '/meals/', newMeal)
          .then(() => {
            this.addMode = false
            this.errorMessage = null
            this.getFoodDay()
          })
          .catch(e => {
            this.getErrorMessage(e, 'Failed to add new meal with typeId ' + this.typeId)
          })
      } else {
        this.errorMessage = 'Please define proper meal type'
      }
    },

    cancelAddMode () {
      this.addMode = false
      this.errorMessage = null
    },

    removeMeal (mealId) {
      axios.delete(this.planDayEndpointUrl + this.$route.params.dayId + '/meals/' + mealId)
        .then(() => {
          this.dayMeals = this.dayMeals.filter(meal => meal.id !== mealId)
          this.errorMessage = null
        })
        .catch(e => {
          let dayMeal = this.dayMeals.find(meal => meal.id === mealId)
          this.getErrorMessage(e, 'Failed to delete ' + dayMeal.type)
        })
    },

    getFoodDay () {
      axios.get(this.planDayEndpointUrl + this.$route.params.dayId)
        .then(response => {
          let planDay = response.data
          this.dateTitle = planDay.date
          this.dayDescription = planDay.description
          this.dayMeals = planDay.meals
          let dateParts = planDay.date.split('-')
          this.dayDate = dateParts[2] + '-' + dateParts[1] + '-' + dateParts[0]
          this.dayDishes = planDay.dishes
          this.dayProducts = planDay.products
          this.productCalorific = planDay.calorific
          this.productProteins = planDay.proteins
          this.productFats = planDay.fats
          this.productCarbs = planDay.carbs
          this.productWeight = planDay.weight
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to load Food plan day...')
        })
    }
  },

  mounted () {
    this.planTitle = this.$route.query.planTitle
    this.planDayEndpointUrl = '/api/plans/' + this.$route.params.planId + '/days/'
    // load food plan day full preview on page init
    this.getFoodDay()
    // load meal types for adding new meals
    axios.get(this.mealTypesEndpointUrl)
      .then(response => {
        this.mealTypes = response.data
      })
      .catch(e => {
        this.getErrorMessage(e, 'Failed to load Meal types...')
      })
    this.goBackPath = {
      path: '/plan/' + this.$route.params.planId + '/day/' + this.$route.params.dayId,
      query: {
        planTitle: this.$route.query.planTitle
      }
    }
    this.planId = this.$route.params.planId
  }
}
</script>

<style scoped>
.food-day-header{
  text-align: center;
  color: #445588;
}
</style>
