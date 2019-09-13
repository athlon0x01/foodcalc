<template>
  <div>
    <h2 class="directory-header">Meal types:</h2>

    <!--Meal Types table-->
    <div v-if="mealTypes.length > 0" class="container">
      <div class="row headerRow">
        <div class="col-sm-10"><strong>Name</strong></div>
      </div>
      <div v-for="(mealType, index) in mealTypes" :key="mealType.id" class="row">
        <template v-if="editMealType === mealType.id">
          <div class="col-sm-10" style="text-align: left">
            <input v-validate="'required'"  v-model="mealType.name" name="mealType.name"
                   v-on:keyup.13="updateMealType(mealType)" style="width: 100%"/>
          </div>
          <div class="col-sm-1">
            <b-button variant="outline-success" size="sm" v-on:click="updateMealType(mealType)">Update</b-button>
          </div>
          <div class="col-sm-1">
            <b-button variant="outline-danger" size="sm" v-on:click="cancelEdit(mealType)">Cancel</b-button>
          </div>
          <div v-show="errors.has('category.name')" class="alert" style="margin-top: 5px">
            <span>{{errors.first('category.name')}}</span>
          </div>
        </template>
        <template v-else>
          <div class="col-sm-10 text-left"><i>{{mealType.name}}</i></div>
          <div class="col-sm-1">
            <b-button variant="outline-success" size="sm" v-on:click="startEdit(mealType)">Edit</b-button>
          </div>
          <div class="col-sm-1">
            <b-button variant="outline-danger" size="sm" v-on:click="deleteMealType(mealType.id, index)">Delete</b-button>
          </div>
        </template>
      </div>
    </div>
    <div v-if="mealTypes.length === 0 && errorMessage === null">
      <p><i>No Meal Types loaded</i></p>
    </div>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>

    <!--Add new meal type section-->
    <b-button variant="link" size="sm" v-on:click="addMode = !addMode">Add new meal type</b-button>
    <div v-if="addMode !== undefined && addMode" class="container">
      <div class="row">
        <div class="col-sm-10">
          <input v-validate="'required'" v-model="newMealType" name="newMealType"
                 v-bind:class="{ validationError: errors.has('newMealType')}"
                 v-on:keyup.13="addMealType()" placeholder="Enter new meal type here.." style="width: 100%"/>
        </div>
        <div class="col-sm-1">
          <b-button variant="outline-success" size="sm" v-on:click="addMealType()">Add</b-button>
        </div>
      </div>
      <div v-show="errors.has('newMealType')" class="row alert" style="margin-top: 5px">
        <span class="col-sm-10">{{errors.first('newMealType')}}</span>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'MealTypes',
  data () {
    return {
      addMode: false,
      newMealType: '',
      editMealType: null,
      oldName: null,
      mealTypes: [],
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

    addMealType () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let newMealType = {
            name: this.newMealType
          }
          axios.post('/api/meal-types', newMealType)
            .then(response => {
              this.mealTypes.push(response.data)
              this.addMode = false
              this.newMealType = ''
              this.clearErrors()
            })
            .catch(e => {
              this.getErrorMessage(e, 'Failed to add new Meal Type ' + this.newMealType)
            })
        } else {
          console.log('Couldn\'t add new meal type due to validation errors')
        }
      })
    },

    updateMealType (mealType) {
      this.$validator.validateAll().then((result) => {
        if (result) {
          axios.put('/api/meal-types/' + mealType.id, mealType)
            .then(() => {
              this.editMealType = null
              this.clearErrors()
            })
            .catch(e => {
              this.getErrorMessage(e, 'Failed to update Meal Type ' + mealType.name)
              this.editMealType = null
            })
        } else {
          console.log('Couldn\'t update meal type due to validation errors')
        }
      })
    },

    deleteMealType (id, index) {
      axios.delete('/api/meal-types/' + id)
        .then(() => {
          this.mealTypes.splice(index, 1)
          this.clearErrors()
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to delete Meal Type ' + this.categories[index].name)
        })
    },

    startEdit (mealType) {
      this.editMealType = mealType.id
      this.oldName = mealType.name
    },

    cancelEdit (mealType) {
      this.editMealType = null
      mealType.name = this.oldName
      this.clearErrors()
    }
  },

  mounted () {
    // load meal types on page init
    axios.get('/api/meal-types')
      .then(response => {
        this.mealTypes = response.data
        this.clearErrors()
      })
      .catch(e => {
        this.getErrorMessage(e, 'Failed to load Meal Types...')
      })
  }
}
</script>

<style scoped>
  .validationError {
    background-color: orange;
  }
</style>
