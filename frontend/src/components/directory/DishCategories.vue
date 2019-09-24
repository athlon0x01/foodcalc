<template>
  <directory-list v-bind:endpoint-url="url" v-bind:title="title" v-bind:no-items-msg="noItemsMsg"/>
</template>

<script>
import DirectoryList from 'src/components/directory/DirectoryList'

export default {
  name: 'DishCategories',
  components: {DirectoryList},
  data () {
    return {
      url: '/api/dish-categories/',
      title: 'Dish categories:',
      noItemsMsg: 'No dish categories yet...'
    }
  }
}
</script>

<style scoped>

</style>
