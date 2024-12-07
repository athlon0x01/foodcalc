<template>
  <div>
    <h2 class="directory-header">{{ pageTitle }}</h2>
    <div class="container">
      <div class="row">
        <div class="col-md-9">
          <input v-validate="'required'" v-model="name" name="name"
                 v-bind:class="{ validationError: errors.has('name')}"
                 placeholder='Введіть назву тут..' style="width: 100%"/>
          <p v-if="errors.has('name') > 0" class="alert">{{errors.first('name')}}</p>
        </div>
        <div class="col-md-3" style="margin-top: 3px">
          <select v-validate="'min_value:1'" v-model="categoryId" name="categoryId" style="width: 100%"
                  v-bind:class="{ validationError: errors.has('categoryId')}">
            <option v-for="category in dishCategories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
          <p v-if="errors.has('categoryId') > 0" class="alert">{{errors.first('categoryId')}}</p>
        </div>
      </div>
      <div class="row" style="margin-top: 3px">
        <div class="col-md-2" style="margin-top: 10px"><strong>Опис</strong></div>
        <div class="col-md-10">
          <textarea v-model="description" name="dishDescription" style="width: 100%"/>
        </div>
      </div>
      <!--Products section-->
      <template v-if="products.length > 0" class="row">
        <h4 style="padding-top:10px">Продукти</h4>
        <!-- Dish linked to some food plan -->
        <div class="row headerRow bg-light">
          <!--Header-->
          <div class="col-md-3 border"><strong>Назва</strong></div>
          <div class="col-md-1 border"><strong>Калорії</strong></div>
          <div class="col-md-1 border"><strong>Протеіни</strong></div>
          <div class="col-md-1 border"><strong>Жири</strong></div>
          <div class="col-md-1 border"><strong>Вуглвд.</strong></div>
          <div class="col-md-1 border"><strong>Вага</strong></div>
          <div class="col-md-2 border"><strong>Пакунок</strong></div>
        </div>
        <div class="row headerRow bg-light">
          <div class="col-md-3 border"><em>Загалом</em></div>
          <div class="col-md-1 border"><em>{{ totalCalorific }}</em></div>
          <div class="col-md-1 border"><em>{{ totalProteins }}</em></div>
          <div class="col-md-1 border"><em>{{ totalFats }}</em></div>
          <div class="col-md-1 border"><em>{{ totalCarbs }}</em></div>
          <div class="col-md-1 border"><em>{{ totalWeight }}</em></div>
        </div>
        <!--Content-->
        <div v-for="product in products" :key="product.id">
          <product-with-package-view v-bind:product="product"
                                     v-bind:food-packages="foodPackages"
                                     v-bind:manage-mode="true"
                                     v-on:productUp="moveProductUp"
                                     v-on:productDown="moveProductDown"
                                     v-on:weightUpdated="updateProductWeight"
                                     v-on:packageIdUpdated="updateProductPackageId"
                                     v-on:productRemoved="removeProduct"/>
        </div>
      </template>
      <b-button variant="link" size="sm" v-on:click="addProductsModeChange">{{ productsTitle }}</b-button>
      <div v-if="addProductsMode !== undefined && addProductsMode" class="border border-dark">
        <select-product-view v-on:hideProducts="hideProductsToAdd"
                             v-on:productSelected="addProduct"/>
      </div>
      <div style="margin-top: 15px">
        <b-button variant="outline-success" size="sm" v-on:click="addNewDish">{{ addButtonTitle }}</b-button>
        <b-button variant="outline-danger" size="sm" v-on:click="goBack" >Назад</b-button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import SelectProductView from 'src/components/directory/product/SelectProductView'
import ProductView from 'src/components/directory/product/ProductView'
import ProductWithPackageView from 'src/components/directory/product/ProductWithPackageView'

export default {
  name: 'EditDish',
  components: {SelectProductView, ProductView, ProductWithPackageView},
  props: {
    oldDish: {
      type: Object,
      required: false
    },
    goBackPath: {
      type: Object,
      required: false
    },
    planId: {
      type: String,
      required: false
    }
  },

  data () {
    let initialProducts = this.editMode() ? this.oldDish.products : []
    return {
      dishesEndpointUrl: '/api/dishes/',
      dishCategoriesEndpointUrl: '/api/dish-categories/',
      dishCategories: [],
      foodPackages: [],
      addProductsMode: false,
      pageTitle: this.editMode() ? 'Редагувати страву' : 'Додати страву',
      productsTitle: 'Додати продукти',
      addButtonTitle: this.editMode() ? 'Змін.' : 'Додати',
      dishId: this.editMode() ? this.oldDish.id : -1,
      name: this.editMode() ? this.oldDish.name : '',
      categoryId: this.editMode() ? this.oldDish.categoryId : 0,
      description: this.editMode() ? this.oldDish.description : '',
      products: initialProducts,
      totalCalorific: this.calculateProductsTotal(initialProducts, p => Number(p.calorific)),
      totalProteins: this.calculateProductsTotal(initialProducts, p => Number(p.proteins)),
      totalFats: this.calculateProductsTotal(initialProducts, p => Number(p.fats)),
      totalCarbs: this.calculateProductsTotal(initialProducts, p => Number(p.carbs)),
      totalWeight: this.calculateProductsTotal(initialProducts, p => Number(p.weight)),
      errorMessage: null
    }
  },

  methods: {
    editMode () {
      return this.oldDish !== undefined
    },

    getGoBackPath () {
      if (this.goBackPath !== undefined) {
        return this.goBackPath
      } else {
        return {name: 'DishesPage'}
      }
    },

    addProductsModeChange () {
      this.addProductsMode = !this.addProductsMode
      this.productsTitle = this.addProductsMode ? 'Приховати продукти' : 'Додати продукти'
    },

    hideProductsToAdd () {
      this.addProductsMode = false
      this.productsTitle = 'Додати продукти'
    },

    addProduct (product) {
      if (this.products.find(p => p.id === product.id) === undefined) {
        this.products.push(product)
        this.updateProductsTotal()
      } else {
        console.log('Dish already contains ' + JSON.stringify(product))
      }
    },

    removeProduct (productId) {
      this.products = this.products.filter(p => p.id !== productId)
      this.updateProductsTotal()
    },

    moveProductUp (productId) {
      const ndx = this.products.findIndex(p => p.id === productId)
      if (ndx > 0) {
        let product = this.products[ndx]
        this.products[ndx] = this.products[ndx - 1]
        this.products[ndx - 1] = product
        this.$forceUpdate()
      }
    },

    moveProductDown (productId) {
      const ndx = this.products.findIndex(p => p.id === productId)
      if (ndx < this.products.length - 1) {
        let product = this.products[ndx]
        this.products[ndx] = this.products[ndx + 1]
        this.products[ndx + 1] = product
        this.$forceUpdate()
      }
    },

    updateProductWeight (productId, weight) {
      let product = this.products.find(p => p.id === productId)
      if (product !== undefined) {
        product.weight = weight
        this.totalWeight = this.calculateProductsTotal(this.products, p => Number(p.weight))
      }
    },

    updateProductPackageId (productId, packageId) {
      let product = this.products.find(p => p.id === productId)
      if (product !== undefined) {
        product.packageId = packageId
      }
    },

    getErrorMessage (error, defaultMessage) {
      if (error.response !== undefined && error.response.data !== undefined &&
        (typeof error.response.data === 'string' || error.response.data instanceof String)) {
        this.errorMessage = error.response.data
      } else {
        console.log(error)
        this.errorMessage = defaultMessage
      }
    },

    clearErrors () {
      this.errorMessage = null
      this.errors.clear()
    },

    getDishCategories () {
      axios.get(this.dishCategoriesEndpointUrl)
        .then(response => {
          this.dishCategories = response.data
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to load Dish categories...')
        })
    },

    addNewDish () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          // build dish object
          let newDish = {
            id: this.dishId,
            name: this.name,
            description: this.description,
            categoryId: this.categoryId,
            products: this.mapProducts()
          }
          if (this.editMode()) {
            this.updateDish(newDish)
          } else {
            this.addDish(newDish)
          }
        } else {
          console.log('Couldn\'t add new dish due to validation errors')
        }
      })
    },

    mapProducts () {
      return this.products.map(p => {
        return {
          productId: p.id,
          weight: p.weight,
          packageId: p.packageId
        }
      })
    },

    addDish (dish) {
      axios.post(this.dishesEndpointUrl, dish)
        .then(() => {
          // on success go to Dishes Page
          this.$router.push({name: 'DishesPage'})
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to add dish ' + JSON.stringify(dish))
        })
    },

    updateDish (dish) {
      axios.put(this.dishesEndpointUrl + dish.id, dish, this.getUpdateDishQueryParam())
        .then(() => {
          // on success go back to Dishes \ Meals \ Day page
          this.$router.push(this.getGoBackPath())
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to update dish ' + JSON.stringify(dish))
        })
    },

    getUpdateDishQueryParam () {
      if (this.planId !== undefined) {
        return {
          params: {
            planId: this.planId
          }
        }
      }
      return {}
    },

    goBack () {
      this.$router.push(this.getGoBackPath())
    },

    calculateProductsTotal (products, propertyGetter) {
      let total = products.reduce((total, product) => propertyGetter(product) + total, 0)
      return total.toFixed(1)
    },

    updateProductsTotal () {
      this.totalCalorific = this.calculateProductsTotal(this.products, p => Number(p.calorific))
      this.totalProteins = this.calculateProductsTotal(this.products, p => Number(p.proteins))
      this.totalFats = this.calculateProductsTotal(this.products, p => Number(p.fats))
      this.totalCarbs = this.calculateProductsTotal(this.products, p => Number(p.carbs))
      this.totalWeight = this.calculateProductsTotal(this.products, p => Number(p.weight))
    }
  },

  mounted () {
    // load dish categories on page init
    this.getDishCategories()
    axios.get('/api/plans/' + this.$route.params.planId + '/packages')
      .then(response => {
        this.foodPackages = response.data
      })
      .catch(e => {
        this.getErrorMessage(e, 'Failed to load Food plan packages...')
      })
  }
}
</script>

<style scoped>

</style>
