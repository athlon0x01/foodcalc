<template>
  <div>
    <h4 class="meal-header">{{meal.type.name}}</h4>
    <div class="container border border-primary">
      <div class="row" style="padding-left: 10px">
        <p>{{meal.description}}</p>
      </div>
      <template v-if="editable">
        <div class="row justify-content-md-center" style="padding-bottom:5px">
          <div class="col-md-2">
            <b-button variant="outline-success" size="sm" v-on:click="editMeal">Змінити</b-button>
          </div>
          <div class="col-md-4"/>
          <div class="col-md-2">
            <b-button variant="outline-danger" size="sm" v-on:click="deleteMeal">Видалити</b-button>
          </div>
        </div>
      </template>
      <!--Dishes section-->
      <template v-if="meal.dishes.length > 0" class="row">
        <!--Header-->
        <h5 style="padding-top:10px">Страви</h5>
        <div class="row headerRow bg-light">
          <div class="col-md-3 border"><strong>Назва</strong></div>
          <div class="col-md-1 border"><strong>Калорії</strong></div>
          <div class="col-md-1 border"><strong>Протеіни</strong></div>
          <div class="col-md-1 border"><strong>Жири</strong></div>
          <div class="col-md-1 border"><strong>Вуглвд.</strong></div>
          <div class="col-md-1 border"><strong>Вага</strong></div>
          <div class="col-md-2 border"><strong>Пакунок</strong></div>
        </div>
        <div v-for="dish in meal.dishes" :key="'mealDish-' + dish.id">
          <dish-view v-bind:dish="dish"/>
        </div>
      </template>
      <!--Products section-->
      <template v-if="meal.products.length > 0" class="row">
        <!--Header-->
        <h5 style="padding-top:10px">Продукти</h5>
        <div class="row headerRow bg-light">
          <div class="col-md-3 border"><strong>Назва</strong></div>
          <div class="col-md-1 border"><strong>Калорії</strong></div>
          <div class="col-md-1 border"><strong>Протеіни</strong></div>
          <div class="col-md-1 border"><strong>Жири</strong></div>
          <div class="col-md-1 border"><strong>Вуглвд.</strong></div>
          <div class="col-md-1 border"><strong>Вага</strong></div>
          <div class="col-md-2 border"><strong>Пакунок</strong></div>
        </div>
        <!--Content-->
        <div v-for="product in meal.products" :key="'mealProduct-' + product.id">
          <product-with-package-view v-bind:product="product"
                                     v-bind:manage-mode="false"/>
        </div>
        <div style="padding-bottom: 10px"/>
      </template>
      <div class="row headerRow bg-light">
        <div class="col-md-2"/>
        <div class="col-md-2 border"><em>Калорії</em></div>
        <div class="col-md-2 border"><em>Протеіни</em></div>
        <div class="col-md-2 border"><em>Жири</em></div>
        <div class="col-md-2 border"><em>Вуглвд.</em></div>
        <div class="col-md-2 border"><em>Вага</em></div>
      </div>
      <div class="row">
        <div class="col-md-2 border"><em><strong>Загалом Трапеза</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{meal.calorific}}</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{meal.proteins}}</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{meal.fats}}</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{meal.carbs}}</strong></em></div>
        <div class="col-md-2 border"><em><strong>{{meal.weight}}</strong></em></div>
      </div>
    </div>
  </div>
</template>

<script>
import ProductWithPackageView from 'src/components/directory/product/ProductWithPackageView'
import DishView from 'src/components/directory/dish/DishView'

export default {
  name: 'MealView',
  components: {ProductWithPackageView, DishView},
  props: {
    meal: {
      type: Object,
      required: true
    },
    editable: {
      type: Boolean,
      required: false
    },
    planTitle: {
      type: String,
      required: false
    },
    dateTitle: {
      type: String,
      required: false
    }
  },

  methods: {
    editMeal () {
      this.$router.push({
        path: '/plan/' + this.$route.params.planId + '/day/' + this.$route.params.dayId + '/meal/' + this.meal.id,
        query: {
          planTitle: this.planTitle,
          dateTitle: this.dateTitle
        }
      })
    },

    deleteMeal () {
      this.$emit('remove', this.meal.id)
    }
  }
}
</script>

<style scoped>
.meal-header{
  text-align: center;
  color: #666600;
  padding-top:10px;
}
</style>
