<template>
  <div class="row">
    <div class="col-md-5 text-left border">&emsp;&emsp;{{name}}</div>
    <div class="col-md-1 border">{{calorific}}</div>
    <div class="col-md-1 border">{{proteins}}</div>
    <div class="col-md-1 border">{{fats}}</div>
    <div class="col-md-1 border">{{carbs}}</div>
    <div class="col-md-1 border">{{weight}}</div>
  </div>
</template>

<script>
export default {
  name: 'ProductView',
  props: {
    product: {
      type: Object,
      required: true
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
      weight: this.product.weight
    }
  }
}
</script>

<style scoped>

</style>
