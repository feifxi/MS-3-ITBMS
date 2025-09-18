<script setup>
import { useAuthStore } from "@/stores/auth";
import { Bot, Gamepad2, Send, User, X } from "lucide-vue-next";
import { ref, watch, nextTick, onMounted, onBeforeUnmount } from "vue";
import Button from "./Button.vue";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { askChatbot } from "@/api";
import { useRouter } from "vue-router";
import ConfirmModal from "./ConfirmModal.vue";

const auth = useAuthStore();
const statusMessageStore = useStatusMessageStore();
const router = useRouter();

const isShowingGame = ref(false);
const choices = ref(["", "", "", "", "", "", "", "", ""]);
const whoseTurn = ref("X");
const isLoading = ref(false);
const isEndGame = ref(false)
const isWithDraw = ref(false)
const botMessage = ref("")

const handleClick = async (index) => {
  try {
    if (!auth.user) {
      handleShowDialog();
      return;
    }
    if (isEndGame.value || isLoading.value || choices.value[index] || whoseTurn.value === "O") return

    choices.value[index] = whoseTurn.value

    // Winner
    if (calculateWinner()) {
      isEndGame.value = true
      botMessage.value = "Just lucky noob, let try again!!"
      return
    } 
    // Withdraw
    else if (choices.value.every(choice => choice !== "")) {
      isEndGame.value = true
      isWithDraw.value = true
      botMessage.value = "Nahhh.. Let try again noob"
      return
    }
    const emptySlot = choices.value.map((v,i ) => v === "" ? i : null).filter(i => i !== null);
    const prompt = `
      You are a Tic-Tac-Toe AI. Answer ONLY in JSON format as shown in the example. 
      Do not write anything else. Do not overwrite existing X or O. Your mark is "O", human is "X".

      Current board: ${JSON.stringify(choices.value)}
      Empty cells: ${JSON.stringify(emptySlot)}

      Follow these strategy rules in order:
      1. If the human can win in their next move, you MUST block that cell.
      2. If you can win in this move, choose the winning cell.
      3. If the center cell (index 4) is empty, pick it.
      4. Pick any available corner (indices 0, 2, 6, 8).
      5. Pick any remaining empty cell.

      Example output:
      {
        "move": 5,
        "message": "Good luck! Let's continue!"
      }

      Now choose your move for the current board and return JSON ONLY in the same format.
      Keep the "message" short and friendly.
      Answer ONLY in JSON. Do NOT include any other text.
    `;

    // console.log(prompt)
    
    whoseTurn.value = "O"
    isLoading.value = true;
    const res = await askChatbot(prompt, auth);
    if (!res.ok) throw new Error("Something went wrong");
    const result = await res.json();
    
    // console.log(result);

    let botAnswer = JSON.parse(result.message)
    let botMoveIndex = botAnswer?.move
    botMessage.value = botAnswer.message

    // if overwrite - pick a random empty cell instead
    if (choices.value[botMoveIndex] !== "") {  
      botMoveIndex = emptySlot[Math.floor(Math.random() * emptySlot.length)];
    }
    // Choose 
    choices.value[botMoveIndex] = "O"

    // Winner
    if (calculateWinner()) {
      isEndGame.value = true
      isLoading.value = false;
      botMessage.value = "Muhahaha Loser"
      return
    } 
    // Withdraw
    else if (choices.value.every(choice => choice !== "")) {
      isEndGame.value = true
      isWithDraw.value = true
      isLoading.value = false;
      botMessage.value = "Nahhh.. Let try again noob"
      return
    }

  } catch (err) {
    console.log(err);
    statusMessageStore.setStatusMessage("Something went wrong.", false);
    isEndGame.value = true
  }
  isLoading.value = false;
  whoseTurn.value = "X"
};

const calculateWinner = () => {
    const lines = [
      [0, 1, 2],
      [3, 4, 5],
      [6, 7, 8],
      [0, 3, 6],
      [1, 4, 7],
      [2, 5, 8],
      [0, 4, 8],
      [2, 4, 6],
    ];
    for (let i = 0; i < lines.length; i++) {
      const [a, b, c] = lines[i];
      if (choices.value[a] && choices.value[a] === choices.value[b] && choices.value[a] === choices.value[c]) {
        return choices.value[a];
      }
    }
    return null;
  };

  const resetGame = () => {
    choices.value = ["", "", "", "", "", "", "", "", ""]
    whoseTurn.value = "X"
    isEndGame.value = false
    isWithDraw.value = false
  }

  const test = () => {
    console.log(JSON.stringify(choices.value))
  }

  const showLoginSuggestDialog = ref(false);

  const handleShowDialog = () => {
    showLoginSuggestDialog.value = true;
  };

  const handleCloseDialog = () => {
    showLoginSuggestDialog.value = false;
  };

  const goToSignin = async () => {
    handleCloseDialog();
    isShowingGame.value = false
    router.push({ name: "login" });
  };

</script>

<template>
  <section
    v-if="isShowingGame"
    class="z-50 fixed top-0 left-0 bg-black/80 w-full h-screen flex flex-col justify-center items-center gap-10"
  >
    <!-- Chat Avatar -->
    <div class="flex items-start gap-1" >
      <div class="bg-white p-3 rounded-full" @click="test">
        <Bot class="w-10 h-10"></Bot>
      </div>

      <div v-if="isLoading" class="bg-purple-500 p-3 rounded-lg rounded-bl-sm shadow-sm">
        <div class="flex gap-1">
          <div class="w-3 h-3 bg-white rounded-full animate-bounce [animation-delay:-0.3s]" ></div>
          <div class="w-3 h-3 bg-white rounded-full animate-bounce [animation-delay:-0.15s]"></div>
          <div class="w-3 h-3 bg-white rounded-full animate-bounce"></div>
        </div>
      </div>
      <div v-else-if="botMessage" class="bg-purple-500 p-3 rounded-lg rounded-bl-sm shadow-sm text-white font-bold">
       {{ botMessage }}
      </div>
    </div>

    <!-- Tic Tac Toe board -->
    <div
      :class="'relative border border-neutral-300 bg-white p-5 rounded-lg w-full max-w-sm shadow'"
    >
      <div
        @click="isShowingGame = false"
        class="absolute -right-2 -top-2 bg-white w-10 h-10 cursor-pointer flex items-center justify-center border border-neutral-300 shadow rounded-full"
      >
        <X />
      </div>
      <button v-if="isEndGame" @click="resetGame" class="absolute -bottom-10 right-0 bg-red-500 hover:bg-red-600 transition-all text-white py-3 px-5 rounded-lg cursor-pointer">
        RESET GAME
      </button>

      <h1 class="text-4xl font-bold bg-purple-400 p-2 rounded-xl text-center">
        <p v-if="isWithDraw">
          Withdraw!
        </p>
        <p v-else-if="isEndGame">
          <span :class="' bg-white rounded-lg px-2 ' + (whoseTurn === 'X' ? 'text-red-500': 'text-blue-500')">{{ whoseTurn === "X" ? "You" : "Bot" }}</span> Win!
        </p>
        <p v-else>
          <span :class="' bg-white rounded-lg px-2 ' + (whoseTurn === 'X' ? 'text-red-500': 'text-blue-500')">{{ whoseTurn === "X" ? "Your" : "Bot" }}</span> Turn
        </p>
      </h1>
      <div class="border-2 grid grid-cols-3 mt-5">
        <div
          v-for="(choice, index) in choices"
          :class="
            'border border-black h-30 flex items-center justify-center text-8xl ' +
            ((choice || isLoading) ? 'cursor-not-allowed ' : 'cursor-pointer ') +
            ((choice === 'X') ? 'text-red-500 ' : 'text-blue-500 ')"
          @click="() => handleClick(index)"
        >
          {{ choice }}
        </div>
      </div>
    </div>
  </section>

  <div @click="isShowingGame = true" v-else class="z-50 fixed bottom-20 right-5  bg-gradient-to-r from-purple-500 to-rose-500 p-2 shadow cursor-pointer rounded-full">
    <Gamepad2 class="text-white" />
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
