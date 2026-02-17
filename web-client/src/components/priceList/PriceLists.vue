<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import PageHeader from "@/components/PageHeader.vue";
import MainLayout from "@/components/MainLayout.vue";
import {useRouter} from "vue-router";

const router = useRouter();
const info = ref('');
const priceLists = ref([]);
const searchQuery = ref('');

function init() {
  apiRequest('/api/priceLists', 'GET').then(response => {
    if (response.code === 200) {
      priceLists.value = response.data;
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
  router.push('/ui/priceLists/create');
}

function edit(priceId) {
  router.push('/ui/priceLists/edit/' + priceId);
}

function deletePrice(priceId) {
  apiRequest('/api/priceLists/' + priceId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deletePrice(priceId), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("priceLists")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
      <div v-if="priceLists.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t('code') }}</th>
            <th>{{ $t('name') }}</th>
            <th>{{ $t('currency') }}</th>
            <th>{{ $t('priceList.parent') }}</th>
            <th>{{ $t('factorToParent') }}</th>
            <th>{{ $t('roundLength') }}</th>
            <th>{{ $t('enabled') }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="priceList in priceLists" :key="priceList.id">
            <td class="mono">{{ priceList.id }}</td>
            <td>{{ priceList.code }}</td>
            <td>{{ priceList.name }}</td>
            <td>{{ priceList.currency.code }}</td>
            <td v-if="priceList.parent">{{ priceList.parent.code }}</td>
            <td v-else></td>
            <td>{{ priceList.factorToParent }}</td>
            <td>{{ priceList.roundLength }}</td>
            <td><input v-model="priceList.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(priceList.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click='deletePrice(priceList.id)'>{{ $t('delete') }}</button>
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