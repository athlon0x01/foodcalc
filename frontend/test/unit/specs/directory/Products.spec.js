import Vue from 'vue'
import Products from 'src/components/directory/Products'

describe('Products.vue', () => {
  it('should render correct contents', () => {
    const Constructor = Vue.extend(Products)
    const vm = new Constructor().$mount()
    expect(vm.$el.querySelector('h2').textContent)
      .to.equal('Products list')
  })
})
