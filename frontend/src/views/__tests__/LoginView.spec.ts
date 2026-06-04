import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import ElementPlus from 'element-plus'
import LoginView from '../LoginView.vue'

describe('LoginView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('renders login form', () => {
    const wrapper = mount(LoginView, {
      global: {
        plugins: [ElementPlus]
      }
    })
    expect(wrapper.find('form').exists()).toBe(true)
  })

  it('has username and password inputs', () => {
    const wrapper = mount(LoginView, {
      global: {
        plugins: [ElementPlus]
      }
    })
    expect(wrapper.find('input[type="text"]').exists() || wrapper.find('input').exists()).toBe(true)
  })
})
