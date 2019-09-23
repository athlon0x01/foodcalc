<template>
  <div>
    <div class="row">
      <div class="col-sm-10">
        <input v-validate="'required'" v-model="newItem" name="newItem"
               v-bind:class="{ validationError: errors.has('newItem')}"
               v-on:keyup.13="addNewItem()" placeholder='Enter here..' style="width: 100%"/>
      </div>
      <div class="col-sm-1">
        <b-button variant="outline-success" size="sm" v-on:click="addNewItem()">Add</b-button>
      </div>
    </div>
    <div v-show="errors.has('newItem')" class="row alert" style="margin-top: 5px">
      <span class="col-sm-10">{{errors.first('newItem')}}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DirectoryListNewItem',

  data: function () {
    return {
      addMode: false,
      newItem: ''
    }
  },

  methods: {
    addNewItem () {
      this.$emit('addNew', this.newItem)
    }
  }
}
</script>

<style scoped>
  .validationError {
    background-color: orange;
  }
</style>
