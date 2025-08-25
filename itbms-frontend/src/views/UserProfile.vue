<script setup>
import { useRouter } from "vue-router";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { useAuthStore } from "@/stores/auth";

const router = useRouter();
const statusMessageStore = useStatusMessageStore();
const auth = useAuthStore()

</script>

<template>
  <div class="px-16 py-8">
    <div class="bg-white px-16 py-8 rounded-xl shadow">
        <h1 class=" text-3xl font-bold">User Profile</h1>
        <p>Nickname : {{ auth.user?.nickname || "-"}}</p>
        <p>Fullname : {{ auth.user?.fullName || "-"}}</p>
        <p>Email : {{ auth.user?.email || "-"}}</p>

    </div>
  </div>
</template>
