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
const brand = ref({});

function init() {
  apiRequest('/api/brands/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      brand.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function updateBrand() {
  apiRequest('/api/brands/' + route.params.id, 'PATCH', brand.value).then((response) => {
    if (response.code === 200) {
      info.value = t('brand.updated');
    } else if (response.code === 401) {
      refreshToken(() => updateBrand(), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("brand.edit")'/>

    <section class="card">
      <form @submit.prevent='updateBrand'>
        <label>
          <input v-model='brand.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t('code') }}: <input v-model='brand.code' name='code' type='text'/></label><br/>
        <label>{{ $t('name') }}: <input v-model='brand.name' autocomplete='false' name='name' type='text'/></label><br/>
        <label>{{ $t('description') }}: <input v-model='brand.description' name='factorToBase'/></label><br/>
        <label>{{ $t('enabled') }}: <input v-model="brand.enabled" name="enabled" type="checkbox"></label><br/>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>