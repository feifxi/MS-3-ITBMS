// ✅ store/auth.js
import { ref, computed } from "vue";
import { defineStore } from "pinia";

const BASE_API = import.meta.env.VITE_BASE_API;

export const useAuthStore = defineStore("auth", () => {
  const user = ref(null);
  const token = ref(localStorage.getItem("access_token") || null);
  const isLoadingAuth = ref(false);

  async function fetchUser() {
    if (!token.value) return;
    isLoadingAuth.value = true;
    const res = await fetch(`${BASE_API}/v2/auth/check-identity`, {
      headers: { Authorization: `Bearer ${token.value}` },
    });
    if (res.ok) {
      user.value = await res.json();
    } else {
      logout();
    }
    isLoadingAuth.value = false;
  }

  function logout() {
    token.value = null;
    user.value = null;
    localStorage.removeItem("access_token");
    localStorage.removeItem("refresh_token");
  }

  return { user, token, isLoadingAuth, fetchUser, logout };
});
