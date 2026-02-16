<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";
import {useRouter} from "vue-router";

const info = ref('');
const brands = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/brands', 'GET').then(response => {
    if (response.code === 200) {
      brands.value = response.data;
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
  router.push('/ui/brands/create');
}

function edit(brandId) {
  router.push('/ui/brands/edit/' + brandId);
}

function deleteBrand(brandId) {
  apiRequest('/api/brands/' + brandId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteBrand(brandId), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("brands")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card">
      <div v-if="brands.length > 0" class="table-wrap">
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
          <tr v-for="brand in brands" :key="brand.id">
            <td class="mono">{{ brand.id }}</td>
            <td>{{ brand.code }}</td>
            <td>{{ brand.name }}</td>
            <td>{{ brand.description }}</td>
            <td><input v-model="brand.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(brand.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click='deleteBrand(brand.id)'>{{ $t('delete') }}</button>
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