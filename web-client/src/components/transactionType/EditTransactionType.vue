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
const transactionType = ref({});

function init() {
  apiRequest('/api/transactionTypes/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      transactionType.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function updateTransactionType() {
  apiRequest('/api/transactionTypes/' + route.params.id, 'PATCH', transactionType.value).then((response) => {
    if (response.code === 200) {
      info.value = t('transactionType.updated');
    } else if (response.code === 401) {
      refreshToken(() => updateTransactionType(), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("transactionType.edit")'/>

    <section class="card">
      <form @submit.prevent='updateTransactionType'>
        <label>
          <input v-model='transactionType.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t('code') }}: <input v-model='transactionType.code' name='code' type='text'/></label><br/>
        <label>{{ $t('name') }}: <input v-model='transactionType.name' autocomplete='false' name='name' type='text'/></label><br/>
        <label>{{ $t('enabled') }}: <input v-model="transactionType.enabled" name="enabled" type="checkbox"></label><br/>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>