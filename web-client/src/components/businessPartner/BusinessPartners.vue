<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRouter} from "vue-router";

const info = ref("");
const businessPartners = ref([]);
const searchQuery = ref("");
const router = useRouter();

function init() {
  apiRequest("/api/businessPartners", "GET").then(response => {
    if (response.code === 200) {
      businessPartners.value = response.data;
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
  router.push("/ui/businessPartners/create");
}

function edit(partnerId) {
  router.push("/ui/businessPartners/edit/" + partnerId);
}

function deleteBusinessPartner(partnerId) {
  apiRequest("/api/businessPartners/" + partnerId, "DELETE").then(response => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteBusinessPartner(partnerId), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("businessPartners")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
      <div v-if="businessPartners.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t("code") }}</th>
            <th>{{ $t("name") }}</th>
            <th>{{ $t("taxId") }}</th>
            <th>{{ $t("partnerType") }}</th>
            <th>{{ $t("enabled") }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="businessPartner in businessPartners" :key="businessPartner.id">
            <td class="mono">{{ businessPartner.id }}</td>
            <td>{{ businessPartner.code }}</td>
            <td>{{ businessPartner.name }}</td>
            <td>{{ businessPartner.taxId }}</td>
            <td>{{ businessPartner.type }}</td>
            <td><input v-model="businessPartner.enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click="edit(businessPartner.id)">{{ $t("edit") }}</button>
              <button class="btn btn-sm btn-danger" @click="deleteBusinessPartner(businessPartner.id)">
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
