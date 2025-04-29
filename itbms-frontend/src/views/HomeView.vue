<script setup>
import { fetchAllSaleItems } from '@/api';
import { onMounted } from 'vue';

  const fetchData = async () => {
    const data = await fetchAllSaleItems()
    console.log(data)
  }

  onMounted(async ()=>{
    await fetchData()
  })
</script>

<template>
  <main>
    Home
  </main>
</template>
