<template>
  <div>
    <b-nav justified tabs>
      <b-nav-item>
        <router-link :to="{ name : 'ProductsPage' }">Продукти</router-link>
      </b-nav-item>
      <b-nav-item>
        <router-link :to="{ name : 'DishesPage' }">Страви</router-link>
      </b-nav-item>
      <b-nav-item>
        <router-link :to="{ name : 'ProductCategoriesPage' }">Категорії продуктів</router-link>
      </b-nav-item>
      <b-nav-item>
        <router-link :to="{ name : 'DishCategoriesPage' }">Категорії страв</router-link>
      </b-nav-item>
      <b-nav-item>
        <router-link :to="{ name : 'MealTypesPage' }">Типи трапез</router-link>
      </b-nav-item>
    </b-nav>
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'DirectoryPage'
}
</script>

<style scoped>

</style>
