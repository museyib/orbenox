<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";
import {useRouter} from "vue-router";

const info = ref('');
const transactionTypes = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/transactionTypes', 'GET').then(response => {
    if (response.code === 200) {
      transactionTypes.value = response.data;
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
  router.push('/ui/transactionTypes/create');
}

function edit(transactionTypeId) {
  router.push('/ui/transactionTypes/edit/' + transactionTypeId);
}

function deleteTransactionType(transactionTypeId) {
  apiRequest('/api/transactionTypes/' + transactionTypeId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteTransactionType(transactionTypeId), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("transactionTypes")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card">
      <div v-if="transactionTypes.length > 0" class="table-wrap">
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
          <tr v-for="transactionType in transactionTypes" :key="transactionType.id">
            <td class="mono">{{ transactionType.id }}</td>
            <td>{{ transactionType.code }}</td>
            <td>{{ transactionType.name }}</td>
            <td><input v-model="transactionType.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(transactionType.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click='deleteTransactionType(transactionType.id)'>{{ $t('delete') }}</button>
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