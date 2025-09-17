<script setup>
import { useAuthStore } from "@/stores/auth";
import { Bot, Send, User } from "lucide-vue-next";
import { ref, watch, nextTick } from "vue";
import Button from "./Button.vue";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { askChatbot } from "@/api";
import ConfirmModal from "./ConfirmModal.vue";
import { useRouter } from "vue-router";

const auth = useAuthStore();
const statusMessageStore = useStatusMessageStore();
const router = useRouter()

const isShowingChatbot = ref(false);
const inputMessage = ref("");
const messages = ref([]);
const isTyping = ref(false);
const messagesEndRef = ref(null);

const handleSendMessage = async (e) => {
  e.preventDefault();

  if (!auth.user) {
    handleShowDialog();
    return;
  }

  const question = inputMessage.value;
  if (!question.trim()) return;

  const userMessage = {
    id: Date.now().toString(),
    text: question,
    sender: "user",
    timestamp: new Date(),
  };

  messages.value = [...messages.value, userMessage];
  inputMessage.value = "";
  isTyping.value = true;

  try {
    const res = await askChatbot(question, auth);
    const result = await res.json();
    // console.log(result);
    if (!res.ok) throw new Error("Something went wrong");
    const botMessage = {
      id: (Date.now() + 1).toString(),
      text: result.message,
      sender: "bot",
      timestamp: new Date(),
    };
    messages.value = [...messages.value, botMessage];
  } catch (err) {
    console.log(err);
    statusMessageStore.setStatusMessage("Something went wrong.", false);
  }
  isTyping.value = false;
};

const handleToggleChatbotBox = () => {
  isShowingChatbot.value = !isShowingChatbot.value;
};

const scrollToBottom = () => {
  if (messagesEndRef.value) {
    messagesEndRef.value.scrollIntoView({ behavior: "smooth" });
  }
};

watch(
  messages,
  () => {
    nextTick(() => {
      scrollToBottom();
    });
  },
  { deep: true }
);

const showLoginSuggestDialog = ref(false);

const handleShowDialog = () => {
  showLoginSuggestDialog.value = true;
};

const handleCloseDialog = () => {
  showLoginSuggestDialog.value = false;
};

const goToSignin = async () => {
  handleCloseDialog();
  isShowingChatbot.value = false
  router.push({ name: "login" });
};
</script>

<template>
  <div
    v-if="isShowingChatbot"
    class="fixed z-50 bottom-0 right-[10%] flex flex-col h-[600px] w-full max-w-sm mx-auto border border-neutral-300 rounded-lg overflow-hidden bg-white"
  >
    <!-- Header -->
    <div
      @click="handleToggleChatbotBox"
      class="cursor-pointer flex items-center gap-3 p-4 border-b border-neutral-300 bg-gradient-to-r from-purple-500 to-rose-500"
    >
      <div
        class="w-10 h-10 rounded-full bg-white flex items-center justify-center"
      >
        <Bot class="w-6 h-6" />
      </div>
      <div>
        <h3 class="font-semibold text-white">Chanombude Bot</h3>
        <p class="text-sm text-white">Online • Ready to brainrot</p>
      </div>
    </div>

    <!-- Messages -->
    <div class="flex-1 overflow-y-auto p-4 space-y-4">
      <div
        v-for="message of messages"
        :key="message.id"
        :class="
          'flex items-start gap-3 animate-bounce-in ' +
          (message.sender === 'user' ? 'flex-row-reverse' : 'flex-row')
        "
      >
        <div
          :class="
            'w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 ' +
            (message.sender === 'user'
              ? 'bg-blue-500'
              : 'bg-gradient-to-r from-purple-500 to-rose-500')
          "
        >
          <User v-if="message.sender === 'user'" class="w-4 h-4 text-white" />
          <Bot v-else class="w-4 h-4 text-white" />
        </div>
        <div
          :class="
            'max-w-[80%] p-3 rounded-lg shadow-sm ' +
            (message.sender === 'user'
              ? 'text-white bg-blue-500 rounded-br-sm'
              : 'text-white bg-purple-500 rounded-bl-sm')
          "
        >
          <p class="text-sm leading-relaxed">{{ message.text }}</p>
          <p class="text-xs opacity-70 mt-1">
            {{
              message.timestamp.toLocaleTimeString([], {
                hour: "2-digit",
                minute: "2-digit",
              })
            }}
          </p>
        </div>
      </div>

      <!-- Typing Indicator -->
      <div v-if="isTyping" class="flex items-start gap-3 animate-fade-in">
        <div
          class="w-8 h-8 rounded-full flex items-center justify-center bg-gradient-to-r from-purple-500 to-rose-500"
        >
          <Bot class="w-4 h-4 text-white" />
        </div>
        <div class="bg-purple-500 p-3 rounded-lg rounded-bl-sm shadow-sm">
          <div class="flex gap-1">
            <div
              class="w-2 h-2 bg-white rounded-full animate-bounce [animation-delay:-0.3s]"
            ></div>
            <div
              class="w-2 h-2 bg-white rounded-full animate-bounce [animation-delay:-0.15s]"
            ></div>
            <div class="w-2 h-2 bg-white rounded-full animate-bounce"></div>
          </div>
        </div>
      </div>

      <div ref="messagesEndRef"></div>
    </div>

    <!-- Input -->
    <div class="p-3 border-t border-neutral-300">
      <form @submit="handleSendMessage" class="flex gap-2">
        <input
          type="text"
          placeholder="Type your message..."
          class="flex-1 bg-white border-neutral-300 focus:border-purple-500 focus:ring-purple-500 rounded-lg p-2"
          v-model="inputMessage"
          :disabled="isTyping"
        />
        <Button
          :variant="'primary'"
          :disabled="!inputMessage.trim() || isTyping"
        >
          <Send class="w-4 h-4" />
        </Button>
      </form>
    </div>
  </div>

  <div
    v-else
    @click="handleToggleChatbotBox"
    class="fixed cursor-pointer bottom-0 right-[2%] flex flex-col mx-auto border border-neutral-300 rounded-lg overflow-hidden"
  >
    <div
      class="flex items-center gap-3 p-2 border-b border-neutral-300 bg-gradient-to-r from-purple-500 to-rose-500"
    >
      <div
        class="w-8 h-8 rounded-full bg-white flex items-center justify-center"
      >
        <Bot class="w-6 h-6" />
      </div>
      <div>
        <h3 class="font-semibold text-white">Chanombude Bot</h3>
      </div>
    </div>
  </div>

  <ConfirmModal
    v-if="showLoginSuggestDialog"
    :title="'Login require'"
    :message="`Login is require to use this feature`"
    :button-label="'Sign in'"
    @confirm="goToSignin"
    @cancel="handleCloseDialog"
  />
</template>
