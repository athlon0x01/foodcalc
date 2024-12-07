<template>
  <div class="row">
    <template v-if="editMode">
      <div class="col-sm-10" style="text-align: left">
        <input v-validate="'required'" v-model="itemName" name="item.name"
               v-bind:class="{ validationError: errors.has('item.name')}"
               v-on:keyup.13="updateItem" style="width: 100%"/>
      </div>
      <div class="col-sm-1">
        <b-button variant="outline-success" size="sm" v-on:click="updateItem()">Онов.</b-button>
      </div>
      <div class="col-sm-1">
        <b-button variant="outline-danger" size="sm" v-on:click="cancelEdit()">Назад</b-button>
      </div>
      <div v-show="errors.has('item.name')" class="alert" style="margin-top: 5px">
        <span>{{errors.first('item.name')}}</span>
      </div>
    </template>
    <template v-else>
      <div class="col-sm-10 text-left"><i>{{itemName}}</i></div>
      <div class="col-sm-1">
        <b-button variant="outline-success" size="sm" v-on:click="startEdit()">Змін.</b-button>
      </div>
      <div class="col-sm-1">
        <b-button variant="outline-danger" size="sm" v-on:click="removeItem()">Видал.</b-button>
      </div>
    </template>
  </div>
</template>

<script>
export default {
  name: 'DirectoryListItem',
  props: {
    itemData: {
      type: Object,
      required: true
    }
  },

  data: function () {
    return {
      editMode: false,
      itemId: this.itemData.id,
      itemName: this.itemData.name
    }
  },

  methods: {
    startEdit () {
      this.editMode = true
    },

    cancelEdit () {
      this.editMode = false
      this.itemName = this.itemData.name
    },

    updateItem () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let updatedItem = {
            id: this.itemId,
            name: this.itemName
          }
          this.$emit('update', updatedItem)
          this.editMode = false
        } else {
          console.log('Couldn\'t update item due to validation errors')
        }
      })
    },

    removeItem () {
      this.$emit('remove', this.itemId)
    }
  }
}
</script>

<style scoped>
  .validationError {
    background-color: orange;
  }
</style>
