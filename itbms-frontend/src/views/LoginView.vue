<script setup>
import { ref, reactive, watch } from "vue";
import { useRouter } from "vue-router";
import Button from "@/components/Button.vue";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { useAuthStore } from "@/stores/auth";

const router = useRouter();
const statusMessageStore = useStatusMessageStore();
const authStore = useAuthStore();

const userData = reactive({
  email: "",
  password: "",
});
const isLoading = ref(false);
const isSubmitting = ref(false);

const currentFocusField = ref(null);
const isFormValid = ref(false);
const errorFormMessage = reactive({
  email: "",
  password: "",
});

const fieldIntegrity = {
  email: {
    checkConstraint: (data) => {
      return 0 < data.length && data.length <= 40 && /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(data);
    },
    errorMessage: "Invalid email.",
  },
  password: {
    checkConstraint: (data) => {
      return 7 < data.length && data.length <= 40;
    },
    errorMessage: "Password must be 8-40 characters long.",
  },
};

const handleFocusIn = (e) => {
  currentFocusField.value = e.target.name;
};

const handleFocusOut = (e) => {
  const currentField = e.target.name;
  if (typeof userData[currentField] === "string") {
    userData[currentField] = userData[currentField].trim();
  }
  showErrorToForm();
  currentFocusField.value = null;
};

const checkFieldIntegrity = (field) => {
  if (fieldIntegrity[field]) {
    return fieldIntegrity[field]?.checkConstraint(userData[field]);
  } else {
    return true;
  }
};

const validateAllField = () => {
  let isValid = true;
  for (const field in userData) {
    if (!checkFieldIntegrity(field)) {
      isValid = false;
      break;
    }
  }
  isFormValid.value = isValid;
};

const submitForm = async (e) => {
  e.preventDefault();
  isSubmitting.value = true;
  try {
    const success = await authStore.login(userData.email, userData.password)
    if (success) {
      router.push({ name: "SaleItemGallery" });
    } 
  } catch (err) {
    console.log(err);
    router.push({ name: "SaleItemGallery" });
  }
  isSubmitting.value = false;
};

const goBackHome = () => {
  router.push("/sale-items");
};

const showErrorToForm = () => {
  const field = currentFocusField.value;
  if (field) {
    // console.log(field)
    errorFormMessage[field] = checkFieldIntegrity(field)
      ? ""
      : fieldIntegrity[field]?.errorMessage;
  }
};

const navigateToRegister = () => {
  router.push({ name: "register" });
};

watch(userData, () => {
  showErrorToForm();
  // Disabled save button
  validateAllField();
  // console.log(userData)
});
</script>

<template>
  <main class="px-4 sm:px-16 py-8">
    <div>
      <div
        v-if="isLoading"
        class="text-center text-blue-500 animate-pulse text-3xl font-bold"
      >
        Loading...
      </div>

      <div v-else class="flex">
        <!-- Side Image -->
        <img
          :src="'https://images.stockcake.com/public/c/4/6/c4678895-7ad4-4670-a61d-923fa66e6df9_large/online-shopping-app-stockcake.jpg'"
          alt="side image"
          :class="'shadow-md flex-1 max-lg:hidden'"
        />

        <div class="flex-1 flex flex-col bg-white max-lg:rounded-xl max-lg:shadow-lg p-6">
          <div class="flex-1 p-3 flex flex-col">
            <h2 class="text-5xl font-bold text-center mt-10">Sign in</h2>
            <!-- User Data Form -->
            <form @submit="submitForm" class="flex flex-col gap-3">
              <div class="flex flex-col gap-1">
                <label>
                  <span class="text-red-500 text-xl">*</span>
                  Email
                </label>
                <input
                  name="email"
                  type="text"
                  class="itbms-email input"
                  placeholder="Email..."
                  v-model="userData.email"
                  @focusin="handleFocusIn"
                  @focusout="handleFocusOut"
                />
                <p class="itbms-message text-red-500 pl-2">
                  {{ errorFormMessage["email"] }}
                </p>
              </div>

              <div class="flex flex-col gap-1">
                <label>
                  <span class="text-red-500 text-xl">*</span>
                  Password
                </label>
                <input
                  name="password"
                  type="text"
                  class="itbms-password input"
                  placeholder="Password..."
                  v-model="userData.password"
                  @focusin="handleFocusIn"
                  @focusout="handleFocusOut"
                />
                <p class="itbms-message text-red-500 pl-2">
                  {{ errorFormMessage["password"] }}
                </p>
              </div>

              <div class="flex flex-col gap-3 mt-5">
                <Button
                  variant="primary"
                  :onclick="submitForm"
                  :disabled="isSubmitting || !isFormValid"
                  class-name="itbms-save-button shadow-lg drop-shadow-[0_1px_1px_rgba(0,0,0,1)]"
                  type="submit"
                >
                  {{ isSubmitting ? "Loading..." : "Sign in" }}
                </Button>
                <button
                  type="button"
                  @click="navigateToRegister"
                  class="text-purple-600 cursor-pointer hover:underline"
                >
                  dont have an account?
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>
