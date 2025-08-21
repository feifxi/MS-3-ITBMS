<script setup>
import { ref } from "vue";
import SellerRegisterForm from "@/components/SellerRegisterForm.vue";
import BuyerRegisterForm from "@/components/BuyerRegisterForm.vue";

const USER_TYPE_TABS = ["BUYER", "SELLER"];

const currentTab = ref(USER_TYPE_TABS[0]);

const handleChangeTab = (tab) => {
    currentTab.value = tab
}
</script>

<template>
  <main class="px-4 sm:px-16 pb-8">
    <div class="">
      <div class="flex bg-white">
        <div 
            v-for="tab in USER_TYPE_TABS"
            @click="handleChangeTab(tab)"
            :class="'py-4 px-12 cursor-pointer font-bold ' + (currentTab === tab ? 'border-b-purple-600 text-purple-600 border-b-[4px]' : '')">
            {{ tab }}
        </div>
      </div>
      <div class="mt-3">
        <BuyerRegisterForm v-if="currentTab === 'BUYER'"></BuyerRegisterForm>
        <SellerRegisterForm v-if="currentTab === 'SELLER'"></SellerRegisterForm>
      </div>
    </div>
  </main>
</template>
