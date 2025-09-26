// src/stores/auth.js
import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { useStatusMessageStore } from './statusMessage'

const BASE_API = import.meta.env.VITE_BASE_API

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref(null)
  const user = ref(null)

  const login = async (email, password) => {
    const statusMessageStore = useStatusMessageStore();
    const res = await fetch(`${BASE_API}/v2/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
      credentials: 'include' // ⬅️ this allows refreshToken cookie to be set
    })

    if (res.ok) {
      const data = await res.json()
      accessToken.value = data.accessToken
      user.value = {
        id: data.id,
        userType: data.userType,
        email: data.email,
        nickname: data.nickname,
        fullName: data.fullName,
        isLocked: data.isLocked,
        roles: data.roles,
      }
      // console.log(accessToken.value)
      statusMessageStore.setStatusMessage("Login success.");
      return {
        success: true,
        userType: data.userType,
        nickname: data.nickname,
      }
    } else if (res.status === 403) {
      statusMessageStore.setStatusMessage("Your email address is not verified. Please check your inbox to verify your account.", false);
      return {
        success: false,
        userType: null,
        nickname: null,
      }
    } else {
      statusMessageStore.setStatusMessage("Email or password is incorrect.", false);
      return {
        success: false,
        userType: null,
        nickname: null,
      } 
    }
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

    if (!res.ok) throw new Error('Signout failed.')
    const data = await res.json()

    accessToken.value = null
    user.value = null

    console.log('Signout success : ', data)
  }

  return {
    accessToken,
    user,
    login,
    logout
  }
})
