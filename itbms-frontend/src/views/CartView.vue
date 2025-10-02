<script setup>
import { useRouter } from "vue-router";
import { useStatusMessageStore } from "@/stores/statusMessage";
import { useAuthStore } from "@/stores/auth";
import { onMounted, ref } from "vue";
import { createOrderItems, validateCartItems } from "@/api";
import { useCartStore } from "@/stores/cart";
import placeHolder from "@/assets/placeholder.svg";

const router = useRouter();
const statusMessageStore = useStatusMessageStore();
const auth = useAuthStore();
const cartStore = useCartStore();

const orderNote = ref("");
const shippingAddress = ref("");
const validatedCart = ref(false);

const BASE_API = import.meta.env.VITE_BASE_API;
const IMAGE_ENDPOINT = BASE_API + "/v1/sale-items/images/";

const isLoading = ref(false);

const increaseQuantity = (item) => {
  cartStore.addToCart({ ...item });
};

const decreaseQuantity = (item) => {
  cartStore.decreaseQuantity(item.id);
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

    // The order will be grouped by sellerId
    const groupedOrderItems = cartStore.items.reduce((acc, item) => {
      if (!acc[item.sellerId]) {
        acc[item.sellerId] = [];
      }
      acc[item.sellerId].push({ ...item });
      return acc;
    }, {});

    const orders = [];
    for (const sellerId in groupedOrderItems) {
      orders.push({
        sellerId: parseInt(sellerId),
        buyerId: auth.user.id,
        orderItems: groupedOrderItems[sellerId].map((item) => ({
          saleItemId: item.id,
          model: item.model,
          quantity: item.quantity,
          price: item.price,
        })),
        orderDate: new Date().toISOString(),
        orderNote: orderNote.value ? orderNote.value : null,
        shippingAddress: shippingAddress.value ? shippingAddress.value : null,
        // orderStatus: "PENDING",
      });
    }

    // console.log(orders)

    isLoading.value = true;
    // Checkout if cart is validated
    if (validatedCart.value) {
      await handleCheckout(orders);
      return;
    }
    // Validate cart if it not
    else {
      const validateRes = await validateCartItems(orders, auth);
      if (!validateRes.ok) throw new Error("Something went wrong");

      const validateResult = await validateRes.json();
      // console.log(validateResult);

      if (validateResult.valid) {
        await handleCheckout(orders);
        return;
      } else {
        alert("Item in cart is changed are you sure to continue?");

        // Update cart items
        validateResult.updateItems.forEach((updatedItem) => {
          cartStore.updateItem(
            updatedItem.saleItemId,
            updatedItem.newPrice,
            updatedItem.availableQuantity
          );
        });

        statusMessageStore.setStatusMessage(
          "Cart is updated, please review before checkout",
          false
        );
        validatedCart.value = true;
      }
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

    cartStore.clearCart();
    statusMessageStore.setStatusMessage("Order success.");
    // router.push({ name: "order" })
  } catch (err) {
    console.log(err);
    statusMessageStore.setStatusMessage("Something went wrong.", false);
  }
};

onMounted(async () => {});
</script>

<template>
  <!-- Update my cart UI  -->
  <div class="px-16 py-8">
    <div class="bg-white px-16 py-8 rounded-xl shadow">
      <div
        class="bg-white bg-opacity-80 shadow-2xl shadow-pink-200 rounded-3xl p-6 sm:p-8 mb-6 border-4 border-pink-100 backdrop-blur-md"
      >
        <h1
          class="text-3xl sm:text-4xl font-extrabold text-rose-500 text-center tracking-widest drop-shadow-sm"
        >
          🛍️ Shopping Cart
        </h1>
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

      <!-- Cart Items and Summary -->
      <div v-else>
        <div class="max-w-4xl mx-auto">
          <!-- Cart Items -->
          <div
            class="bg-white bg-opacity-80 shadow-2xl shadow-pink-200 rounded-3xl p-6 sm:p-8 border-4 border-pink-100 backdrop-blur-md"
          >
            <ul class="space-y-4">
              <li
                v-for="item in cartStore.items"
                :key="item.id"
                class="bg-gradient-to-r from-pink-50 to-purple-50 border-2 border-pink-200 rounded-2xl p-4 sm:p-6 flex flex-col lg:flex-row items-center gap-4 shadow-lg hover:shadow-xl transition-all duration-300"
              >
                <!-- Product Image -->
                <div class="flex-shrink-0">
                  <img
                    :src="
                      item.saleItemImages[0]
                        ? `${IMAGE_ENDPOINT}${item.saleItemImages[0].fileName}`
                        : placeHolder
                    "
                    alt="product"
                    class="w-32 h-32 sm:w-40 sm:h-40 object-cover rounded-xl border-4 border-white shadow-md"
                    @error="(event) => (event.target.src = placeHolder)"
                  />
                </div>

                <!-- Product Info -->
                <div class="flex-1 text-center sm:text-left">
                  <p class="text-lg sm:text-xl">
                    <span class="font-extrabold text-purple-700">{{
                      item.brandName
                    }}</span>
                  </p>
                  <p class="text-base sm:text-lg text-gray-700 mt-1">
                    {{ item.model }}
                  </p>
                  <p class="font-extrabold text-purple-700">
                    {{ item.availableQuantity }} in stock
                  </p>
                  <p class="text-xl sm:text-2xl font-bold text-rose-500 mt-2">
                    ฿{{ (item.price * item.quantity).toFixed(2) }}
                  </p>
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
                    >{{ cartStore.totalItems }}</span
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
                    >฿{{ cartStore.totalPrice.toFixed(2) }}</span
                  >
                </div>

                <!-- Place Order Button -->
                <button
                  @click="handlePlaceOrder"
                  class="cursor-pointer w-full bg-gradient-to-r from-pink-400 to-rose-400 text-white px-8 py-4 rounded-full text-xl sm:text-2xl font-bold shadow-2xl hover:from-purple-400 hover:to-purple-500 transition-all duration-300 hover:scale-105 drop-shadow-[0_2px_2px_rgba(0,0,0,0.8)] mt-4"
                >
                  🎉 Place Order Now
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
