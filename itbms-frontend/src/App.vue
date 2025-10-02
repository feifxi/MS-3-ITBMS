<script setup>
import { RouterView, useRouter } from "vue-router";
import Navbar from "./components/Navbar.vue";
import ToastMessage from "./components/ToastMessage.vue";
import { useAuthStore } from "./stores/auth";
import { onMounted, ref } from "vue";
import { fetchWithAuth, refreshAccessToken } from "./api";
import Chatbot from "./components/Chatbot.vue";
import TicTacToeBot from "./components/TicTacToeBot.vue";
import { useCartStore } from "./stores/cart";

const router = useRouter();
const auth = useAuthStore();
const cartStore = useCartStore();

const isLoading = ref(false);

const loadProfile = async () => {
  try {
    isLoading.value = true;
    // fetch access token
    await refreshAccessToken(auth);

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
      roles: profile.roles,
      userType: profile.userType,
      isLocked: profile.isLocked,
    };

    // console.log("Logged in as:", profile);
  } catch (err) {
    cartStore.clearCart();
    console.error("Auth error:", err.message);
  } finally {
    isLoading.value = false;
  }
};

loadProfile();
</script>

<template>
  <!-- Loading State -->
  <div v-if="isLoading" class="text-center flex flex-col items-center justify-center h-screen">
    <div
      class="inline-block animate-spin rounded-full h-16 w-16 border-4 border-purple-200 border-t-purple-600 mb-4"
    ></div>
    <p class="text-gray-600 text-lg">Loading...</p>
  </div>

  <!-- Page View -->
  <main v-else class="relative bg-gradient-to-br from-rose-50 to-purple-50">
    <Navbar />
    <RouterView />
    <Chatbot />
    <TicTacToeBot />
    <ToastMessage />
  </main>
</template>

<style scoped></style>
