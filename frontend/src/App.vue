<template>
  <div id="app">
    <b-nav justified tabs>
      <b-nav-item>
        <router-link to="/">Home</router-link>
      </b-nav-item>
      <b-nav-item>
        <router-link to="/directory">Food Guide</router-link>
      </b-nav-item>
      <b-nav-item>
        <router-link to="/about">About</router-link>
      </b-nav-item>
    </b-nav>
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
.alert {
  color: red;
  width: 100%;
}
.directory-header{
  text-align: center;
  padding-top:30px;
}
.food-plan-header{
  text-align: center;
  color: #009926;
}
</style>
