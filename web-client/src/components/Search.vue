<script setup>
defineProps({
  searchQuery: {
    type: String,
    required: true
  },
  onSearch: {
    type: Function,
    required: true
  }
});
</script>

<template>
  <input
      :value="searchQuery"
      aria-label="Search"
      class="search"
      name="search"
      placeholder="Search…"
      type="search"
      @input="onSearch"
  />
</template>

<style scoped>
.search {
  padding: .4rem .6rem;
  border: 1px solid var(--border);
  border-radius: 8px;
  min-width: 220px;
}
</style>
