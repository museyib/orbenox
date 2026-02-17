<script setup>

import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRoute, useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";

const route = useRoute();
const router = useRouter();
const info = ref('');
const stockBalance = ref([]);

function init() {
  apiRequest('/api/stockBalance/' + route.params.id, 'GET').then(response => {
    if (response.code === 200) {
      stockBalance.value = response.data;
    } else if(response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}
onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title="$t('stockBalance')">
    </PageHeader>

    <section class="card list-card">
      <div v-if="stockBalance.length > 0" class="table-wrap">
        <div>
          <table class="data-table" role="table">
            <thead>
            <tr>
              <th>{{ $t('warehouse.title') }}</th>
              <th>{{ $t('quantity') }}</th>
              <th>{{ $t('reservedQuantity') }}</th>
              <th>{{ $t('freeQuantity') }}</th>
              <th aria-hidden="true" class="actions-col"></th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="stockData in stockBalance">
              <td>{{ stockData.warehouse.code }}</td>
              <td>{{ stockData.quantity }}</td>
              <td>{{ stockData.reservedQuantity }}</td>
              <td>{{ stockData.freeQuantity }}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-else class="empty">
        {{ $t('noRecords') }}
      </div>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>

<style scoped>

</style>
