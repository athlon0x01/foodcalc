import Vue from 'vue'
import AboutPage from 'src/components/AboutPage'

describe('AboutPage.vue', () => {
  it('should render correct contents', () => {
    const Constructor = Vue.extend(AboutPage)
    const vm = new Constructor().$mount()
    expect(vm.$el.querySelector('h2').textContent)
      .to.equal('Outdoor Food Calculator About page')
  })
})
