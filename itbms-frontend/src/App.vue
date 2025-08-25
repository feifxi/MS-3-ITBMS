<script setup>
import { RouterView, useRouter } from 'vue-router'
import Navbar from './components/Navbar.vue';
import ToastMessage from './components/ToastMessage.vue';
import { useAuthStore } from './stores/auth';
import {onMounted, ref } from 'vue';
import { fetchWithAuth, refreshAccessToken } from './api';

const router = useRouter()
const auth = useAuthStore()
const isLoading = ref(false)

const loadProfile = async () => {
  isLoading.value = true
  try {
    // fetch access token
    await refreshAccessToken(auth)

    // fetch user profile
    const res = await fetchWithAuth(`/v2/auth/me`, {}, auth);
    if (!res.ok) {
      throw new Error("Failed to load user profile");
    }
    const profile = await res.json();
    auth.user = {
      id: profile.id,
      email: profile.email,
      nickname: profile.nickname,
      fullName: profile.fullName,
      isLocked: profile.isLocked,
      roles: profile.roles,
    }

    // console.log("Logged in as:", profile);
  } catch (err) {
    if (err.message === "Session expired, please log in again." || err.message === "Invalid token or expires.") {
      // router.push({ name: "login" })
    }
    console.error("Auth error:", err.message);
  } finally {
    isLoading.value = false
  }
}


loadProfile()
</script>

<template>
  <!-- Auth Loading -->
  <main v-if="isLoading" class="text-center text-blue-500 animate-pulse text-3xl font-bold mt-10">
    Loading...
  </main>
  <!-- Page View -->
  <main v-else class="relative bg-gradient-to-br from-rose-50 to-purple-50">
    <Navbar />
    <RouterView />
    <ToastMessage />
  </main>
</template>

<style scoped></style>
