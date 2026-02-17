<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRouter} from "vue-router";

const info = ref('');
const productTypes = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/productTypes', 'GET').then(response => {
    if (response.code === 200) {
      productTypes.value = response.data;
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
  router.push('/ui/productTypes/create');
}

function edit(productTypeId) {
  router.push('/ui/productTypes/edit/' + productTypeId);
}

function deleteProductType(productTypeId) {
  apiRequest('/api/productTypes/' + productTypeId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteProductType(productTypeId), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("productTypes")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
      <div v-if="productTypes.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t('code') }}</th>
            <th>{{ $t('name') }}</th>
            <th>{{ $t('description') }}</th>
            <th>{{ $t('enabled') }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="productType in productTypes" :key="productType.id">
            <td class="mono">{{ productType.id }}</td>
            <td>{{ productType.code }}</td>
            <td>{{ productType.name }}</td>
            <td>{{ productType.description }}</td>
            <td><input v-model="productType.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(productType.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger"
                      @click='deleteProductType(productType.id)'>{{ $t('delete') }}
              </button>
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