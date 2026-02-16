<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRouter} from "vue-router";

const info = ref("");
const accounts = ref([]);
const searchQuery = ref("");
const router = useRouter();

function init() {
  apiRequest("/api/accounts", "GET").then(response => {
    if (response.code === 200) {
      accounts.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function create() {
  router.push("/ui/accounts/create");
}

function edit(accountId) {
  router.push("/ui/accounts/edit/" + accountId);
}

function deleteAccount(accountId) {
  apiRequest("/api/accounts/" + accountId, "DELETE").then(response => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteAccount(accountId), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("accounts")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card">
      <div v-if="accounts.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t("code") }}</th>
            <th>{{ $t("name") }}</th>
            <th>{{ $t("accountType") }}</th>
            <th>{{ $t("enabled") }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="account in accounts" :key="account.id">
            <td class="mono">{{ account.id }}</td>
            <td>{{ account.code }}</td>
            <td>{{ account.name }}</td>
            <td>{{ account.accountType }}</td>
            <td><input v-model="account.enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click="edit(account.id)">{{ $t("edit") }}</button>
              <button class="btn btn-sm btn-danger" @click="deleteAccount(account.id)">{{ $t("delete") }}</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div v-else class="empty">
        {{ $t("noRecords") }}
      </div>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>
