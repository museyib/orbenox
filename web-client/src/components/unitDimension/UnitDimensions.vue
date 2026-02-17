<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRouter} from "vue-router";

const info = ref("");
const unitDimensions = ref([]);
const searchQuery = ref("");
const router = useRouter();

function init() {
  apiRequest("/api/unitDimensions", "GET").then(response => {
    if (response.code === 200) {
      unitDimensions.value = response.data;
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
  router.push("/ui/unitDimensions/create");
}

function edit(unitDimensionId) {
  router.push("/ui/unitDimensions/edit/" + unitDimensionId);
}

function deleteUnitDimension(unitDimensionId) {
  apiRequest("/api/unitDimensions/" + unitDimensionId, "DELETE").then(response => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteUnitDimension(unitDimensionId), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("unitDimensions")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
      <div v-if="unitDimensions.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t("code") }}</th>
            <th>{{ $t("name") }}</th>
            <th>{{ $t("enabled") }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="unitDimension in unitDimensions" :key="unitDimension.id">
            <td class="mono">{{ unitDimension.id }}</td>
            <td>{{ unitDimension.code }}</td>
            <td>{{ unitDimension.name }}</td>
            <td><input v-model="unitDimension.enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click="edit(unitDimension.id)">{{ $t("edit") }}</button>
              <button class="btn btn-sm btn-danger" @click="deleteUnitDimension(unitDimension.id)">
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
