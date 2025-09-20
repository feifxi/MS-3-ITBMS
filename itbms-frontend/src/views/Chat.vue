<script setup>
import { ref, onMounted, watch, nextTick } from "vue"
import { Client } from "@stomp/stompjs"

const BASE_WS_API = import.meta.env.VITE_BASE_API

const messages = ref([])
const message = ref("")
const chatWindow = ref(null)
const username = "User" + Math.floor(Math.random() * 1000)

const client = new Client({
  brokerURL: `ws://localhost:8080/itb-mshop/ws`, // Spring Boot WebSocket endpoint
  reconnectDelay: 5000, // auto reconnect every 5s
  heartbeatIncoming: 4000,
  heartbeatOutgoing: 4000,
  onConnect: () => {
    client.subscribe("/topic/messages", (msg) => {
      messages.value.push(JSON.parse(msg.body))
    })
  },
  onStompError: (frame) => {
    console.error("Broker error:", frame.headers["message"])
  }
})

onMounted(() => {
  client.activate()
})

// auto scroll to bottom on new message
watch(messages, async () => {
  await nextTick()
  if (chatWindow.value) {
    chatWindow.value.scrollTop = chatWindow.value.scrollHeight
  }
})

function sendMessage() {
  if (message.value.trim() !== "") {
    client.publish({
      destination: "/app/chat",
      body: JSON.stringify({ from: username, text: message.value }),
    })
    message.value = ""
  }
  console.log("Kuy jerb")
}
</script>

<template>
  <div class="flex flex-col max-w-md mx-auto h-screen bg-gray-100 rounded-2xl shadow-lg">
    <!-- Header -->
    <div class="p-4 bg-blue-600 text-white rounded-t-2xl font-bold text-center">
      Realtime Chat
    </div>

    <!-- Messages -->
    <div ref="chatWindow" class="flex-1 overflow-y-auto p-4 space-y-3">
      <div
        v-for="(msg, i) in messages"
        :key="i"
        class="max-w-[70%] p-3 rounded-2xl"
        :class="msg.from === username 
          ? 'bg-blue-500 text-white ml-auto rounded-br-none' 
          : 'bg-gray-300 text-gray-900 mr-auto rounded-bl-none'"
      >
        <p class="text-sm font-semibold">{{ msg.from }}</p>
        <p>{{ msg.text }}</p>
      </div>
    </div>

    <!-- Input -->
    <div class="flex items-center p-3 border-t bg-white rounded-b-2xl">
      <input
        v-model="message"
        type="text"
        placeholder="Type a message..."
        class="flex-1 border rounded-lg px-3 py-2 mr-2 focus:outline-none focus:ring focus:ring-blue-300"
        @keyup.enter="sendMessage"
      />
      <button
        @click="sendMessage"
        class="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition"
      >
        Send
      </button>
    </div>
  </div>
</template>