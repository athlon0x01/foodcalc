<template>
  <div class="row">
    <div class="col-md-3">
      <input v-validate="'required'" v-model="name" name="name"
             v-bind:class="{ validationError: errors.has('name')}"
             placeholder='Enter product name here..' style="width: 100%"/>
      <p v-if="errors.has('name') > 0" class="alert">{{errors.first('name')}}</p>
    </div>
    <div class="col-md-2" style="margin-top: 5px">
      <select v-validate="'min_value:1'" v-model="categoryId" name="categoryId"
              v-bind:class="{ validationError: errors.has('categoryId')}">
        <option v-for="category in categories" :key="category.id" :value="category.id">
          {{ category.name }}
        </option>
      </select>
      <p v-if="errors.has('categoryId') > 0" class="alert">{{errors.first('categoryId')}}</p>
    </div>
    <div class="col-md-1">
      <input type="number" min="0" step="0.01" v-model="calorific" name="calorific" style="width: 100%"/>
    </div>
    <div class="col-md-1">
      <input type="number" min="0" step="0.01" v-model="proteins" name="proteins" style="width: 100%"/>
    </div>
    <div class="col-md-1">
      <input type="number" min="0" step="0.01" v-model="fats" name="fats" style="width: 100%"/>
    </div>
    <div class="col-md-1">
      <input type="number" min="0" step="0.01" v-model="carbs" name="carbs" style="width: 100%"/>
    </div>
    <div class="col-md-1">
      <input type="number" min="0" step="0.01" v-model="defaultWeight" name="defaultWeight" style="width: 100%"/>
    </div>
    <div class="col-md-1">
      <b-button variant="outline-success" size="sm" v-on:click="addNewProduct">Add</b-button>
    </div>
    <div class="col-md-1">
      <b-button variant="outline-danger" size="sm" v-on:click="cancelAdd">Cancel</b-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NewProduct',

  props: {
    categories: {
      type: Array,
      required: true
    }
  },

  data: function () {
    return {
      name: '',
      categoryId: 0,
      calorific: 0,
      proteins: 0,
      fats: 0,
      carbs: 0,
      defaultWeight: 5
    }
  },

  methods: {
    addNewProduct () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let product = {
            name: this.name,
            categoryId: this.categoryId,
            calorific: this.calorific,
            proteins: this.proteins,
            fats: this.fats,
            carbs: this.carbs,
            defaultWeight: this.defaultWeight
          }
          this.$emit('addNew', product)
        } else {
          console.log('Couldn\'t add new product due to validation errors')
        }
      })
    },

    cancelAdd () {
      this.$emit('cancelAdd')
    }
  }
}
</script>

<style scoped>
  .validationError {
    background-color: orange;
  }
</style>
