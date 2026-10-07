<script setup>

defineProps({
  title: {
    type: String,
    required: true
  }
});

</script>

<template>
  <div class="page-header">
    <h1>{{ $t(title) }}</h1>
    <slot/>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: .75rem;
}

.page-header h1 {
  margin: 0;
  font-size: 1.35rem;
}

@media (max-width: 720px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: .6rem;
  }
}

</style>