<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRouter} from "vue-router";

const info = ref('');
const producers = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/producers', 'GET').then(response => {
    if (response.code === 200) {
      producers.value = response.data;
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
  router.push('/ui/producers/create');
}

function edit(producerId) {
  router.push('/ui/producers/edit/' + producerId);
}

function deleteProducer(producerId) {
  apiRequest('/api/producers/' + producerId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteProducer(producerId), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("producers")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card">
      <div v-if="producers.length > 0" class="table-wrap">
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
          <tr v-for="producer in producers" :key="producer.id">
            <td class="mono">{{ producer.id }}</td>
            <td>{{ producer.code }}</td>
            <td>{{ producer.name }}</td>
            <td>{{ producer.description }}</td>
            <td><input v-model="producer.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(producer.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click='deleteProducer(producer.id)'>{{ $t('delete') }}</button>
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