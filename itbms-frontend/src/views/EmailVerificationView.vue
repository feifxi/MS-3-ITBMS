<script setup>
import { verifyEmail } from '@/api';
import { useStatusMessageStore } from '@/stores/statusMessage';
import { BadgeCheck, BadgeX } from 'lucide-vue-next';
import { onMounted, ref } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';

const route = useRoute()
const router = useRouter()
const isLoading = ref(false)
const isSuccess = ref(false)

const sendEmailVerification = async () => {
  try {
    isLoading.value = true;

    const { token } = route.query
    if (!token) {
      router.push({ name: "Home" })
      return 
    }

    const res = await verifyEmail(token)
    const data = await res.json()
    // console.log(data);
    if (res.ok) {
      isSuccess.value = true
      
    } else throw new Error("Invalid credential.")
  } catch (err) {
    console.log(err);
    isSuccess.value = false
  }
  isLoading.value = false;
}

onMounted(() => {
  sendEmailVerification()
})

</script>

<template>
  <main class="px-4 sm:px-16 py-8">
    <div class="bg-white rounded-xl shadow">
      <div
        v-if="isLoading"
        class="text-center text-blue-500 animate-pulse text-3xl font-bold"
      >
        Loading...
      </div>

      <div v-else-if="isSuccess">
        <div class="px-16 py-32 flex flex-col gap-3 justify-center items-center">
          <BadgeCheck class="text-green-500 size-20" />
          <h2 class="text-3xl">Email Verification Success</h2>
          <p class="text-lg text-gray-500">
            Your email was verified. You can continue login  
            <RouterLink :to="{ name: 'login' }" class="text-purple-500 underline">here.</RouterLink>
          </p>
        </div>
      </div>

      <div v-else>
        <div class="px-16 py-32 flex flex-col gap-3 justify-center items-center">
          <BadgeX class="text-red-500 size-20" />
          <h2 class="text-3xl">Email Verification Failed</h2>
          <p class="text-lg text-gray-500">
            Invalid credential or varidation time expires. Go back to   
            <RouterLink :to="{ name: 'Home'}" class="text-purple-500 underline">Home page.</RouterLink>
          </p>
        </div>
      </div>
    </div>
  </main>
</template>
