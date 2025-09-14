<script setup>
import { useRouter } from "vue-router";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { useAuthStore } from "@/stores/auth";
import { onMounted, ref } from "vue";
import { fetchUserProfile, updateUserProfile } from "@/api";

const router = useRouter();
const statusMessageStore = useStatusMessageStore();
const auth = useAuthStore()

const userProfile = ref(null)
const isLoading = ref(false)

const loadCartData =  async () => {
  try {
    isLoading.value = true
    const res = await fetchUserProfile(auth)
    const profile = await res.json()
    userProfile.value = profile

    console.log(profile)  // print user profile
  } catch (error) {
    console.log(error)
  }
}


onMounted(async () => {
  loadCartData()
})

</script>

<template>
  <div class="px-16 py-8">
    <div class="bg-white px-16 py-8 rounded-xl shadow">
        <h1 class=" text-3xl font-bold">Cart</h1>
        
    </div>
  </div>
</template>
