<template>
  <directory-list v-bind:endpoint-url="url" v-bind:title="title"/>
</template>

<script>
import DirectoryList from 'src/components/directory/DirectoryList'

export default {
  name: 'DishCategories',
  components: {DirectoryList},
  data () {
    return {
      url: '/api/dish-categories/',
      title: 'Dish Categories'
    }
  }
}
</script>

<style scoped>

</style>
