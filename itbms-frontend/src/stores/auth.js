// src/stores/auth.js
import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

const BASE_API = import.meta.env.VITE_BASE_API

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref(null)
  const user = ref(null)

  const isAuthenticated = computed(() => !!accessToken.value && !!user.value)

  const login = async (email, password) => {
    const res = await fetch(`${BASE_API}/v2/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
      credentials: 'include' // ⬅️ this allows refreshToken cookie to be set
    })

    if (!res.ok) return false

    const data = await res.json()
    
    accessToken.value = data.accessToken
    user.value = {
      id: data.id,
      email: data.email,
      nickname: data.nickname,
      fullName: data.fullName,
      isLocked: data.isLocked,
      roles: data.roles,
    }

    // console.log(accessToken.value)
    return true
  }

  const logout = async () => {
    if (!accessToken.value) {
      accessToken.value = null
      user.value = null
      throw new Error('User not logged in.')
    }
    
    const res = await fetch(`${BASE_API}/v2/auth/logout`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${accessToken.value}`
      },
      credentials: "include",
    })

    if (!res.ok) throw new Error('Logout failed.')
    const data = await res.json()

    accessToken.value = null
    user.value = null

    console.log('Logout success : ', data)
  }

  return {
    accessToken,
    user,
    isAuthenticated,
    login,
    logout
  }
})
