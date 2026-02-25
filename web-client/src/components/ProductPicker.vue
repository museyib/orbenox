<script setup>


import {computed, ref} from "vue";

const props = defineProps({
  showPicker: {
    type: Boolean,
    default: false
  },
  products: {
    type: Array,
    required: true
  },
  line: {
    type: Object,
    required: true
  },
  onClose: {
    type: Function,
    required: false
  }
});

const showPicker = ref(() => props.showPicker);
const searchTerm = ref("");

const filteredProducts = computed(() => {
  const term = searchTerm.value.trim().toLowerCase();
  if (!term) return props.products;
  return props.products.filter(p => {
    const code = String(p.code || "").toLowerCase();
    const name = String(p.name || "").toLowerCase();
    return code.includes(term) || name.includes(term);
  });
});

function setProduct(product) {
  if (!props.line || !product) return;
  props.line.product = product;
  props.line.productId = product.id;
  props.line.productCode = product.code;
  props.line.productName = product.name;
  showPicker.value = false;
}
</script>

<template>
  <div v-if="showPicker" class="product-picker-backdrop" >
    <div class="product-picker">
      <div class="product-picker-header">
        <h5>{{ $t("productPickerTitle") }}</h5>
        <button class="btn btn-sm" type="button" @click = "onClose">{{ $t("close") }}</button>
      </div>
      <div class="product-picker-search">
        <input v-model="searchTerm" :placeholder="$t('search')" type="text"/>
      </div>
      <div class="product-picker-list">
        <button v-for="productItem in filteredProducts"
                :key="productItem.id"
                class="product-picker-item"
                type="button"
                @click="setProduct(productItem)">
          <span class="product-code">{{ productItem.code }}</span>
          <span class="product-name">{{ productItem.name }}</span>
        </button>
        <div v-if="filteredProducts.length === 0" class="product-picker-empty">
          {{ $t("noRecords") }}
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>


.product-picker-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, .45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.product-picker {
  width: min(720px, 92vw);
  max-height: 80vh;
  background: var(--surface, #fff);
  border: 1px solid var(--border);
  border-radius: .6rem;
  padding: .75rem;
  display: flex;
  flex-direction: column;
  gap: .5rem;
  box-shadow: 0 18px 50px rgba(0, 0, 0, .2);
}

.product-picker-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: .5rem;
}

.product-picker-header h5 {
  margin: 0;
}

.product-picker-search input {
  width: 100%;
}

.product-picker-list {
  display: grid;
  gap: .35rem;
  overflow: auto;
  padding-right: .2rem;
}

.product-picker-item {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: .75rem;
  padding: .45rem .6rem;
  border: 1px solid var(--border);
  border-radius: .4rem;
  background: transparent;
  text-align: left;
}

.product-picker-item:hover {
  background: var(--glass);
}

.product-code {
  font-weight: 600;
}

.product-name {
  color: var(--muted);
}

.product-picker-empty {
  color: var(--muted);
  padding: .5rem .3rem;
}
</style>
