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
            <input v-validate="'required'"  v-model="category.name" name="category.name"
                   v-on:keyup.13="updateCategory(category)" style="width: 100%"/>
          </div>
          <div class="col-sm-1">
            <b-button variant="outline-success" size="sm" v-on:click="updateCategory(category)">Update</b-button>
          </div>
          <div class="col-sm-1">
            <b-button variant="outline-danger" size="sm" v-on:click="cancelEdit(category)">Cancel</b-button>
          </div>
          <div v-show="errors.has('category.name')" class="alert" style="margin-top: 5px">
            <span>{{errors.first('category.name')}}</span>
          </div>
        </template>
        <template v-else>
          <div class="col-sm-10 text-left"><i>{{category.name}}</i></div>
          <div class="col-sm-1">
            <b-button variant="outline-success" size="sm" v-on:click="startEdit(category)">Edit</b-button>
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
    <div v-if="addMode !== undefined && addMode" class="container">
      <div class="row">
        <div class="col-sm-10">
          <input v-validate="'required'" v-model="newCategory" name="newCategory"
                 v-bind:class="{ validationError: errors.has('newCategory')}"
                 v-on:keyup.13="addCategory()" placeholder="Enter new category here.." style="width: 100%"/>
        </div>
        <div class="col-sm-1">
          <b-button variant="outline-success" size="sm" v-on:click="addCategory()">Add</b-button>
        </div>
      </div>
      <div v-show="errors.has('newCategory')" class="row alert" style="margin-top: 5px">
        <span class="col-sm-10">{{errors.first('newCategory')}}</span>
      </div>
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
      newCategory: '',
      editCategory: null,
      oldName: null,
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

    clearErrors () {
      this.errorMessage = null
      this.errors.clear()
    },

    addCategory () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let newCategory = {
            name: this.newCategory
          }
          axios.post('/api/product-categories', newCategory)
            .then(response => {
              this.categories.push(response.data)
              this.addMode = false
              this.newCategory = ''
              this.clearErrors()
            })
            .catch(e => {
              this.getErrorMessage(e, 'Failed to add new Product Category ' + this.newCategory)
            })
        } else {
          console.log('Couldn\'t add new category due to validation errors')
        }
      })
    },

    updateCategory (category) {
      this.$validator.validateAll().then((result) => {
        if (result) {
          axios.put('/api/product-categories/' + category.id, category)
            .then(() => {
              this.editCategory = null
              this.clearErrors()
            })
            .catch(e => {
              this.getErrorMessage(e, 'Failed to update Product Category ' + category.name)
              this.editCategory = null
            })
        } else {
          console.log('Couldn\'t update category due to validation errors')
        }
      })
    },

    deleteCategory (id, index) {
      axios.delete('/api/product-categories/' + id)
        .then(() => {
          this.categories.splice(index, 1)
          this.clearErrors()
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to delete Product Category ' + this.categories[index].name)
        })
    },

    startEdit (category) {
      this.editCategory = category.id
      this.oldName = category.name
    },

    cancelEdit (category) {
      this.editCategory = null
      category.name = this.oldName
      this.clearErrors()
    }
  },

  mounted () {
    // load product categories on page init
    axios.get('/api/product-categories')
      .then(response => {
        this.categories = response.data
        this.clearErrors()
      })
      .catch(e => {
        this.getErrorMessage(e, 'Failed to load Product categories...')
      })
  }
}
</script>

<style scoped>
  .validationError {
    background-color: orange;
  }
</style>
