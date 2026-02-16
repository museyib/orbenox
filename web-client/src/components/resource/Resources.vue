<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {onMounted, ref} from "vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import {useRouter} from "vue-router";

const info = ref('');
const resources = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/resources', 'GET').then(response => {
    if (response.code === 200) {
      resources.value = response.data;
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
  router.push('/ui/resources/create');
}

function edit(resourceId) {
  router.push('/ui/resources/edit/' + resourceId);
}

function deleteResource(resourceId) {
  apiRequest('/api/resources/' + resourceId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteResource(resourceId), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("resources")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card">
      <div v-if="resources.length > 0" class="table-wrap">
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
          <tr v-for="resource in resources" :key="resource.id">
            <td class="mono">{{ resource.id }}</td>
            <td>{{ resource.code }}</td>
            <td>{{ resource.name }}</td>
            <td><input v-model="resource.enabled" name="enabled" type="checkbox"/>
            </td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(resource.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click='deleteResource(resource.id)'>{{ $t('delete') }}</button>
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