import Vue from 'vue'
import ProductCategories from 'src/components/directory/ProductCategories'

describe('ProductCategories.vue', () => {
  it('should render correct contents', () => {
    const Constructor = Vue.extend(ProductCategories)
    const vm = new Constructor().$mount()
    expect(vm.$el.querySelector('h2').textContent)
      .to.equal('Product categories:')
  })
})
