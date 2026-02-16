<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";

const router = useRouter();
const info = ref('');
const users = ref([]);
const searchQuery = ref('');

function init() {
  apiRequest('/api/users', 'GET').then((response) => {
    if (response.code === 200) {
      users.value = response.data;
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
  router.push('/ui/users/create');
}

function edit(userId) {
  router.push('/ui/users/edit/' + userId);
}

function deleteUser(userId) {
  apiRequest('/api/users/' + userId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteUser(userId), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function permissions(userId) {
  router.push('/ui/users/' + userId + '/permission');
}

function onSearch() {

}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("users")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card">
      <div v-if="users.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t('user.username') }}</th>
            <th>{{ $t('user.displayName') }}</th>
            <th>{{ $t('user.userType') }}</th>
            <th>{{ $t('enabled') }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="user in users" :key="user.id">
            <td class="mono">{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.displayName }}</td>
            <td>{{ user.userType.code }}</td>
            <td><input v-model="user.enabled" name="enabled" type="checkbox"/>
            </td>
            <td class="actions-col">
              <button class="btn btn-sm" @click="edit(user.id)">{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click="deleteUser(user.id)">{{ $t('delete') }}</button>
              <button class="btn btn-sm" @click="permissions(user.id)">{{ $t('permissions') }}</button>
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