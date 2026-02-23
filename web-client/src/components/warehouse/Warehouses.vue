<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";
import {useRouter} from "vue-router";

const info = ref('');
const infoType = ref('');
const warehouses = ref([]);
const searchQuery = ref('');
const page = ref(0);
const pageSize = ref(10);
const hasNext = ref(false);
const hasPrev = ref(false);
const router = useRouter();

function buildUrl() {
  const query = new URLSearchParams({
    page: String(page.value),
    size: String(pageSize.value),
    search: searchQuery.value.trim().toLowerCase(),
  });
  return '/api/warehouses?' + query;
}
async function init() {
  try {
    const response = await apiRequest(buildUrl(), 'GET');
    if (response.code === 200) {
      warehouses.value = response.data;
      hasNext.value = response.headers.hasNext;
      hasPrev.value = response.headers.hasPrev;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  } catch (error) {
    info.value = error;
    infoType.value = "error";
  }
}
function create() {
  router.push('/ui/warehouses/create');
}

function edit(warehouseId) {
  router.push('/ui/warehouses/edit/' + warehouseId);
}

function deleteWarehouse(warehouseId) {
  apiRequest('/api/warehouses/' + warehouseId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteWarehouse(warehouseId), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

function onSearch(event) {
  page.value = 0;
  searchQuery.value = event.target.value;
  init();
}

async function previousPage() {
  if (page.value === 0) {
    return;
  }
  page.value -= 1;
  await init();
}

async function nextPage() {
  if (!hasNext.value) {
    return;
  }
  page.value += 1;
  await init();
}

async function onPageSizeChange(event) {
  pageSize.value = Number(event.target.value);
  page.value = 0;
  await init();
}
onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("warehouses")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
      <div v-if="warehouses.length > 0" class="table-wrap">
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
          <tr v-for="warehouse in warehouses" :key="warehouse.id">
            <td class="mono">{{ warehouse.id }}</td>
            <td>{{ warehouse.code }}</td>
            <td>{{ warehouse.name }}</td>
            <td><input v-model="warehouse.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(warehouse.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click='deleteWarehouse(warehouse.id)'>{{ $t('delete') }}</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div v-else class="empty">
        {{ $t('noRecords') }}
      </div>
      <div class="pagination">
        <div class="page-controls">
          <button :disabled="!hasPrev" class="btn btn-sm" type="button" @click="previousPage">Prev</button>
          <span class="page-number">Page {{ page + 1 }}</span>
          <button :disabled="!hasNext" class="btn btn-sm" type="button" @click="nextPage">Next</button>
        </div>

        <label class="page-size">
          Size:
          <select :value="pageSize" @change="onPageSizeChange">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
          </select>
        </label>
      </div>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>
<style scoped>
.pagination {
  margin-top: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
}

.page-controls {
  display: flex;
  align-items: center;
  gap: 0.45rem;
}

.page-number {
  color: var(--muted);
}

.page-size {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  color: var(--muted);
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
  pointer-events: none;
}
</style>

