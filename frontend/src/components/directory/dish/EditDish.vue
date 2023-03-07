<template>
  <div>
    <h2 class="directory-header">Add Dish</h2>
    <div class="container">
      <div class="row">
        <div class="col-md-9">
          <input v-validate="'required'" v-model="name" name="name"
                 v-bind:class="{ validationError: errors.has('name')}"
                 placeholder='Enter dish name here..' style="width: 100%"/>
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
      <div style="margin-top: 15px">
        <b-button variant="outline-success" size="sm" v-on:click="addNewDish">{{ addButtonTitle }}</b-button>
        <router-link :to="{ name : 'DishesPage' }"><b-button variant="outline-danger" size="sm" >Cancel</b-button></router-link>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'EditDish',
  props: {
    oldDish: {
      type: Object,
      required: false
    }
  },

  data () {
    return {
      dishesEndpointUrl: '/api/dishes/',
      dishCategoriesEndpointUrl: '/api/dish-categories/',
      dishCategories: [],
      addButtonTitle: this.editMode() ? 'Update' : 'Add',
      dishId: this.editMode() ? this.oldDish.id : -1,
      name: this.editMode() ? this.oldDish.name : '',
      categoryId: this.editMode() ? this.oldDish.categoryId : 0,
      products: this.editMode() ? this.oldDish.products : [],
      errorMessage: null
    }
  },

  methods: {
    editMode () {
      return this.oldDish !== undefined
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
          this.getErrorMessage(e, 'Failed to load Products...')
        })
    },

    addNewDish () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          // build dish object
          let newDish = {
            id: this.dishId,
            name: this.name,
            categoryId: this.categoryId,
            products: this.products
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
      axios.put(this.dishesEndpointUrl + dish.id, dish)
        .then(() => {
          // on success go to Dishes Page
          this.$router.push({name: 'DishesPage'})
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to update dish ' + JSON.stringify(dish))
        })
    }
  },

  mounted () {
    // load dish categories on page init
    this.getDishCategories()
  }
}
</script>

<style scoped>

</style>
