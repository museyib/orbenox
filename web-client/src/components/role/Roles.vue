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
  return '/api/roles?' + query;
}
async function init() {
  try {
    const response = await apiRequest(buildUrl(), 'GET');
    if (response.code === 200) {
      roles.value = response.data;
      hasNext.value = response.headers.hasNext;
      hasPrev.value = response.headers.hasPrev;
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

