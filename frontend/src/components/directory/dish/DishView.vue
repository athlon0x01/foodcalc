<template>
  <div>
    <div v-bind:key="dish.id" class="row">
      <div class="col-md-10 border" style="text-align: left">&emsp;<em>{{dish.name}}</em></div>
      <div class="col-md-1">
        <b-button variant="outline-success" size="sm" v-on:click="editDish">Edit</b-button>
      </div>
      <div class="col-md-1">
        <b-button variant="outline-danger" size="sm" v-on:click="removeDish">Delete</b-button>
      </div>
    </div>
    <template v-if="dish.products.length > 0">
      <div v-for="product in dish.products" :key="dish.id + '-' + product.id">
        <product-view v-bind:product="product"/>
      </div>
    </template>
  </div>
</template>

<script>
import ProductView from 'src/components/directory/product/ProductView'

export default {
  name: 'DishView',
  components: {ProductView},
  props: {
    dish: {
      type: Object,
      required: true
    }
  },

  methods: {
    editDish () {
      console.log('Will edit later')
    },

    removeDish () {
      console.log('Will remove later')
    }
  }
}
</script>

<style scoped>

</style>
