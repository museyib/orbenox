<script setup>
defineProps({
  onRefresh: {
    type: Function,
    required: true
  }
});
</script>

<template>
  <div class="right-controls">
    <button class="btn btn-ghost" @click="onRefresh">
      ⟳
      <span class="sr-only">Refresh</span>
    </button>
  </div>
</template>

<style scoped>
.right-controls {
  margin-left: auto;
  display: flex;
  gap: .5rem;
  align-items: center;
}

.btn-ghost {
  border-color: transparent;
  color: var(--muted);
  background: transparent;
}
</style>