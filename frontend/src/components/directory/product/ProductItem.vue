<template>
  <div class="row">
    <template v-if="editMode">
      <div class="col-md-3">
        <input v-validate="'required'" v-model="name" name="name"
               v-bind:class="{ validationError: errors.has('name')}"
               placeholder='Enter product name here..' style="width: 100%"/>
        <p v-if="errors.has('name') > 0" class="alert">{{errors.first('name')}}</p>
      </div>
      <div class="col-md-2" style="margin-top: 5px">
        <select v-model="categoryId" name="categoryId">
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
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
        <input type="number" min="0" step="0.01" v-model="weight" name="defaultWeight" style="width: 100%"/>
      </div>
      <div class="col-sm-1">
        <b-button variant="outline-success" size="sm" v-on:click="updateProduct">Update</b-button>
      </div>
      <div class="col-sm-1">
        <b-button variant="outline-danger" size="sm" v-on:click="cancelEdit">Cancel</b-button>
      </div>
      <div v-show="errors.has('item.name')" class="alert" style="margin-top: 5px">
        <span>{{errors.first('item.name')}}</span>
      </div>
    </template>
    <template v-else>
      <div class="col-md-5 text-left border">{{name}}</div>
      <div class="col-md-1 border">{{calorific}}</div>
      <div class="col-md-1 border">{{proteins}}</div>
      <div class="col-md-1 border">{{fats}}</div>
      <div class="col-md-1 border">{{carbs}}</div>
      <div class="col-md-1 border">{{weight}}</div>
      <div class="col-md-1">
        <b-button variant="outline-success" size="sm" v-on:click="startEdit">Edit</b-button>
      </div>
      <div class="col-md-1">
        <b-button variant="outline-danger" size="sm" v-on:click="removeItem">Delete</b-button>
      </div>
    </template>
  </div>
</template>

<script>
export default {
  name: 'ProductItem',
  props: {
    product: {
      type: Object,
      required: true
    },
    categories: {
      type: Array,
      required: true
    }
  },

  data: function () {
    return {
      editMode: false,
      productId: this.product.id,
      name: this.product.name,
      categoryId: this.product.categoryId,
      calorific: this.product.calorific,
      proteins: this.product.proteins,
      fats: this.product.fats,
      carbs: this.product.carbs,
      weight: this.product.weight
    }
  },

  methods: {
    startEdit () {
      this.editMode = true
    },

    cancelEdit () {
      this.editMode = false
      this.productId = this.product.id
      this.name = this.product.name
      this.categoryId = this.product.categoryId
      this.calorific = this.product.calorific
      this.proteins = this.product.proteins
      this.fats = this.product.fats
      this.carbs = this.product.carbs
      this.weight = this.product.weight
    },

    updateProduct () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let product = {
            id: this.productId,
            name: this.name,
            categoryId: this.categoryId,
            calorific: this.calorific,
            proteins: this.proteins,
            fats: this.fats,
            carbs: this.carbs,
            weight: this.weight
          }
          this.$emit('update', product)
          this.editMode = false
        } else {
          console.log('Couldn\'t update product due to validation errors')
        }
      })
    },

    removeItem () {
      this.$emit('remove', this.productId)
    }
  }
}
</script>

<style scoped>
  .validationError {
    background-color: orange;
  }
</style>
