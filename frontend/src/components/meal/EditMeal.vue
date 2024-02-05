<template>
  <div>
    <div class="container" style="text-align: left">
      <b-button variant="link" v-on:click="goBackToPlan">{{this.$route.query.planTitle}}</b-button>\<b-button variant="link" v-on:click="goBack">{{this.$route.query.dateTitle}}</b-button>
    </div>
    <h4 class="meal-header">{{mealTitle}}</h4>
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
      <!--Dishes section-->
      <template v-if="mealDishes.length > 0" class="row">
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
        <div v-for="dish in mealDishes" :key="dish.id">
          <dish-view v-bind:dish="dish"
                     v-bind:go-back-path="goBackPath"
                     v-bind:manage-mode="true"
                     v-on:remove="removeDish"/>
        </div>
      </template>
      <!--Products section-->
      <template v-if="mealProducts.length > 0" class="row">
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
        <div v-for="product in mealProducts" :key="product.id">
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
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import SelectProductView from 'src/components/directory/product/SelectProductView'
import ProductView from 'src/components/directory/product/ProductView'
import DishView from 'src/components/directory/dish/DishView'
import SelectDishView from 'src/components/dish/SelectDishView.vue'

export default {
  name: 'EditMeal',
  components: {SelectDishView, SelectProductView, ProductView, DishView},
  data () {
    return {
      mealsEndpointUrl: '/api/plans/',
      mealTypesEndpointUrl: '/api/meal-types/',
      goBackPath: null,
      addProductsMode: false,
      productsTitle: 'Show Products to add',
      addDishesMode: false,
      dishesTitle: 'Show Dishes to add',
      mealTypes: [],
      mealTitle: null,
      mealTypeId: null,
      mealDescription: null,
      mealProducts: [],
      mealDishes: [],
      productCalorific: 0,
      productProteins: 0,
      productFats: 0,
      productCarbs: 0,
      productWeight: 0,
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
      axios.post(this.mealsEndpointUrl + this.$route.params.mealId + '/dishes/' + dishId)
        .then(response => {
          let newDish = response.data
          this.mealDishes.push(newDish)
          // this.updateProductsTotal()
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to add dish id - ' + dishId)
        })
    },

    removeDish (dishId) {
      this.mealDishes = this.mealDishes.filter(d => d.id !== dishId)
      // this.updateProductsTotal()
    },

    addProductsModeChange () {
      this.addProductsMode = !this.addProductsMode
      this.productsTitle = this.addProductsMode ? 'Hide Products to be added' : 'Show Products to add'
    },

    hideProductsToAdd () {
      this.addProductsMode = false
      this.productsTitle = 'Show Products to add'
    },

    addProduct (product) {
      if (this.mealProducts.find(p => p.id === product.id) === undefined) {
        this.mealProducts.push(product)
        this.updateProductsTotal()
      } else {
        console.log('Meal already contains product ' + JSON.stringify(product))
      }
    },

    removeProduct (productId) {
      this.mealProducts = this.mealProducts.filter(p => p.id !== productId)
      this.updateProductsTotal()
    },

    moveProductUp (productId) {
      const ndx = this.mealProducts.findIndex(p => p.id === productId)
      if (ndx > 0) {
        let product = this.mealProducts[ndx]
        this.mealProducts[ndx] = this.mealProducts[ndx - 1]
        this.mealProducts[ndx - 1] = product
        this.$forceUpdate()
      }
    },

    moveProductDown (productId) {
      const ndx = this.mealProducts.findIndex(p => p.id === productId)
      if (ndx < this.mealProducts.length - 1) {
        let product = this.mealProducts[ndx]
        this.mealProducts[ndx] = this.mealProducts[ndx + 1]
        this.mealProducts[ndx + 1] = product
        this.$forceUpdate()
      }
    },

    updateProductWeight (productId, weight) {
      let product = this.mealProducts.find(p => p.id === productId)
      if (product !== undefined) {
        product.weight = weight
        this.productWeight = this.calculateProductsTotal(this.mealProducts, p => Number(p.weight))
      }
    },

    calculateProductsTotal (products, propertyGetter) {
      let total = products.reduce((total, product) => propertyGetter(product) + total, 0)
      return total.toFixed(1)
    },

    updateProductsTotal () {
      this.productCalorific = this.calculateProductsTotal(this.mealProducts, p => Number(p.calorific))
      this.productProteins = this.calculateProductsTotal(this.mealProducts, p => Number(p.proteins))
      this.productFats = this.calculateProductsTotal(this.mealProducts, p => Number(p.fats))
      this.productCarbs = this.calculateProductsTotal(this.mealProducts, p => Number(p.carbs))
      this.productWeight = this.calculateProductsTotal(this.mealProducts, p => Number(p.weight))
    },

    mapProducts () {
      return this.mealProducts.map(p => {
        return {
          productId: p.id,
          weight: p.weight
        }
      })
    },

    mapDishes () {
      return this.mealDishes.map(d => d.id)
    },

    updateMeal () {
      if (this.mealTypeId > 0) {
        let newMeal = {
          id: this.$route.params.mealId,
          typeId: this.mealTypeId,
          description: this.mealDescription,
          products: this.mapProducts(),
          dishes: this.mapDishes()
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

    goBackToPlan () {
      this.$router.push({path: '/plan/' + this.$route.params.planId})
    },

    goBack () {
      this.$router.push({
        path: '/plan/' + this.$route.params.planId + '/day/' + this.$route.params.dayId,
        query: {planTitle: this.$route.query.planTitle}
      })
    },

    getMeal () {
      axios.get(this.mealsEndpointUrl + this.$route.params.mealId)
        .then(response => {
          let meal = response.data
          this.mealTitle = meal.type.name
          this.mealTypeId = meal.type.id
          this.mealDescription = meal.description
          this.mealDishes = meal.dishes
          this.mealProducts = meal.products
          this.productCalorific = meal.calorific
          this.productProteins = meal.proteins
          this.productFats = meal.fats
          this.productCarbs = meal.carbs
          this.productWeight = meal.weight
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
    this.goBackPath = {
      path: '/plan/' + this.$route.params.planId + '/day/' + this.$route.params.dayId + '/meal/' + this.$route.params.mealId,
      query: {
        planTitle: this.$route.query.planTitle,
        dateTitle: this.$route.query.dateTitle
      }
    }
  }
}
</script>

<style scoped>
.meal-header{
  text-align: center;
  color: #666600;
}
</style>
