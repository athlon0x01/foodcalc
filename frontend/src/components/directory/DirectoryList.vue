<template>
  <div>
    <h2 class="directory-header">{{title}}</h2>

    <!--List-->
    <div v-if="items.length > 0" class="container">
      <div class="row headerRow">
        <div class="col-sm-10"><strong>Name</strong></div>
      </div>
      <div v-for="item in items" :key="item.id">
        <directory-list-item v-bind:item-data="item" v-on:update="updateItem" v-on:remove="removeItem"/>
      </div>
    </div>
    <div v-if="items.length === 0 && errorMessage === null">
      <p><i>No {{title}} yet...</i></p>
    </div>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>

    <!--Add new item section-->
    <b-button variant="link" size="sm" v-on:click="addMode = !addMode">Add new</b-button>
    <div v-if="addMode !== undefined && addMode" class="container">
      <directory-list-new-item v-on:addNew="addItem"/>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import DirectoryListItem from 'src/components/directory/DirectoryListItem'
import DirectoryListNewItem from 'src/components/directory/DirectoryListNewItem'

export default {
  name: 'DirectoryList',

  components: {DirectoryListNewItem, DirectoryListItem},

  props: {
    endpointUrl: {
      type: String,
      required: true
    },
    title: {
      type: String,
      required: true
    }
  },

  data () {
    return {
      addMode: false,
      items: [],
      errorMessage: null
    }
  },

  methods: {
    getErrorMessage (error, defaultMessage) {
      if (error.response !== undefined && error.response.data !== undefined &&
        (typeof error.response.data === 'string' || error.response.data instanceof String)) {
        this.errorMessage = error.response.data
      } else {
        console.log(error)
        this.errorMessage = defaultMessage
      }
    },

    clearErrors () {
      this.errorMessage = null
      this.errors.clear()
    },

    addItem (newItem) {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let newItemObject = {
            name: newItem
          }
          axios.post(this.endpointUrl, newItemObject)
            .then(response => {
              this.items.push(response.data)
              this.addMode = false
              this.clearErrors()
            })
            .catch(e => {
              this.getErrorMessage(e, 'Failed to add ' + newItem)
            })
        } else {
          console.log('Couldn\'t add new item due to validation errors')
        }
      })
    },

    updateItem (item) {
      this.$validator.validateAll().then((result) => {
        if (result) {
          axios.put(this.endpointUrl + item.id, item)
            .then(() => {
              let index = this.items.findIndex(it => it.id === item.id)
              this.items[index] = item
              this.clearErrors()
            })
            .catch(e => {
              this.getErrorMessage(e, 'Failed to update ' + item.name)
            })
        } else {
          console.log('Couldn\'t update item due to validation errors')
        }
      })
    },

    removeItem (id) {
      axios.delete(this.endpointUrl + id)
        .then(() => {
          this.items = this.items.filter(item => item.id !== id)
          this.clearErrors()
        })
        .catch(e => {
          let item = this.items.find(item => item.id === id)
          this.getErrorMessage(e, 'Failed to delete ' + item.name)
        })
    }
  },

  mounted () {
    // load content on page init
    axios.get(this.endpointUrl)
      .then(response => {
        this.items = response.data
        this.clearErrors()
      })
      .catch(e => {
        this.getErrorMessage(e, 'Failed to load items...')
      })
  }
}
</script>

<style scoped>

</style>
