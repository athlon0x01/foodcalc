<template>
  <div>
    <div v-bind:key="'dishView-' + dish.id" class="row">
      <div class="col-md-10 border" style="text-align: left;margin-top: 3px">&emsp;<em>{{dish.name}}</em></div>
      <template v-if="selectMode">
        <div class="col-md-1">
          <b-button variant="outline-info" size="sm" v-on:click="selectDish" >Select</b-button>
        </div>
      </template>
      <template v-if="manageMode">
        <div class="col-md-1">
          <b-button variant="outline-success" size="sm" v-on:click="editDish">Edit</b-button>
        </div>
        <div class="col-md-1">
          <b-button variant="outline-danger" size="sm" v-on:click="removeDish">Delete</b-button>
        </div>
      </template>
    </div>
    <template v-if="dish.products.length > 0">
      <div v-for="product in dish.products" :key="'dish-' + dish.id + '-' + product.id">
        <product-view v-bind:product="product"/>
      </div>
    </template>
  </div>
</template>

<script>
import ProductView from 'src/components/directory/product/ProductView'

export default {
  name: 'TemplateDishView',
  components: {ProductView},
  props: {
    dish: {
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

  methods: {
    selectDish () {
      this.$emit('dishSelected', this.dish.id)
    },

    editDish () {
      this.$router.push({name: 'EditTemplateDishPage', params: {oldDish: this.dish}})
    },

    removeDish () {
      this.$emit('remove', this.dish.id)
    }
  }
}
</script>

<style scoped>

</style>
