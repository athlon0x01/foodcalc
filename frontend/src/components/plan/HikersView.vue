<template>
  <div>
    <h3 class="members-header">Учасники</h3>
    <!--hikers \ members-->
    <div v-if="members.length > 0" class="container">
      <!--Header-->
      <div class="row headerRow bg-light">
        <div class="col-md-3 border"><strong>Ім'я</strong></div>
        <div class="col-md-5 border"><strong>Опис</strong></div>
        <div class="col-md-2 border"><strong>Коефіцієнт</strong></div>
      </div>
      <div v-for="hiker in members" :key="'hiker-' + hiker.id">
        <hiker-item v-bind:hiker = "hiker"
                    v-on:update="updateMember"
                    v-on:remove="removeMember"/>
      </div>
    </div>
    <!--Errors output-->
    <div v-if="errorMessage !== null" class="alert">
      <p>{{errorMessage}}</p>
    </div>
    <!--Add new hiker section-->
    <b-button variant="link" size="sm" v-on:click="addMode = !addMode">Додати учасника</b-button>
    <div v-if="addMode !== undefined && addMode" class="container">
      <div class="row">
        <div class="col-md-4"/>
        <div class="col-md-2 border bg-light"><strong>Ім'я</strong></div>
        <div>
          <input v-validate="'required'" v-model="newHiker" name="newHiker"
                 v-bind:class="{ validationError: errors.has('newHiker')}"
                 placeholder='Введіть тут..' style="width: 100%"/>
          <p v-if="errors.has('newHiker') > 0" class="alert">{{errors.first('newHiker')}}</p>
        </div>
      </div>
      <div class="row" style="padding-top:10px;padding-bottom:15px">
        <div class="col-md-5"/>
        <div class="col-md-1">
          <b-button variant="outline-success" size="sm" v-on:click="addNewHiker">Додати</b-button>
        </div>
        <div class="col-md-1">
          <b-button variant="outline-danger" size="sm" v-on:click="addMode = false">Назад</b-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import HikerItem from 'src/components/plan/HikerItem.vue'

export default {
  name: 'HikersView',
  components: {HikerItem},
  props: {
    hikers: {
      type: Array,
      required: true
    }
  },

  data () {
    return {
      hikersEndpointUrl: '/api/plans/' + this.$route.params.planId + '/hikers/',
      addMode: false,
      members: this.hikers,
      newHiker: null,
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

    updateMember (member) {
      axios.put(this.hikersEndpointUrl + member.id, member)
        .then(() => {
          this.errorMessage = null
        })
        .catch(e => {
          this.getErrorMessage(e, 'Failed to update member ' + JSON.stringify(member))
        })
    },

    removeMember (hikerId) {
      axios.delete(this.hikersEndpointUrl + hikerId)
        .then(() => {
          this.members = this.members.filter(hiker => hiker.id !== hikerId)
          this.errorMessage = null
        })
        .catch(e => {
          let member = this.members.find(hiker => hiker.id === hikerId)
          this.getErrorMessage(e, 'Failed to delete ' + member.name)
        })
    },

    addNewHiker () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let hiker = {
            name: this.newHiker,
            weightCoefficient: 1.0
          }
          axios.post(this.hikersEndpointUrl, hiker)
            .then(response => {
              console.log('New Hiker - ' + JSON.stringify(response.data))
              this.addMode = false
              this.errorMessage = null
              this.newHiker = ''
              this.members.push(response.data)
            })
            .catch(e => {
              this.getErrorMessage(e, 'Failed to add member ' + JSON.stringify(hiker))
            })
        } else {
          console.log('Couldn\'t add new member due to validation errors')
        }
      })
    }
  }
}
</script>

<style scoped>
.members-header{
  text-align: center;
  color: #ff0045;
}
</style>
