<script setup>
import { useRouter } from "vue-router";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { useAuthStore } from "@/stores/auth";
import { onMounted, ref } from "vue";
import { createOrderItems, validateCartItems } from "@/api";
import { useCartStore } from "@/stores/cart";
import placeHolder from "@/assets/placeholder.svg";
import ConfirmModal from "@/components/ConfirmModal.vue";
import { CircleAlert } from "lucide-vue-next";

const router = useRouter();
const statusMessageStore = useStatusMessageStore();
const auth = useAuthStore();
const cartStore = useCartStore();

// console.log(cartStore.itemsGroupedBySeller);

const orderNote = ref("");
const shippingAddress = ref("");

const BASE_API = import.meta.env.VITE_BASE_API;
const IMAGE_ENDPOINT = BASE_API + "/v1/sale-items/images/";

const isLoading = ref(false);

const increaseQuantity = (item) => {
  cartStore.addToCart({ ...item });
};

const decreaseQuantity = (item) => {
  if (item.quantity === 1) {
    handleShowRemoveDialog(item.id);
    return;
  }
  cartStore.decreaseQuantity(item.id);
};

const handleSelectItem = (itemId, event) => {
  cartStore.selectItem(itemId, event.target.checked);
};

const handleSelectItemsBySeller = (sellerId, event) => {
  cartStore.selectItemsBySeller(sellerId, event.target.checked);
};

const handleSelectAll = (event) => {
  cartStore.selectAll(event.target.checked);
};

const handlePlaceOrder = async () => {
  try {
    if (!shippingAddress.value) {
      statusMessageStore.setStatusMessage(
        "Please enter your shipping address",
        false
      );
      return;
    } else if (cartStore.items.length === 0) {
      statusMessageStore.setStatusMessage("No items in cart", false);
      return;
    }

    const orders = [];
    for (const sellerId in cartStore.selectedItemsGroupedBySeller) {
      orders.push({
        sellerId: parseInt(sellerId),
        buyerId: auth.user.id,
        orderItems: cartStore.selectedItemsGroupedBySeller[sellerId].map(
          (item) => ({
            saleItemId: item.id,
            model: item.model,
            quantity: item.quantity,
            price: item.price,
          })
        ),
        orderDate: new Date().toISOString(),
        orderNote: orderNote.value ? orderNote.value : null,
        shippingAddress: shippingAddress.value ? shippingAddress.value : null,
        // orderStatus: "PENDING",
      });
    }

    // console.log(orders)

    isLoading.value = true;

    const validateRes = await validateCartItems(orders, auth);
    if (!validateRes.ok) throw new Error("Something went wrong");

    const validateResult = await validateRes.json();
    // console.log(validateResult);

    // if (true) { // <------- open this to skip validation
    if (validateResult.valid) {
      await handleCheckout(orders);
      return;
    } else {
      const updatedProductMessage = validateResult.updateItems
        .map((item) => item.errorMessages ? item.errorMessages.map((msg) =>  `<li>${msg}</li>`).join("") : null)
        .filter((msg) => msg) // Filter out empty messages
        .join("");
      // console.log(updatedProductMessage)
      const HTMLMessage = 
      `<div class="flex flex-col gap-1 items-start justify-start text-left">
        ${updatedProductMessage}
      </div>`;
      handleShowNotiItemUpdateDialog(HTMLMessage);

      // Update cart items
      validateResult.updateItems.forEach((updatedItem) => {
        cartStore.updateItem(
          updatedItem.saleItemId,
          updatedItem.newPrice,
          updatedItem.availableQuantity
        );
      });
      // console.log(cartStore.items);
    }
  } catch (err) {
    console.log(err);
    statusMessageStore.setStatusMessage("Something went wrong.", false);
  } finally {
    isLoading.value = false;
  }
};

const handleCheckout = async (orders) => {
  try {
    const res = await createOrderItems(orders, auth);
    const result = await res.json();
    // console.log(result)
    if (!res.ok) throw new Error("Something went wrong");

    cartStore.clearSelected();
    statusMessageStore.setStatusMessage("Order success.");
    // router.push({ name: "order" })
  } catch (err) {
    console.log(err);
    statusMessageStore.setStatusMessage("Something went wrong.", false);
  }
};

// remove from cart confirmation dialog
const showConfirmDialog = ref(false);
const removeId = ref(false);

const handleShowRemoveDialog = (itemId) => {
  showConfirmDialog.value = true;
  removeId.value = itemId;
};

const handleCloseRemoveDialog = () => {
  showConfirmDialog.value = false;
  removeId.value = null;
};

const handleConfirmRemoveDialog = () => {
  cartStore.removeFromCart(removeId.value);
  handleCloseRemoveDialog();
};

// item in cart noti dialog
const showNotiItemUpdateDialog = ref(false);
const itemUpdatedMessage = ref("");

const handleShowNotiItemUpdateDialog = (message) => {
  showNotiItemUpdateDialog.value = true;
  itemUpdatedMessage.value = message;
};

const handleCloseNotiItemUpdateDialog = () => {
  showNotiItemUpdateDialog.value = false;
  itemUpdatedMessage.value = "";
};
</script>

<template>
  <div class="px-16 py-8">
    <div class="bg-white px-16 py-8 rounded-xl shadow">
      <div
        class="bg-white bg-opacity-80 shadow-2xl shadow-pink-200 rounded-3xl p-6 sm:p-8 mb-6 border-4 border-pink-100 backdrop-blur-md"
      >
        <div class="flex items-center justify-center gap-4">
          <div
            class="bg-gradient-to-r from-purple-500 to-pink-500 rounded-full p-3"
          >
            <svg
              class="w-8 h-8 text-white"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
              ></path>
            </svg>
          </div>

          <h1
            class="text-4xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent"
          >
            Shopping Cart
          </h1>
        </div>
      </div>
      <!-- Loading State -->
      <div v-if="isLoading" class="text-center py-16">
        <div
          class="inline-block animate-spin rounded-full h-16 w-16 border-4 border-purple-200 border-t-purple-600 mb-4"
        ></div>
        <p class="text-gray-600 text-lg">Ordering...</p>
      </div>

      <!-- Empty State -->
      <div
        v-else-if="!cartStore.items || cartStore.items.length === 0"
        class="text-center text-purple-400 font-bold text-3xl"
      >
        No items in cart
      </div>

      <!-- Cart Items -->
      <div v-else>
        <div class="mx-auto">
          <!-- Cart Items -->
          <div
            class="space-y-4 bg-white bg-opacity-80 shadow-2xl shadow-pink-200 rounded-3xl p-6 sm:p-8 border-4 border-pink-100 backdrop-blur-md"
          >
            <div class="mb-4">
              <label class="inline-flex items-center">
                <input
                  type="checkbox"
                  class="w-5 h-5 cursor-pointer"
                  @change="handleSelectAll"
                  :checked="
                    cartStore.totalSelectedItems === cartStore.totalItems &&
                    cartStore.totalItems > 0
                  "
                />
                <span class="ml-2 text-lg font-bold text-purple-600"
                  >Select All</span
                >
              </label>
            </div>
            <div>
              <hr class="border-purple-200 mb-4" />
            </div>

            <div
              v-for="itemsGroupedBySeller in cartStore.itemsGroupedBySeller"
              class=""
            >
              <div class="flex gap-3 items-center mb-4">
                <input
                  type="checkbox"
                  :id="`select-seller-${itemsGroupedBySeller[0].seller.id}`"
                  class="w-5 h-5 cursor-pointer"
                  @change="
                    handleSelectItemsBySeller(
                      itemsGroupedBySeller[0].seller.id,
                      $event
                    )
                  "
                  :checked="itemsGroupedBySeller.every((item) => item.selected)"
                />
                <label
                  :for="`select-seller-${itemsGroupedBySeller[0].seller.id}`"
                  class="text-xl font-bold text-purple-600"
                >
                  {{ itemsGroupedBySeller[0].seller.shopName }}
                </label>
              </div>
              
              <ul class="space-y-4">
                <li
                  v-for="item of itemsGroupedBySeller"
                  :key="item.id"
                  class="relative bg-gradient-to-r from-pink-50 to-purple-50 border-2 border-pink-200 rounded-2xl p-4 sm:p-6 flex flex-col lg:flex-row items-center gap-4 shadow-lg hover:shadow-xl transition-all duration-300"
                >
                  <!-- Updated Item Alert -->
                  <div v-if="item.isUpdated" class="absolute left-3 top-0.5 flex items-center gap-1">
                    <CircleAlert class="text-red-500" />
                    <p class="text-red-500 max-lg:hidden">This item has been updated</p>
                  </div>

                  <!-- Select Item Checkbox -->
                  <input
                    type="checkbox"
                    :id="`select-item-${item.id}`"
                    class="w-5 h-5 cursor-pointer"
                    @change="handleSelectItem(item.id, $event)"
                    :checked="item.selected"
                  />

                  <!-- Product Image -->
                  <div class="flex-shrink-0">
                    <img
                      :src="
                        item.saleItemImages[0]
                          ? `${IMAGE_ENDPOINT}${item.saleItemImages[0].fileName}`
                          : placeHolder
                      "
                      alt="product"
                      class="w-35 h-35 object-cover rounded-xl border-4 border-white shadow-md"
                      @error="(event) => (event.target.src = placeHolder)"
                    />
                  </div>

                  <!-- Product Info -->
                  <div class="flex-1 text-center sm:text-left">
                    <p class="text-md">
                      <span class="font-extrabold text-purple-700">{{
                        item.brandName
                      }}</span>
                    </p>
                    <p class="text-base sm:text-md text-gray-700 mt-1">
                      {{ item.model }}
                    </p>
                   
                    <div
                      v-if="item.isUpdated && (item.oldAvailableQuantity !== item.availableQuantity)"
                      class="flex flex-wrap gap-2 items-center"
                    >
                      <p class="font-extrabold text-purple-700">
                        New Quantity : {{ String(item.availableQuantity).replace(/\B(?=(\d{3})+(?!\d))/g, ",") }}
                      </p>
                      <p class="bg-white border-2 py-1 px-2 border-purple-300 rounded-full flex items-center font-bold text-purple-700 shadow-inner">
                        Old Quantity : {{ String(item.oldAvailableQuantity).replace(/\B(?=(\d{3})+(?!\d))/g, ",") }}
                      </p>
                    </div>
                    <div v-else class="font-extrabold text-purple-700">
                      {{ String(item.availableQuantity).replace(/\B(?=(\d{3})+(?!\d))/g, ",") }} available
                    </div>

                    <div
                      v-if="item.isUpdated && (item.oldPrice !== item.price)"
                      class="flex gap-2 flex-wrap items-center"
                    >
                      <p class="font-extrabold text-purple-700">
                        New Price : ฿{{ item.price.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",") }}
                      </p>
                      <p class="bg-white border-2 py-1 px-2 border-purple-300 rounded-full flex items-center font-bold text-purple-700 shadow-inner">
                        Old Price : ฿{{ item.oldPrice.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",") }}
                      </p>
                    </div>
                    <div v-else class="text-xl sm:text-lg font-bold text-purple-700 mt-2">
                      Unit Price: ฿{{ item.price.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",") }}
                    </div>

                    <div class="text-xl sm:text-lg font-bold text-rose-500 mt-2">
                      {{ item.isUpdated ? 'New Total:' : 'Total:' }} ฿{{ (item.price * item.quantity).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",") }}
                    </div>
                  </div>

                  <!-- Quantity Controls -->
                  <div class="flex items-center gap-3">
                    <button
                      @click="decreaseQuantity(item)"
                      class="cursor-pointer w-10 h-10 sm:w-12 sm:h-12 bg-gradient-to-r from-rose-400 to-pink-400 text-white rounded-full flex items-center justify-center text-2xl font-bold shadow-lg hover:from-rose-500 hover:to-pink-500 transition-all duration-300 hover:scale-110"
                    >
                      -
                    </button>
                    <div
                      class="w-12 sm:w-16 h-10 sm:h-12 bg-white border-2 border-purple-300 rounded-full flex items-center justify-center text-lg sm:text-xl font-bold text-purple-700 shadow-inner"
                    >
                      {{ item.quantity }}
                    </div>
                    <button
                      @click="increaseQuantity(item)"
                      class="cursor-pointer w-10 h-10 sm:w-12 sm:h-12 bg-gradient-to-r from-purple-400 to-rose-400 text-white rounded-full flex items-center justify-center text-2xl font-bold shadow-lg hover:from-purple-500 hover:to-rose-500 transition-all duration-300 hover:scale-110"
                    >
                      +
                    </button>
                  </div>
                </li>
              </ul>
            </div>

            <!-- Summary Section -->
            <div
              class="mt-8 bg-gradient-to-r from-purple-50 to-pink-50 border-4 border-pink-200 rounded-2xl p-6 sm:p-8 shadow-xl"
            >
              <div class="space-y-4">
                <!-- Shipping Address -->
                <div class="flex items-start gap-4 mb-3">
                  <div
                    class="bg-gradient-to-r from-pink-600 to-purple-600 rounded-full p-2.5 flex-shrink-0 shadow-md"
                  >
                    <svg
                      class="w-5 h-5 text-white"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                      ></path>
                    </svg>
                  </div>
                  <div class="flex-1">
                    <label class="text-sm text-gray-600 font-bold mb-2 block"
                      >Shipping Address
                      <span class="text-red-500">*</span></label
                    >
                    <textarea
                      v-model="shippingAddress"
                      placeholder="Enter your shipping address..."
                      rows="3"
                      class="bg-white w-full px-4 py-3 border-2 border-purple-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-400 focus:border-transparent resize-none font-medium text-gray-800"
                    ></textarea>
                  </div>
                </div>

                <!-- Order Note -->
                <div class="flex items-start gap-4 mb-3">
                  <div
                    class="bg-gradient-to-r from-pink-600 to-purple-600 rounded-full p-2.5 flex-shrink-0 shadow-md"
                  >
                    <svg
                      class="w-5 h-5 text-white"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"
                      ></path>
                    </svg>
                  </div>
                  <div class="flex-1">
                    <label class="text-sm text-gray-600 font-bold mb-2 block"
                      >Order Note</label
                    >
                    <textarea
                      v-model="orderNote"
                      placeholder="Additional instructions or requests..."
                      rows="3"
                      class="bg-white w-full px-4 py-3 border-2 border-purple-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-400 focus:border-transparent resize-none font-medium text-gray-800"
                    ></textarea>
                  </div>
                </div>

                <!-- Total Items and Price -->
                <div
                  class="flex justify-between items-center pb-4 border-b-2 border-pink-200"
                >
                  <span class="text-lg sm:text-xl font-semibold text-purple-700"
                    >Total Items:</span
                  >
                  <span
                    class="text-2xl sm:text-3xl font-extrabold text-rose-500"
                    >{{ cartStore.totalSelectedItems }}</span
                  >
                </div>

                <div
                  class="flex justify-between items-center pb-6 border-b-2 border-pink-200"
                >
                  <span class="text-xl sm:text-2xl font-bold text-purple-700"
                    >Total Price:</span
                  >
                  <span
                    class="text-3xl sm:text-4xl font-extrabold text-rose-500"
                    >฿{{
                      cartStore.totalSelectedItemPrice
                        .toFixed(2)
                        .replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                    }}</span
                  >
                </div>

                <!-- Place Order Button -->
                <button
                  @click="handlePlaceOrder"
                  :disabled="
                    isLoading ||
                    shippingAddress === '' ||
                    cartStore.totalSelectedItemPrice === 0
                  "
                  :class="[
                    isLoading ||
                    shippingAddress === '' ||
                    cartStore.totalSelectedItemPrice === 0
                      ? 'bg-gray-300 cursor-not-allowed'
                      : 'bg-gradient-to-r from-rose-500 to-purple-500 cursor-pointer',
                    'w-full text-white text-xl sm:text-2xl font-extrabold py-3 rounded-full shadow-lg transition-all duration-300 hover:scale-105 flex items-center justify-center',
                  ]"
                >
                  Place Order Now
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <ConfirmModal
    v-if="showConfirmDialog"
    :title="'Remove this item from cart?'"
    :message="``"
    :button-label="'remove'"
    @confirm="handleConfirmRemoveDialog"
    @cancel="handleCloseRemoveDialog"
  />

  <ConfirmModal
    v-if="showNotiItemUpdateDialog"
    :title="'Some items in your cart have been updated. Please review before checkout.'"
    :message="itemUpdatedMessage"
    :is-disabled="true"
    @cancel="handleCloseNotiItemUpdateDialog"
  />
</template>
