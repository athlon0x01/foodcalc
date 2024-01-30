<template>
  <div>
    <div v-bind:key="category.id" class="row bg-light">
      <div class="col-md-12 border" style="text-align: left"><em><strong>{{category.name}}</strong></em></div>
    </div>
    <template v-if="category.dishes.length > 0">
      <div v-for="dish in category.dishes" :key="category.id + '-' + dish.id">
        <dish-view v-bind:dish="dish"
                   v-bind:select-mode="selectMode"
                   v-bind:manage-mode="manageMode"
                   v-on:remove="removeDish"
                   v-on:dishSelected="selectDish"/>
      </div>
    </template>
  </div>
</template>

<script>
import DishView from 'src/components/directory/dish/DishView'

export default {
  name: 'CategoryWithDishes',
  components: {DishView},
  props: {
    category: {
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
    removeDish (dishId) {
      this.$emit('remove', dishId)
    },

    selectDish (dishId) {
      this.$emit('select', dishId)
    }
  }
}
</script>

<style scoped>

</style>
