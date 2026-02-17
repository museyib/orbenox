<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";
import {useRouter} from "vue-router";

const info = ref('');
const warehouses = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/warehouses', 'GET').then(response => {
    if (response.code === 200) {
      warehouses.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function create() {
  router.push('/ui/warehouses/create');
}

function edit(warehouseId) {
  router.push('/ui/warehouses/edit/' + warehouseId);
}

function deleteWarehouse(warehouseId) {
  apiRequest('/api/warehouses/' + warehouseId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteWarehouse(warehouseId), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function onSearch() {

}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("warehouses")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
      <div v-if="warehouses.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t('code') }}</th>
            <th>{{ $t('name') }}</th>
            <th>{{ $t('enabled') }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="warehouse in warehouses" :key="warehouse.id">
            <td class="mono">{{ warehouse.id }}</td>
            <td>{{ warehouse.code }}</td>
            <td>{{ warehouse.name }}</td>
            <td><input v-model="warehouse.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(warehouse.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click='deleteWarehouse(warehouse.id)'>{{ $t('delete') }}</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div v-else class="empty">
        {{ $t('noRecords') }}
      </div>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>