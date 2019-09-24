<template>
  <directory-list v-bind:endpoint-url="url" v-bind:title="title"/>
</template>

<script>
import DirectoryList from 'src/components/directory/DirectoryList'

export default {
  name: 'ProductCategories',
  components: {DirectoryList},
  data () {
    return {
      url: '/api/product-categories/',
      title: 'Product Categories'
    }
  }
}
</script>

<style scoped>

</style>
