<template>
  <div>
    <div v-bind:key="'dishView-' + dish.id" class="row">
      <div class="col-md-10 border" style="text-align: left;margin-top: 3px">&emsp;<em>{{dish.name}}</em></div>
      <template v-if="selectMode">
        <div class="col-md-1">
          <b-button variant="outline-info" size="sm" v-on:click="selectDish" >Обрати</b-button>
        </div>
      </template>
      <template v-if="manageMode">
        <div class="col-md-1">
          <b-button variant="outline-success" size="sm" v-on:click="editDish">Змін.</b-button>
        </div>
        <div class="col-md-1">
          <b-button variant="outline-danger" size="sm" v-on:click="removeDish">Видал.</b-button>
        </div>
      </template>
    </div>
    <template v-if="dish.products.length > 0">
      <div v-for="product in dish.products" :key="'dish-' + dish.id + '-' + product.id">
        <product-with-package-view v-bind:product="product"
                                   v-bind:manage-mode="false"/>
      </div>
    </template>
  </div>
</template>

<script>
import ProductWithPackageView from 'src/components/directory/product/ProductWithPackageView'

export default {
  name: 'DishView',
  components: {ProductWithPackageView},
  props: {
    dish: {
      type: Object,
      required: true
    },
    goBackPath: {
      type: Object,
      required: false
    },
    selectMode: {
      type: Boolean,
      required: false
    },
    manageMode: {
      type: Boolean,
      required: false
    },
    planId: {
      type: String,
      required: false
    }
  },

  methods: {
    selectDish () {
      this.$emit('dishSelected', this.dish.id)
    },

    editDish () {
      this.$router.push({
        name: 'EditDishPage',
        params: {
          goBackPath: this.goBackPath,
          oldDish: this.dish,
          planId: this.planId
        }
      })
    },

    removeDish () {
      this.$emit('remove', this.dish.id)
    }
  }
}
</script>

<style scoped>

</style>
