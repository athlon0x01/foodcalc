<template>
  <div class="row">
    <div class="col-md-3">
      <input v-validate="'required'" v-model="foodPackage.name" name="packageName"
             v-bind:class="{ validationError: errors.has('packageName')}"
             placeholder='Enter member name here..' style="width: 100%"/>
      <p v-if="errors.has('packageName') > 0" class="alert">{{errors.first('packageName')}}</p>
    </div>
    <div class="col-md-5">
      <input v-model="foodPackage.description" name="packageDescription" style="width: 100%"/>
    </div>
    <div class="col-md-1">
      <input type="number" min="0" step="0.1" v-model="foodPackage.volumeCoefficient" name="packageCoefficient" style="width: 100%"/>
    </div>
    <div class="col-md-1">
      <input type="number" min="0" step="0.1" v-model="foodPackage.additionalWeight" name="packageWeight" style="width: 100%"/>
    </div>
    <div class="col-sm-1">
      <b-button variant="outline-success" size="sm" v-on:click="updatePackage">Update</b-button>
    </div>
    <div class="col-sm-1">
      <b-button variant="outline-danger" size="sm" v-on:click="deletePackage">Delete</b-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FoodPackageItem',
  props: {
    foodPackage: {
      type: Object,
      required: true
    }
  },

  methods: {
    updatePackage () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          let thePackage = {
            id: this.foodPackage.id,
            name: this.foodPackage.name,
            description: this.foodPackage.description,
            volumeCoefficient: this.foodPackage.volumeCoefficient,
            additionalWeight: this.foodPackage.additionalWeight
          }
          this.$emit('update', thePackage)
        } else {
          console.log('Couldn\'t update Package due to validation errors')
        }
      })
    },

    deletePackage () {
      this.$emit('remove', this.foodPackage.id)
    }
  }
}
</script>

<style scoped>

</style>
