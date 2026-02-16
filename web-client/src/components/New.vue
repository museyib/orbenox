<script setup>
defineProps({
  onCreate: {
    type: Function,
    required: false
  },
  visible: {
    type: Boolean,
    required: false,
    default: true
  }
});
</script>

<template>
  <button v-show="visible" class="btn btn-primary" @click="onCreate">
    ＋ {{ $t('new') }}
  </button>
</template>

<style scoped>

</style>