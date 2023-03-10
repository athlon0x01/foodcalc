<template>
  <div>
    <div v-bind:key="category.id" class="row bg-light">
      <div class="col-md-12 border" style="text-align: left"><em><strong>{{category.name}}</strong></em></div>
    </div>
    <template v-if="category.products.length > 0">
      <div v-for="product in category.products" :key="category.id + '-' + product.id">
        <product-view v-bind:product="product"
                      v-bind:select-mode="true"
                      v-bind:manage-mode="false"
                      v-on:productSelected="selectProduct"/>
      </div>
    </template>
  </div>
</template>

<script>
import ProductView from 'src/components/directory/product/ProductView'
export default {
  name: 'CategoryWithProductViews',
  components: {ProductView},
  props: {
    category: {
      type: Object,
      required: true
    }
  },

  methods: {
    selectProduct (product) {
      this.$emit('productSelected', product)
    }
  }
}
</script>

<style scoped>

</style>
