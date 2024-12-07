<template>
  <div class="row">
    <div class="col-md-3">
      <input v-validate="'required'" v-model="hiker.name" name="hikerName"
             v-bind:class="{ validationError: errors.has('hikerName')}"
             placeholder='Введіть тут..' style="width: 100%"/>
      <p v-if="errors.has('hikerName') > 0" class="alert">{{errors.first('hikerName')}}</p>
    </div>
    <div class="col-md-5">
      <input v-model="hiker.description" name="hikerDescription" style="width: 100%"/>
    </div>
    <div class="col-md-2">
      <input type="number" min="0" step="0.1" v-model="hiker.weightCoefficient" name="hikerCoefficient" style="width: 100%"/>
    </div>
    <div class="col-sm-1">
      <b-button variant="outline-success" size="sm" v-on:click="updateHiker">Змін.</b-button>
    </div>
    <div class="col-sm-1">
      <b-button variant="outline-danger" size="sm" v-on:click="deleteHiker">Видал.</b-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HikerItem',
  props: {
    hiker: {
      type: Object,
      required: true
    }
  },

  methods: {
    updateHiker () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let member = {
            id: this.hiker.id,
            name: this.hiker.name,
            description: this.hiker.description,
            weightCoefficient: this.hiker.weightCoefficient
          }
          this.$emit('update', member)
        } else {
          console.log('Couldn\'t update Member due to validation errors')
        }
      })
    },

    deleteHiker () {
      this.$emit('remove', this.hiker.id)
    }
  }
}
</script>

<style scoped>

</style>
