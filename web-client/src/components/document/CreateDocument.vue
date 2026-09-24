<script setup>
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("document.new")'/>
    <section class="card document-type-options">
      <RouterLink class="btn btn-primary" to="/ui/documents/create/sales-order">
        {{ $t("document.salesOrder.new") }}
      </RouterLink>
      <RouterLink class="btn" to="/ui/documents/create/product-approve">
        {{ $t("document.productApprove.new") }}
      </RouterLink>
    </section>
  </MainLayout>
</template>

<style scoped>
.document-type-options { display: flex; flex-wrap: wrap; gap: 1rem; }
</style>
