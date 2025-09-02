<script setup>
import { useRouter } from "vue-router";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { useAuthStore } from "@/stores/auth";
import { onMounted, ref } from "vue";
import { fetchUserProfile } from "@/api";

const router = useRouter();
const statusMessageStore = useStatusMessageStore();
const auth = useAuthStore()

const userProfile = ref(null)
const isLoading = ref(false)

const loadUserProfile =  async () => {
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


// redirect if user is not logged in
const redirectIfNotSeller = () => {
  if (auth.user?.userType !== "SELLER") {
    router.push({ name: "SaleItemGallery" })
  }
}

onMounted(async () => {
    redirectIfNotSeller()
    loadUserProfile()
})

</script>

<template>
  <div class="px-16 py-8">
    <div class="bg-white px-16 py-8 rounded-xl shadow">
        <h1 class=" text-3xl font-bold">User Profile</h1>
        <p>Nickname : {{ auth.user?.nickname || "-"}}</p>
        <p>Fullname : {{ auth.user?.fullName || "-"}}</p>
        <p>Email : {{ auth.user?.email || "-"}}</p>
        <p>User type : {{ auth.user?.userType || "-"}}</p>
    </div>
  </div>
</template>
