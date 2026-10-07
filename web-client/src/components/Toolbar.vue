<script setup>
import RefreshButton from "@/components/RefreshButton.vue";
import Search from "@/components/Search.vue";
import New from "@/components/New.vue";

defineProps({
  onRefresh: {
    type: Function,
    required: true
  },
  onCreate: {
    type: Function,
    required: false
  },
  searchQuery: {
    type: String,
    required: true
  },
  onSearch: {
    type: Function,
    required: true
  },
  visible: {
    type: Boolean,
    required: false,
    default: true
  }
});
</script>
<template>
  <div class="toolbar">
    <New v-show="visible" :on-create="onCreate"/>
    <Search :on-search="onSearch" :search-query="searchQuery"/>
    <RefreshButton :on-refresh="onRefresh"/>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: .6rem;
  align-items: center;
}
</style>