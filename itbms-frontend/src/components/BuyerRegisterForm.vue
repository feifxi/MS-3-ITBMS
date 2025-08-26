<script setup>
import { ref, reactive, watch } from "vue";
import { useRouter } from "vue-router";
import { fetchWithAuth, registerUser } from "../api";
import Button from "@/components/Button.vue";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { useAuthStore } from "@/stores/auth";

const router = useRouter();
const statusMessageStore = useStatusMessageStore();

const userData = reactive({
  nickname: "",
  fullName: "",
  email: "",
  password: "",
});
const isLoading = ref(false);
const isSubmitting = ref(false);

const currentFocusField = ref(null);
const isFormValid = ref(false);
const errorFormMessage = reactive({
  nickname: "",
  fullName: "",
  email: "",
  password: "",
});

const fieldIntegrity = {
  nickname: {
    checkConstraint: (data) => {
      return 0 < data.length && data.length <= 40;
    },
    errorMessage: "Nickname must be 1-40 characters long.",
  },
  fullName: {
    checkConstraint: (data) => {
      return 0 < data.length && data.length <= 40;
    },
    errorMessage: "Full Name must be 1-40 characters long.",
  },
  email: {
    checkConstraint: (data) => {
      return 0 < data.length && data.length <= 40 && /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(data);
    },
    errorMessage: "Invalid email.",
  },
  password: {
    checkConstraint: (data) => {
      // return 7 < data.length && data.length <= 40;
      return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(data);
    },
    errorMessage: "Password must be at least 8 characters long and include at least one lowercase letter, one uppercase letter, one number, and one special character.",
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
    // add userdata
    const formData = new FormData();
    formData.append('userType', "BUYER");
    for (const field in userData) {
      if (userData[field] !== "" && userData[field] != null) {
        formData.append(field, userData[field]);
      }
    }
    // formData.forEach((value, key) => {
    //   console.log(key, " : ", value);
    // });
    const res = await registerUser(formData);
    const result = await res.json();
    console.log(result);
    if (res.ok) {
      statusMessageStore.setStatusMessage("The user account has been successfully registered.");
      router.push({ name: "login" });
    } else if (res.status === 400) {
      statusMessageStore.setStatusMessage(result.message, false);
    } else {
      throw new Error("Something went wrong");
    }
  } catch (err) {
    console.log(err);
    statusMessageStore.setStatusMessage("Something went wrong.", false);
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

const navigateToLogin = () => {
  router.push({ name: "login" });
};


// ========= Testing Zone ===========
const auth = useAuthStore()
const isTesing = ref(false)

const testFunc = () => {
  loadProfile()
  // auth.logout()
}

const testFunc2 = () => {
  console.log(auth.user)
  console.log(auth.accessToken)
}

// example of using fetch with auth
const loadProfile = async () => {
  try {
    const res = await fetchWithAuth(`/v2/auth/me`, {}, auth);
    if (!res.ok) {
      throw new Error("Failed to load profile");
    }
    const profile = await res.json();
    console.log("Profile:", profile);
  } catch (err) {
    if (err.message === "Session expired, please log in again.") {
      // await auth.logout()
      router.push({ name: "login" })
    }
    console.error("Auth error:", err.message);
  }
}



watch(userData, () => {
  showErrorToForm();
  // Disabled save button
  validateAllField();
  // console.log(userData)
});

</script>

<template>
  <div>
    <div v-if="isLoading" class="text-center text-blue-500 animate-pulse text-3xl font-bold">
      Loading...
    </div>

    <div v-else class="flex">
      <!-- Side Image -->
      <img
        :src="'https://images.stockcake.com/public/c/4/6/c4678895-7ad4-4670-a61d-923fa66e6df9_large/online-shopping-app-stockcake.jpg'"
        alt="side image"
        :class="'shadow-md flex-1 max-lg:hidden'"
      />

      <div
        class="flex-1 flex flex-col bg-white max-lg:rounded-xl max-lg:shadow-lg p-6"
      >
        <div class="flex-1 p-3">
          <h2 class="text-5xl font-bold text-center mt-10">Buyer Sign up</h2>
          <!-- User Data Form -->
          <form @submit="submitForm" class="flex flex-col gap-3">
            <div class="flex flex-col gap-1">
              <label>
                <span class="text-red-500 text-xl">*</span>
                Nickname
              </label>
              <input
                name="nickname"
                type="text"
                class="itbms-nickname input"
                placeholder="Nickname..."
                v-model="userData.nickname"
                @focusin="handleFocusIn"
                @focusout="handleFocusOut"
              />
              <p class="itbms-message text-red-500 pl-2">
                {{ errorFormMessage["nickname"] }}
              </p>
            </div>

            <div class="flex flex-col gap-1">
              <label>
                <span class="text-red-500 text-xl">*</span>
                Full Name
              </label>
              <input
                name="fullName"
                type="text"
                class="itbms-fullName input"
                placeholder="Full Name..."
                v-model="userData.fullName"
                @focusin="handleFocusIn"
                @focusout="handleFocusOut"
              />
              <p class="itbms-message text-red-500 pl-2">
                {{ errorFormMessage["fullName"] }}
              </p>
            </div>

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
                :disabled="isSubmitting || !isFormValid"
                class-name="itbms-save-button px-10 shadow-lg drop-shadow-[0_1px_1px_rgba(0,0,0,1)]"
                type="submit"
              >
                {{ isSubmitting ? "Loading..." : "Sign up" }}
              </Button>
              <button
                type="button"
                @click="navigateToLogin"
                class="text-purple-600 cursor-pointer hover:underline"
              >
                already have an account?
              </button>

              <!-- Testing Button -->
              <div v-if="isTesing" class="flex flex-col gap-4">
                <Button
                :onclick="testFunc"
                variant="primary"
                class-name="itbms-save-button px-10 shadow-lg drop-shadow-[0_1px_1px_rgba(0,0,0,1)]"
                type="button"
              >
                {{ "Test" }}
              </Button>

                <Button
                  :onclick="testFunc2"
                  variant="primary"
                  class-name="itbms-save-button px-10 shadow-lg drop-shadow-[0_1px_1px_rgba(0,0,0,1)]"
                  type="button"
                >
                  {{ "Test2" }}
                </Button>
              </div>

            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>
