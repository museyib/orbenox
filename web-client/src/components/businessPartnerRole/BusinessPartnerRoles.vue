<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRouter} from "vue-router";

const info = ref("");
const businessPartnerRoles = ref([]);
const searchQuery = ref("");
const router = useRouter();

function init() {
  apiRequest("/api/businessPartnerRoles", "GET").then(response => {
    if (response.code === 200) {
      businessPartnerRoles.value = response.data;
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
  router.push("/ui/businessPartnerRoles/create");
}

function edit(id) {
  router.push("/ui/businessPartnerRoles/edit/" + id);
}

function deleteBusinessPartnerRole(id) {
  apiRequest("/api/businessPartnerRoles/" + id, "DELETE").then(response => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteBusinessPartnerRole(id), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("businessPartnerRoles")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
      <div v-if="businessPartnerRoles.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t("businessPartner.title") }}</th>
            <th>{{ $t("code") }}</th>
            <th>{{ $t("name") }}</th>
            <th>{{ $t("partnerRole") }}</th>
            <th>{{ $t("enabled") }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="businessPartnerRole in businessPartnerRoles" :key="businessPartnerRole.id">
            <td class="mono">{{ businessPartnerRole.id }}</td>
            <td class="mono">{{ businessPartnerRole.partnerId }}</td>
            <td>{{ businessPartnerRole.partnerCode }}</td>
            <td>{{ businessPartnerRole.partnerName }}</td>
            <td>{{ businessPartnerRole.role }}</td>
            <td><input v-model="businessPartnerRole.enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click="edit(businessPartnerRole.id)">{{ $t("edit") }}</button>
              <button class="btn btn-sm btn-danger" @click="deleteBusinessPartnerRole(businessPartnerRole.id)">
                {{ $t("delete") }}
              </button>
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
