<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRouter} from "vue-router";

const info = ref('');
const productCategories = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/productCategories', 'GET').then(response => {
    if (response.code === 200) {
      productCategories.value = response.data;
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
  router.push('/ui/productCategories/create');
}

function edit(productCategoryId) {
  router.push('/ui/productCategories/edit/' + productCategoryId);
}

function deleteProductCategory(productCategoryId) {
  apiRequest('/api/productCategories/' + productCategoryId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteProductCategory(productCategoryId), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("productCategories")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
      <div v-if="productCategories.length > 0" class="table-wrap">
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
          <tr v-for="productCategory in productCategories" :key="productCategory.id">
            <td class="mono">{{ productCategory.id }}</td>
            <td>{{ productCategory.code }}</td>
            <td>{{ productCategory.name }}</td>
            <td>{{ productCategory.description }}</td>
            <td><input v-model="productCategory.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(productCategory.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger"
                      @click='deleteProductCategory(productCategory.id)'>{{ $t('delete') }}
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