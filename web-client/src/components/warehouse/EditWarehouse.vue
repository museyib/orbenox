<script setup>
import {onMounted, ref} from 'vue';
import {apiRequest, refreshToken} from '@/api.js';
import {useRoute, useRouter} from 'vue-router';
import {useI18n} from "vue-i18n";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const {t} = useI18n();
const router = useRouter();
const route = useRoute();
const info = ref('');
const warehouse = ref({});

function init() {
  apiRequest('/api/warehouses/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      warehouse.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function updateWarehouse() {
  apiRequest('/api/warehouses/' + route.params.id, 'PATCH', warehouse.value).then((response) => {
    if (response.code === 200) {
      info.value = t('warehouse.updated');
    } else if (response.code === 401) {
      refreshToken(() => updateWarehouse(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("warehouse.edit")'/>

    <section class="card">
      <form @submit.prevent='updateWarehouse'>
        <label>
          <input v-model='warehouse.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t('code') }}: <input v-model='warehouse.code' name='code' type='text'/></label><br/>
        <label>{{ $t('name') }}: <input v-model='warehouse.name' autocomplete='false' name='name' type='text'/></label><br/>
        <label>{{ $t('enabled') }}: <input v-model="warehouse.enabled" name="enabled" type="checkbox"></label><br/>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>