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
    <div v-else>
      <p><i>No Categories loaded</i></p>
    </div>
    <!--Errors output-->
    <div v-if="errors.length > 0" class="alert">
      <p v-for="(error, index) in errors" :key="index">{{error}}</p>
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
      errors: []
    }
  },

  methods: {
    addCategory () {
      let newCategory = {
        name: this.newName
      }
      axios.post('/api/product-categories', newCategory)
        .then(response => {
          this.categories.push(response.data)
          this.addMode = false
          this.newName = ''
        })
        .catch(e => {
          this.errors.push(e)
        })
    },

    updateCategory (category) {
      axios.put('/api/product-categories/' + category.id, category)
        .then(() => {
          this.editCategory = null
        })
        .catch(e => {
          this.errors.push(e)
        })
    },

    deleteCategory (id, index) {
      axios.delete('/api/product-categories/' + id)
        .then(() => {
          this.categories.splice(index, 1)
        })
        .catch(e => {
          this.errors.push(e)
        })
    }
  },

  mounted () {
    // load product categories on page init
    axios.get('/api/product-categories')
      .then(response => {
        this.categories = response.data
      })
      .catch(e => {
        this.errors.push(e)
      })
  }
}
</script>

<style scoped>
</style>
