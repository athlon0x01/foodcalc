<template>
  <div>
    <div v-bind:key="category.id" class="row bg-light">
      <div class="col-md-12 border" style="text-align: left"><i><b>{{category.name}}</b></i></div>
    </div>
    <template v-if="category.products.length > 0">
      <div v-for="product in category.products" :key="category.id + '-' + product.id">
        <product-item v-bind:product="product"
                      v-bind:categories="categories"
                      v-on:update="updateProduct"
                      v-on:remove="removeProduct"/>
      </div>
    </template>
  </div>
</template>

<script>
import ProductItem from 'src/components/directory/product/ProductItem'
export default {
  name: 'CategoryWithProducts',
  components: {ProductItem},
  props: {
    category: {
      type: Object,
      required: true
    },
    categories: {
      type: Array,
      required: true
    }
  },

  methods: {
    updateProduct (product) {
      this.$emit('update', product)
    },

    removeProduct (productId) {
      this.$emit('remove', productId)
    }
  }
}
</script>

<style scoped>

</style>
