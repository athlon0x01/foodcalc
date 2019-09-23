<template>
  <div>
    <h2 class="directory-header">Dish categories:</h2>

    <!--Categories table-->
    <div v-if="categories.length > 0" class="container">
      <div class="row headerRow">
        <div class="col-sm-10"><strong>Name</strong></div>
      </div>
      <div v-for="category in categories" :key="category.id">
        <directory-list-item v-bind:item-data="category" v-on:update="updateCategory" v-on:remove="removeCategory"/>
      </div>
    </div>
    <div v-if="categories.length === 0 && errorMessage === null">
      <p><i>No Categories loaded</i></p>
    </div>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>

    <!--Add new item section-->
    <b-button variant="link" size="sm" v-on:click="addMode = !addMode">Add new dish category</b-button>
    <div v-if="addMode !== undefined && addMode" class="container">
      <directory-list-new-item v-on:addNew="addCategory"/>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import DirectoryListItem from 'src/components/directory/DirectoryListItem'
import DirectoryListNewItem from 'src/components/directory/DirectoryListNewItem'

export default {
  name: 'DishCategories',
  components: {DirectoryListNewItem, DirectoryListItem},
  data () {
    return {
      addMode: false,
      categories: [],
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

    clearErrors () {
      this.errorMessage = null
      this.errors.clear()
    },

    addCategory (newItem) {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let newItemObject = {
            name: newItem
          }
          axios.post('/api/dish-categories', newItemObject)
            .then(response => {
              this.categories.push(response.data)
              this.addMode = false
              this.clearErrors()
            })
            .catch(e => {
              this.getErrorMessage(e, 'Failed to add new Dish Category ' + this.newCategory)
            })
        } else {
          console.log('Couldn\'t add new category due to validation errors')
        }
      })
    },

    updateCategory (category) {
      this.$validator.validateAll().then((result) => {
        if (result) {
          axios.put('/api/dish-categories/' + category.id, category)
            .then(() => {
              let index = this.categories.findIndex(cat => cat.id === category.id)
              this.categories[index] = category
              this.clearErrors()
            })
            .catch(e => {
              this.getErrorMessage(e, 'Failed to update Dish Category ' + category.name)
            })
        } else {
          console.log('Couldn\'t update category due to validation errors')
        }
      })
    },

    removeCategory (id) {
      axios.delete('/api/dish-categories/' + id)
        .then(() => {
          this.categories = this.categories.filter(category => category.id !== id)
          this.clearErrors()
        })
        .catch(e => {
          let category = this.categories.find(category => category.id === id)
          this.getErrorMessage(e, 'Failed to delete Dish Category ' + category.name)
        })
    }
  },

  mounted () {
    // load dish categories on page init
    axios.get('/api/dish-categories')
      .then(response => {
        this.categories = response.data
        this.clearErrors()
      })
      .catch(e => {
        this.getErrorMessage(e, 'Failed to load Dish categories...')
      })
  }
}
</script>

<style scoped>
  .validationError {
    background-color: orange;
  }
</style>
