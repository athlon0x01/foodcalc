<template>
  <div class="row">
    <div :class="[nameWidthClass, nameTextClass]">&emsp;&emsp;{{name}}</div>
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
    </template>
    <template v-else>
      <div class="col-md-1 border">{{weight}}</div>
    </template>
    <div class="col-md-1" v-if="selectMode">
      <b-button variant="outline-info" size="sm" v-on:click="selectProduct" >Обрати</b-button>
    </div>
    <div class="col-md-1" v-if="manageMode">
      <b-button variant="outline-info" size="sm" v-on:click="moveUp">/\</b-button>
      <b-button variant="outline-info" size="sm" v-on:click="moveDown">\/</b-button>
    </div>
    <div class="col-md-1" v-if="manageMode">
      <b-button variant="outline-danger" size="sm" v-on:click="deleteProduct" >Видал.</b-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProductView',
  props: {
    product: {
      type: Object,
      required: true
    },
    selectMode: {
      type: Boolean,
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
      nameWidthClass: this.selectMode ? 'col-md-6' : 'col-md-5',
      nameTextClass: 'text-left border'
    }
  },

  methods: {
    selectProduct () {
      this.$emit('productSelected', this.product)
    },

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
    }
  }
}
</script>

<style scoped>

</style>
