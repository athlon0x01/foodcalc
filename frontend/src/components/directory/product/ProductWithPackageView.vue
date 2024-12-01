<template>
  <div class="row">
    <div class="col-md-3 text-left border">&emsp;&emsp;{{name}}</div>
    <div class="col-md-1 border">{{calorific}}</div>
    <div class="col-md-1 border">{{proteins}}</div>
    <div class="col-md-1 border">{{fats}}</div>
    <div class="col-md-1 border">{{carbs}}</div>
    <template v-if="manageMode">
      <div class="col-md-1">
        <input type="number" min="0" step="0.01" name="weight" style="width: 100%"
               v-model="weight"
               v-on:change="updateWeight"/>
      </div>
      <div class="col-md-2" style="margin-top: 5px">
        <select v-model="foodPackageId" name="packageId" v-on:change="updatePackageId">
          <option v-for="foodPackage in foodPackages" :key="foodPackage.id" :value="foodPackage.id">
            {{ foodPackage.name }}
          </option>
        </select>
      </div>
    </template>
    <template v-else>
      <div class="col-md-1 border">{{weight}}</div>
      <div class="col-md-2 border">{{foodPackageName}}</div>
    </template>
    <div class="col-md-1" v-if="manageMode">
      <b-button variant="outline-info" size="sm" v-on:click="moveUp">/\</b-button>
      <b-button variant="outline-info" size="sm" v-on:click="moveDown">\/</b-button>
    </div>
    <div class="col-md-1" v-if="manageMode">
      <b-button variant="outline-danger" size="sm" v-on:click="deleteProduct" >Delete</b-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProductWithPackageView',
  props: {
    product: {
      type: Object,
      required: true
    },
    foodPackages: {
      type: Array,
      required: false
    },
    manageMode: {
      type: Boolean,
      required: false
    }
  },

  data: function () {
    return {
      productId: this.product.id,
      name: this.product.name,
      calorific: this.product.calorific,
      proteins: this.product.proteins,
      fats: this.product.fats,
      carbs: this.product.carbs,
      weight: this.product.weight,
      foodPackageId: this.product.packageId,
      foodPackageName: this.product.packageName
    }
  },

  methods: {
    moveUp () {
      this.$emit('productUp', this.product.id)
    },

    moveDown () {
      this.$emit('productDown', this.product.id)
    },

    deleteProduct () {
      this.$emit('productRemoved', this.product.id)
    },

    updateWeight () {
      this.$emit('weightUpdated', this.product.id, this.weight)
    },

    updatePackageId () {
      this.$emit('packageIdUpdated', this.product.id, this.foodPackageId)
    }
  }
}
</script>

<style scoped>

</style>
