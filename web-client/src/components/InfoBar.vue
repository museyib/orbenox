<script setup>

defineProps({
  info: {
    type: String,
    required: true
  }
});

</script>

<template>
  <footer class="pager">
    <span class="info">{{ info }}</span>
  </footer>
</template>

<style scoped>
.pager {
  margin-top: .8rem;
  display: flex;
  justify-content: space-between;
  color: var(--muted);
}
</style>