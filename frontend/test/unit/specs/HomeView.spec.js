import Vue from 'vue'
import HomeView from '@/components/HomeView'

describe('HomeView.vue', () => {
  it('should render correct contents', () => {
    const Constructor = Vue.extend(HomeView)
    const vm = new Constructor().$mount()
    expect(vm.$el.querySelector('h2').textContent)
      .to.equal('Outdoor Food Calculator welcome page')
  })
})
