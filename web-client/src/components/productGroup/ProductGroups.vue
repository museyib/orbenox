<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRouter} from "vue-router";

const info = ref('');
const productGroups = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/productGroups', 'GET').then(response => {
    if (response.code === 200) {
      productGroups.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  });
}

function create() {
  router.push('/ui/productGroups/create');
}

function edit(productGroupId) {
  router.push('/ui/productGroups/edit/' + productGroupId);
}

function deleteProductGroup(productGroupId) {
  apiRequest('/api/productGroups/' + productGroupId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteProductGroup(productGroupId), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("productGroups")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card">
      <div v-if="productGroups.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t('code') }}</th>
            <th>{{ $t('name') }}</th>
            <th>{{ $t('description') }}</th>
            <th>{{ $t('parentGroup') }}</th>
            <th>{{ $t('enabled') }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="productGroup in productGroups" :key="productGroup.id">
            <td class="mono">{{ productGroup.id }}</td>
            <td>{{ productGroup.code }}</td>
            <td>{{ productGroup.name }}</td>
            <td>{{ productGroup.description }}</td>
            <td v-if="productGroup.parent">{{ productGroup.parent.code }}</td>
            <td v-else></td>
            <td><input v-model="productGroup.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(productGroup.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger"
                      @click='deleteProductGroup(productGroup.id)'>{{ $t('delete') }}
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