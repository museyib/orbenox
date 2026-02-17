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
const page = ref(0);
const pageSize = ref(10);
const hasNextPage = ref(false);
const router = useRouter();

function buildUrl() {
  const query = new URLSearchParams({
    page: String(page.value),
    size: String(pageSize.value),
    search: searchQuery.value.trim().toLowerCase(),
  });
  return '/api/resources?' + query;
}

async function init() {
  try {
    const response = await apiRequest(buildUrl(), 'GET');
    if (response.code === 200) {
      resources.value = response.data;
      console.log(response);
      hasNextPage.value = response.headers.hasNextPage;
      console.log(hasNextPage.value)
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  } catch (error) {
    info.value = error;
  }
}

function create() {
  router.push('/ui/resources/create');
}

function edit(resourceId) {
  router.push('/ui/resources/edit/' + resourceId);
}

async function deleteResource(resourceId) {
  try {
    const response = await apiRequest('/api/resources/' + resourceId, 'DELETE');
    if (response.code === 200) {
      await init();
      if (resources.value.length === 0 && page.value > 0) {
        page.value -= 1;
        await init();
      }
    } else if (response.code === 401) {
      refreshToken(() => deleteResource(resourceId), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  } catch (error) {
    info.value = error;
  }
}

function onSearch(event) {
  page.value = 0;
  searchQuery.value = event.target.value;
  init()
}

async function previousPage() {
  if (page.value === 0) {
    return;
  }
  page.value -= 1;
  await init();
}

async function nextPage() {
  if (!hasNextPage.value) {
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
    <PageHeader :title='$t("resources")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
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

      <div class="pagination">
        <div class="page-controls">
          <button :disabled="page === 0" class="btn btn-sm" type="button" @click="previousPage">Prev</button>
          <span class="page-number">Page {{ page + 1 }}</span>
          <button :disabled="!hasNextPage" class="btn btn-sm" type="button" @click="nextPage">Next</button>
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
    <InfoBar :info="info"/>
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
