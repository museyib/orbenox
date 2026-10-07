<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import {useRoute, useRouter} from 'vue-router';
import {useI18n} from "vue-i18n";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const {t} = useI18n();
const router = useRouter();
const route = useRoute();
const info = ref('');
const infoType = ref('');
const role = ref({});

function init() {
  apiRequest('/api/roles/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      role.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

function updateRole() {
  apiRequest('/api/roles/' + route.params.id, 'PATCH', role.value).then((response) => {
    if (response.code === 200) {
      info.value = t('role.updated');
      infoType.value = "success";
    } else if (response.code === 401) {
      refreshToken(() => updateRole(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("role.edit")'/>

    <section class="card">
      <form @submit.prevent='updateRole'>
        <label>
          <input v-model='role.id' hidden="hidden" name="id" type="text"/>
        </label>
        <label>{{ $t('code') }}: <input v-model='role.code' name="code" type="text"/></label>
        <label>{{ $t('name') }}: <input v-model='role.name' autocomplete='false' name="name" type="text"/></label>
        <label>{{ $t('enabled') }}: <input v-model="role.enabled" name="enabled" type="checkbox"></label>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

