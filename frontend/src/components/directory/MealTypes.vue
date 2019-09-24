<template>
  <directory-list v-bind:endpoint-url="url" v-bind:title="title"/>
</template>

<script>
import DirectoryList from 'src/components/directory/DirectoryList'

export default {
  name: 'MealTypes',
  components: {DirectoryList},
  data () {
    return {
      url: '/api/meal-types/',
      title: 'Meal Types'
    }
  }
}
</script>

<style scoped>

</style>
