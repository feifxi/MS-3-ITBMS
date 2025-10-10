<script setup>
import { ref, reactive, watch } from "vue";
import { useRouter } from "vue-router";
import { registerUser } from "../api";
import Button from "@/components/Button.vue";
import { useStatusMessageStore } from "@/stores/statusMessage";
import placeHolder from "@/assets/placeholder.svg";

const router = useRouter();
const statusMessageStore = useStatusMessageStore();

const BANK_NAMES = ['SCB', 'KBANK', 'KTB', 'BAY', 'GSB', 'TTB']

const userData = reactive({
  nickname: "",
  fullName: "",
  email: "",
  password: "",
  shopName: "",
  phone: "",
  bankName: "",
  bankAccountNumber: "",
  idCardNumber: "",
  idCardImageFront: null,
  idCardImageBack: null,
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
  shopName: "",
  phone: "",
  bankName: "",
  bankAccountNumber: "",
  idCardNumber: "",
  idCardImageFront: "",
  idCardImageBack: "",
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
      return 0 < data.length && data.length <= 50 && /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(data);
    },
    errorMessage: "Invalid email.",
  },
  password: {
    checkConstraint: (data) => {
      if (typeof data !== "string") return false;
      const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,14}$/;
      return regex.test(data);
    },
    errorMessage: "Password must be 8-14 characters long and include at least one lowercase letter, one uppercase letter, one number, and one special character.",
  },
  shopName: {
    checkConstraint: (data) => {
      return 0 < data.length && data.length <= 40;
    },
    errorMessage: "Shop Name must be 1-40 characters long.",
  },
  phone: {
    checkConstraint: (data) => {
      // Allows only digits, optional + at start, 8–15 digits total
      return /^\+?[0-9]{8,15}$/.test(data);
    },
    errorMessage: "Phone number must contain only digits (8-15 digits, optional + at start).",
  },
  bankName: {
    checkConstraint: (data) => {
      return data != '';
    },
    errorMessage: "Bank must be selected.",
  },
  bankAccountNumber: {
    checkConstraint: (data) => {
      // Must be 6–16 digits, only numbers allowed
      return /^[0-9]{6,16}$/.test(data)
    },
    errorMessage: "Bank account number must contain only digits (6-16 characters).",
  },
  idCardNumber: {
    checkConstraint: (data) => {
      // Must be exactly 13 digits, all numeric
      return /^[0-9]{13}$/.test(data);
    },
    errorMessage: "ID Card Number must contain exactly 13 digits.",
  } 
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

const handleChangeBank = (e) => {
  currentFocusField.value = e.target.name
}

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
  if (userData.idCardImageFront == ''  || userData.idCardImageFront == null ||
      userData.idCardImageBack == ''  || userData.idCardImageBack == null) 
  {
    isValid = false 
  }
  isFormValid.value = isValid;
};

const submitForm = async (e) => {
  e.preventDefault();
  isSubmitting.value = true;
  try {
    // add userdata
    const formData = new FormData();
    formData.append('userType', "SELLER");
    for (const field in userData) {
      if (userData[field] !== "" && userData[field] != null) {
        formData.append(field, userData[field]);
      }
    }
    // add natnional id card image if exists
    if (userData.idCardImageFront !== ''  && userData.idCardImageFront != null &&
        userData.idCardImageBack !== ''  && userData.idCardImageBack != null) 
    { 
      formData.append("idCardImageFront", userData.idCardImageFront.file);
      formData.append("idCardImageBack", userData.idCardImageBack.file);
    }
    // formData.forEach((value, key) => {
    //   console.log(key, " : ", value);
    // });
    const res = await registerUser(formData);
    const result = await res.json();
    // console.log(result);
    if (res.ok) {
      statusMessageStore.setStatusMessage("The user account has been successfully registered.");
      router.push({ name: "login" });
    } 
    else if (res.status === 409) {
      statusMessageStore.setStatusMessage("User with this email already exists.", false);
    } 
    else if (res.status === 400) {
      statusMessageStore.setStatusMessage(result.message, false);
    } 
    else {
      throw new Error("Something went wrong");
    }
  } catch (err) {
    console.log(err);
    statusMessageStore.setStatusMessage("Something went wrong.", false);
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

const handleSelectidCardImage = (e, type = "FRONT") => {
  const file = e.target.files[0];
  const MAX_MB_UNIT = 2 * 1024 * 1024;
  if (file > MAX_MB_UNIT) {
    statusMessageStore.setStatusMessage("Files exceed 2MB.", false);
    return;
  }
  let uploadedImage = {
    file: file,
    preview: URL.createObjectURL(file),
  }

  if (type === "FRONT") {
    userData.idCardImageFront = uploadedImage
  } else {
    userData.idCardImageBack = uploadedImage
  }
  e.target.value = "";
};

const handleRemoveidCardNumberImage = (type = "FRONT") => {
  if (type === "FRONT") {
    userData.idCardImageFront = ''
  } else {
    userData.idCardImageBack = ''
  }
}

const navigateToLogin = () => {
  router.push({ name: "login" });
}

watch(userData, () => {
  showErrorToForm();
  // Disabled save button
  validateAllField();
  // console.log(userData)
});
</script>

<template>
  <div class="">
    <div
      v-if="isLoading"
      class="text-center text-blue-500 animate-pulse text-3xl font-bold"
    >
      Loading...
    </div>

    <div class="flex">
      <div class="flex-1 flex flex-col bg-white max-lg:rounded-xl max-lg:shadow-lg p-6">
        <h2 class="text-5xl font-bold text-center mt-10">Seller Sign up</h2>
        <div class="flex-1 p-3">
          <form @submit="submitForm" class="flex flex-col gap-3">
            <!-- User Data Form -->
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

            <div class="flex flex-col gap-1">
              <label>
                <span class="text-red-500 text-xl">*</span>
                Shop Name
              </label>
              <input
                name="shopName"
                type="text"
                class="itbms-shopName input"
                placeholder="Shop Name..."
                v-model="userData.shopName"
                @focusin="handleFocusIn"
                @focusout="handleFocusOut"
              />
              <p class="itbms-message text-red-500 pl-2">
                {{ errorFormMessage["shopName"] }}
              </p>
            </div>

            <div class="flex flex-col gap-1">
              <label>
                <span class="text-red-500 text-xl">*</span>
                Phone
              </label>
              <input
                name="phone"
                type="text"
                class="itbms-phone input"
                placeholder="Phone..."
                v-model="userData.phone"
                @focusin="handleFocusIn"
                @focusout="handleFocusOut"
              />
              <p class="itbms-message text-red-500 pl-2">
                {{ errorFormMessage["phone"] }}
              </p>
            </div>

            <div class="flex flex-col gap-1">
              <label>
                <span class="text-red-500 text-xl">*</span>
                Bank
              </label>
              <select
                name="bankName"
                class="itbms-bankName input"
                v-model="userData.bankName"
                @change="handleChangeBank"
              >
                <option :value="''">
                  {{ "Select bank" }}
                </option>
                <option
                  v-for="bankName of BANK_NAMES"
                  :value="bankName"
                >
                  {{ bankName }}
                </option>
              </select>
              <p class="itbms-message text-red-500 pl-2">
                {{ errorFormMessage["bankName"] }}
              </p>
            </div>

            <div class="flex flex-col gap-1">
              <label>
                <span class="text-red-500 text-xl">*</span>
                Bank Account Number
              </label>
              <input
                name="bankAccountNumber"
                type="text"
                class="itbms-bankAccountNumber input"
                placeholder="Bank Account Number..."
                v-model="userData.bankAccountNumber"
                @focusin="handleFocusIn"
                @focusout="handleFocusOut"
              />
              <p class="itbms-message text-red-500 pl-2">
                {{ errorFormMessage["bankAccountNumber"] }}
              </p>
            </div>

            <div class="flex flex-col gap-1">
              <label>
                <span class="text-red-500 text-xl">*</span>
                National Id
              </label>
              <input
                name="idCardNumber"
                type="text"
                class="itbms-idCardNumber input"
                placeholder="National ID..."
                v-model="userData.idCardNumber"
                @focusin="handleFocusIn"
                @focusout="handleFocusOut"
              />
              <p class="itbms-message text-red-500 pl-2">
                {{ errorFormMessage["idCardNumber"] }}
              </p>
            </div>

            <!-- National ID Card Image Upload -->
            <div class="flex flex-col gap-1">
              <label>National ID Card Images</label>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-1">
              <div class="flex flex-col gap-2 relative">
                <span 
                  v-if="userData.idCardImageFront" 
                  class="z-50 -top-3 -right-3 absolute flex justify-center items-center cursor-pointer size-8 bg-white shadow-xl rounded-full hover:size-9 transition-all" 
                  @click="handleRemoveidCardNumberImage('FRONT')"
                >
                  X
                </span>
                <img
                  :src="userData.idCardImageFront?.preview || placeHolder"
                  alt="sale item"
                  :class="'shadow-md'"
                />
                <label
                  htmlFor="image-upload-front"
                  className="itbms-upload-button primary-btn max-w-[200px]"
                >
                  upload front image
                </label>
                <input
                  id="image-upload-front"
                  type="file"
                  accept="image/*"
                  @change="(e) => handleSelectidCardImage(e, 'FRONT')"
                  className="hidden"
                />
              </div>

              <div class="flex flex-col gap-2 relative">
                <span 
                  v-if="userData.idCardImageBack" 
                  class="z-50 -top-3 -right-3 absolute flex justify-center items-center cursor-pointer size-8 bg-white shadow-xl rounded-full hover:size-9 transition-all" 
                  @click="handleRemoveidCardNumberImage('BACK')"
                >
                  X
                </span>
                <img
                  :src="userData.idCardImageBack?.preview || placeHolder"
                  alt="sale item"
                  :class="'shadow-md'"
                />
                <label
                  htmlFor="image-upload-back"
                  className="itbms-upload-button primary-btn max-w-[200px]"
                >
                  upload back image
                </label>
                <input
                  id="image-upload-back"
                  type="file"
                  accept="image/*"
                  @change="(e) => handleSelectidCardImage(e, 'BACK')"
                  className="hidden"
                />
              </div>
             </div>
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
            </div>
          </form>
        </div>
      </div>
      <!-- Side Image -->
      <img
        :src="'https://images.stockcake.com/public/c/4/6/c4678895-7ad4-4670-a61d-923fa66e6df9_large/online-shopping-app-stockcake.jpg'"
        alt="side image"
        :class="'shadow-md flex-1 max-lg:hidden'"
      />
    </div>
  </div>
</template>
