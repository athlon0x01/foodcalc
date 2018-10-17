<template>
  <div>
    <nav>
      <router-link :to="{ name : 'Products' }">Products</router-link>
      <router-link :to="{ name : 'ProductCategories' }">Product Categories</router-link>
    </nav>
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
