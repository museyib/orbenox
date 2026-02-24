<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {computed, ref, watch} from "vue";
import {useRouter} from "vue-router";

const props = defineProps({
  line:{
    type: Object,
    required: false
  },
  product: {
    type: Object,
    required: false
  },
  priceListId: {
    type: Number,
    required: false
  },
  products: {
    type: Array,
    required: true
  },
  disabled: {
    type: Boolean,
    default: false
  },
  onRemove: {
    type: Function,
    required: false
  }
});

const router = useRouter();
const info = ref("");
const infoType = ref('');

const line = computed(() => props.line);
const product = computed(() => props.product);

const selectedProduct = computed({
  get: () => props.product,
  set: (value) => {
    if (props.line) {
      props.line.product = value;
    }
  }
});


function getPriceListData (productId, priceListId, unitId) {
  if (!productId || !priceListId || !unitId) {
    return;
  }
  const url = "/api/productPrices/byPriceListId?productId=" + productId
      + "&priceListId=" + priceListId + "&unitId=" + unitId;
  apiRequest(url, "GET").then(response => {
    if (response.code === 200) {
      props.line.unitPrice = response.data.price;
      info.value = response.data;
      infoType.value = "success";
    } else if (response.code === 401) {
      refreshToken(() => getPriceListData(productId, priceListId, unitId), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

watch(
  () => [selectedProduct.value, props.priceListId],
  ([newProduct, newPriceListId]) => {
    if (props.disabled) {
      return;
    }
    if (!newProduct || !newPriceListId) {
      return;
    }
    getPriceListData(newProduct.id, newPriceListId, newProduct.defaultUnit?.id);
  }
);
</script>

<template>
  <tr class="product-line-row">
    <td class="cell cell-product">
      <input v-model="line.product.id" :disabled="props.disabled" />
    </td>
    <td class="cell cell-number">
      <input v-model="line.quantity" :disabled="props.disabled" min="0" step="0.0001" type="number"/>
    </td>
    <td class="cell cell-number">
      <input v-model="line.unitPrice" :disabled="props.disabled" min="0" step="0.0001" type="number"/>
    </td>
    <td class="cell cell-number">
      <input v-model="line.discountRatio" :disabled="props.disabled" min="0" step="0.01" type="number"/>
    </td>
    <td class="cell cell-remove">
      <button v-if="onRemove && !props.disabled" class="btn btn-sm btn-danger" type="button" @click="onRemove">x</button>
    </td>
  </tr>
</template>

<style scoped>
.cell {
  width: 100%;
}

.cell-number {
  text-align: right;
}

.cell-remove {
  justify-content: center;
  min-width: 0;
  padding: .2rem .4rem;
}
</style>
