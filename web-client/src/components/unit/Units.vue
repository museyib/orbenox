<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import {useRouter} from "vue-router";

const info = ref('');
const units = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/units', 'GET').then(response => {
    if (response.code === 200) {
      units.value = response.data;
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
  router.push('/ui/units/create');
}

function edit(unitId) {
  router.push('/ui/units/edit/' + unitId);
}

function deleteUnit(unitId) {
  apiRequest('/api/units/' + unitId, 'DELETE').then((response) => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deleteUnit(unitId), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("units")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card">
      <div v-if="units.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t('code') }}</th>
            <th>{{ $t('name') }}</th>
            <th>{{ $t('unitDimension') }}</th>
            <th>{{ $t('isBase') }}</th>
            <th>{{ $t('factorToBase') }}</th>
            <th>{{ $t('offsetToBase') }}</th>
            <th>{{ $t('enabled') }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="unit in units" :key="unit.id">
            <td class="mono">{{ unit.id }}</td>
            <td>{{ unit.code }}</td>
            <td>{{ unit.name }}</td>
            <td>{{ unit.unitDimension.name }}</td>
            <td><input v-model="unit.base" name="enabled" type="checkbox"/>
            </td>
            <td>{{ unit.factorToBase }}</td>
            <td>{{ unit.offsetToBase }}</td>
            <td>
              <input
                  name="enabled"
                  v-model="unit.enabled"
                  :aria-label="`Toggle enabled for ${unit.username}`"
                  type="checkbox"
              />
            </td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(unit.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click='deleteUnit(unit.id)'>{{ $t('delete') }}</button>
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