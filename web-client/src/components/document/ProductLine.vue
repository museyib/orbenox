<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {computed, ref, watch} from "vue";
import {useRouter} from "vue-router";
import ProductPicker from "@/components/ProductPicker.vue";

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
  },
  onClose :{
    type: Function,
    required: false
  }
});

const router = useRouter();
const info = ref("");
const infoType = ref('');
const showPicker = ref(false);
const searchTerm = ref("");

const line = computed(() => props.line);
const fallbackProduct = computed(() => props.product);

const activeProduct = computed(() => {
  const fromLine = line.value?.product;
  if (fromLine?.id) return fromLine;
  const fromFallback = fallbackProduct.value;
  if (fromFallback?.id) return fromFallback;
  return null;
});

const onClose = () => {
  showPicker.value = false;
  props.onClose();
}

function ensureLineProduct() {
  if (!props.line || props.line.product || !fallbackProduct.value?.id) return;
  props.line.product = fallbackProduct.value;
}

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
  () => [activeProduct.value, props.priceListId],
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

watch(
  () => [fallbackProduct.value, line.value],
  () => ensureLineProduct(),
  {immediate: true}
);

watch(
  () => showPicker.value,
  (isOpen) => {
    if (isOpen) {
      searchTerm.value = "";
    }
  }
);
</script>

<template>
  <tr class="product-line-row">
    <td class="cell cell-product">
      <span>{{ activeProduct?.code || "-" }}</span>
      <button v-if="!props.disabled" class="btn btn-sm" type="button" @click="showPicker = true">
        {{ activeProduct ? $t("changeProduct") : $t("selectProduct") }}
      </button>
    </td>
    <td class="cell cell-product">
      {{ activeProduct?.name || "-" }}
    </td>
    <td class="cell cell-number">
      <input v-model="line.quantity" :disabled="props.disabled" min="0" step="0.0001" type="number" name="quantity"/>
    </td>
    <td class="cell cell-number">
      <input v-model="line.unitPrice" :disabled="props.disabled" min="0" step="0.0001" type="number" name="unitPrice"/>
    </td>
    <td class="cell cell-number">
      <input v-model="line.discountRatio" :disabled="props.disabled" min="0" step="0.01" type="number" name="discountRatio"/>
    </td>
    <td class="cell cell-remove">
      <button v-if="onRemove && !props.disabled" class="btn btn-sm btn-danger" type="button" @click="onRemove">x</button>
    </td>
  </tr>

  <teleport to="body">
    <ProductPicker :line="line"
                   :products="props.products"
                   :showPicker="showPicker"
                   :onClose="onClose"  />
  </teleport>
</template>

<style scoped>
.cell {
  width: 40%;
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
