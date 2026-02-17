<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRouter} from "vue-router";

const info = ref('');
const currencies = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/currencies', 'GET').then(response => {
    if (response.code === 200) {
      currencies.value = response.data;
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
  router.push('/ui/currencies/create');
}

function edit(currencyId) {
  router.push('/ui/currencies/edit/' + currencyId);
}

function deleteCurrency(currencyId) {
  apiRequest('/api/currencies/' + currencyId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteCurrency(currencyId), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("currencies")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
      <div v-if="currencies.length > 0" class="table-wrap">
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
          <tr v-for="currency in currencies" :key="currency.id">
            <td class="mono">{{ currency.id }}</td>
            <td>{{ currency.code }}</td>
            <td>{{ currency.name }}</td>
            <td><input v-model="currency.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(currency.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click='deleteCurrency(currency.id)'>{{ $t('delete') }}</button>
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