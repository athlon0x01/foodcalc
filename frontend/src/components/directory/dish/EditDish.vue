<template>
  <div>
    <h2 class="directory-header">Dish edit page draft</h2>
    <div>
      <b-button variant="outline-success" size="sm" v-on:click="addNewDish">Add</b-button>
      <router-link :to="{ name : 'DishesPage' }"><b-button variant="outline-danger" size="sm" >Cancel</b-button></router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EditDish',

  methods: {
    addNewDish () {
      console.log('New dish added')
      this.$router.push({name: 'DishesPage'})
    }
  }
}
</script>

<style scoped>

</style>
