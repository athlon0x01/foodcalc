<template>
  <div>
    <h2 class="directory-header">Product categories:</h2>

    <!--Categories table-->
    <div v-if="categories.length > 0" class="container">
      <div class="row headerRow">
        <div class="col-sm-10"><strong>Name</strong></div>
      </div>
      <div v-for="(category, index) in categories" :key="category.id" class="row">
        <template v-if="editCategory === category.id">
          <div class="col-sm-10" style="text-align: left">
            <input v-on:keyup.13="updateCategory(category)" v-model="category.name" style="width: 100%"/>
          </div>
          <div class="col-sm-1">
            <b-button variant="outline-success" size="sm" v-on:click="updateCategory(category)">Update</b-button>
          </div>
          <div class="col-sm-1">
            <b-button variant="outline-danger" size="sm" v-on:click="editCategory = null">Cancel</b-button>
          </div>
        </template>
        <template v-else>
          <div class="col-sm-10 text-left"><i>{{category.name}}</i></div>
          <div class="col-sm-1">
            <b-button variant="outline-success" size="sm" v-on:click="editCategory = category.id">Edit</b-button>
          </div>
          <div class="col-sm-1">
            <b-button variant="outline-danger" size="sm" v-on:click="deleteCategory(category.id, index)">Delete</b-button>
          </div>
        </template>
      </div>
    </div>
    <div v-if="categories.length === 0 && errorMessage === null">
      <p><i>No Categories loaded</i></p>
    </div>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>

    <!--Add new category section-->
    <b-button variant="link" size="sm" v-on:click="addMode = !addMode">Add new category</b-button>
    <div v-if="addMode !== undefined && addMode">
      <input v-on:keyup.13="addCategory()" v-model="newName"/>
      <b-button variant="outline-success" size="sm" v-on:click="addCategory()">Add</b-button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ProductCategories',
  data () {
    return {
      addMode: false,
      newName: '',
      editCategory: null,
      categories: [],
      errorMessage: null
    }
  },

  methods: {
    getErrorMessage (error, defaultMessage) {
      if (error.response !== undefined && error.response.data !== undefined &&
        error.response.data.message !== undefined) {
        this.errorMessage = error.response.data.message
      } else {
        console.log(error)
        this.errorMessage = defaultMessage
      }
    },

    addCategory () {
      let newCategory = {
        name: this.newName
      }
      axios.post('/api/product-categories', newCategory)
        .then(response => {
          this.categories.push(response.data)
          this.addMode = false
          this.newName = ''
          this.errorMessage = null
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to add new Product Category ' + this.newName)
        })
    },

    updateCategory (category) {
      axios.put('/api/product-categories/' + category.id, category)
        .then(() => {
          this.editCategory = null
          this.errorMessage = null
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to update Product Category ' + category.name)
          this.editCategory = null
        })
    },

    deleteCategory (id, index) {
      axios.delete('/api/product-categories/' + id)
        .then(() => {
          this.categories.splice(index, 1)
          this.errorMessage = null
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to delete Product Category ' + this.categories[index].name)
        })
    }
  },

  mounted () {
    // load product categories on page init
    axios.get('/api/product-categories')
      .then(response => {
        this.categories = response.data
        this.errorMessage = null
      })
      .catch(e => {
        this.getErrorMessage(e, 'Failed to load Product categories...')
      })
  }
}
</script>

<style scoped>
</style>
