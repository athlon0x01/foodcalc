<template>
  <div>
    <b-nav justified tabs>
      <b-nav-item>
        <router-link :to="{ name : 'ProductsPage' }">Products</router-link>
      </b-nav-item>
      <b-nav-item>
        <router-link :to="{ name : 'ProductCategoriesPage' }">Product Categories</router-link>
      </b-nav-item>
      <b-nav-item>
        <router-link :to="{ name : 'MealTypesPage' }">Meal Types</router-link>
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
