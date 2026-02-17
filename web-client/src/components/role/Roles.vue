<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {onMounted, ref} from "vue";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import Toolbar from "@/components/Toolbar.vue";
import {useRouter} from "vue-router";

const info = ref('');
const roles = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/roles', 'GET').then(response => {
    if (response.code === 200) {
      roles.value = response.data;
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
  router.push('/ui/roles/create');
}

function edit(roleId) {
  router.push('/ui/roles/edit/' + roleId);
}

function deleteRole(roleId) {
  apiRequest('/api/roles/' + roleId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteRole(roleId), () => router.push('/ui/login'));
    } else {
      document.getElementById('info').innerHTML = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function permissions(roleId) {
  router.push('/ui/roles/' + roleId + '/permission');
}

function onSearch() {

}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("roles")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
      <div v-if="roles.length > 0" class="table-wrap">
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
          <tr v-for="role in roles" :key="role.id">
            <td class="mono">{{ role.id }}</td>
            <td>{{ role.code }}</td>
            <td>{{ role.name }}</td>
            <td><input v-model="role.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(role.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click='deleteRole(role.id)'>{{ $t('delete') }}</button>
              <button class="btn btn-sm" @click='permissions(role.id)'>{{ $t('permissions') }}</button>
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