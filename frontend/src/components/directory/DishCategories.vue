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
      title: 'Категорії страв'
    }
  }
}
</script>

<style scoped>

</style>
