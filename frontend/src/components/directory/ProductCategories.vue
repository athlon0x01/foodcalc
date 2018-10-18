<template>
  <div>
    <h2 class="directory-header">Product categories:</h2>

    <!--Categories table-->
    <table v-if="categories.length > 0" align="center" border="1" width="300px">
      <tr><th>Name</th></tr>
      <tr v-for="category in categories" :key="category.id">
        <td><i>{{category.name}}</i></td>
      </tr>
    </table>
    <div v-else>
      <p><i>No Categories loaded</i></p>
    </div>
    <!--Errors output-->
    <div v-if="errors.length > 0" class="alert">
      <p v-for="(error, index) in errors" :key="index">{{error}}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ProductCategories',
  data () {
    return {
      categories: [],
      errors: []
    }
  },
  mounted () {
    // load product categories on page init
    axios.get('/api/product-categories')
      .then(response => {
        this.categories = response.data
      })
      .catch(e => {
        this.errors.push(e)
      })
  }
}
</script>

<style scoped>
</style>
