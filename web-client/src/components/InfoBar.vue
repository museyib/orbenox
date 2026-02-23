<script setup>
import {computed, onBeforeUnmount, ref, watch} from "vue";

const props = defineProps({
  info: {
    type: String,
    required: true
  },
  type: {
    type: String,
  }
});

const visible = ref(false);
const fading = ref(false);
let closeTimer = null;
let fadeTimer = null;

const tone = computed(() => {
  if (props.type === "error" || props.type === "success") {
    return props.type;
  }
  return "success";
});

const clearTimers = () => {
  if (closeTimer) {
    clearTimeout(closeTimer);
    closeTimer = null;
  }
  if (fadeTimer) {
    clearTimeout(fadeTimer);
    fadeTimer = null;
  }
};

const closeToast = () => {
  clearTimers();
  fading.value = true;
  closeTimer = setTimeout(() => {
    visible.value = false;
    fading.value = false;
  }, 200);
};

watch(() => props.info, (value) => {
  clearTimers();
  if (value) {
    visible.value = true;
    fading.value = false;
    fadeTimer = setTimeout(() => closeToast(), 4000);
  } else {
    visible.value = false;
  }
});

onBeforeUnmount(() => clearTimers());
</script>

<template>
  <footer v-if="visible" class="info-toast" :class="[tone, { fading }]">
    <span class="info">{{ info }}</span>
    <button class="info-close" type="button" aria-label="Close" @click="closeToast">x</button>
  </footer>
</template>

<style scoped>
.info-toast {
  position: fixed;
  right: 1.25rem;
  bottom: 1.25rem;
  max-width: min(420px, calc(100vw - 2.5rem));
  padding: .65rem 2.1rem .65rem .85rem;
  border-radius: 10px;
  border: 1px solid transparent;
  box-shadow: 0 10px 24px rgba(12, 18, 38, 0.14);
  z-index: 1000;
  opacity: 1;
  transform: translateY(0);
  transition: opacity .2s ease, transform .2s ease;
}

.info-toast.success {
  background: #ecfdf5;
  color: #065f46;
  border-color: #a7f3d0;
}

.info-toast.error {
  background: #fef2f2;
  color: #991b1b;
  border-color: #fecaca;
}

.info-toast.fading {
  opacity: 0;
  transform: translateY(6px);
}

.info-close {
  position: absolute;
  top: .35rem;
  right: .45rem;
  border: none;
  background: transparent;
  color: inherit;
  font-size: 1rem;
  line-height: 1;
  cursor: pointer;
  padding: .2rem;
}
</style>
