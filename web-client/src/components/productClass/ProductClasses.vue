<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRouter} from "vue-router";

const info = ref('');
const productClasses = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/productClasses', 'GET').then(response => {
    if (response.code === 200) {
      productClasses.value = response.data;
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
  router.push('/ui/productClasses/create');
}

function edit(productClassId) {
  router.push('/ui/productClasses/edit/' + productClassId);
}

function deleteProductClass(productClassId) {
  apiRequest('/api/productClasses/' + productClassId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteProductClass(productClassId), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  });
}

function onSearch() {

}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("productClasses")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
      <div v-if="productClasses.length > 0" class="table-wrap">
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
          <tr v-for="productClass in productClasses" :key="productClass.id">
            <td class="mono">{{ productClass.id }}</td>
            <td>{{ productClass.code }}</td>
            <td>{{ productClass.name }}</td>
            <td>{{ productClass.description }}</td>
            <td><input v-model="productClass.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(productClass.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click='deleteProductClass(productClass.id)'>
                {{ $t('delete') }}
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