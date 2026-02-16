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
const currency = ref({});

function init() {
  apiRequest('/api/currencies/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      currency.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function updateCurrency() {
  apiRequest('/api/currencies/' + route.params.id, 'PATCH', currency.value).then((response) => {
    if (response.code === 200) {
      info.value = t('currency.updated');
    } else if (response.code === 401) {
      refreshToken(() => updateCurrency(), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("currency.edit")'/>

    <section class="card">
      <form @submit.prevent='updateCurrency'>
        <label>
          <input v-model='currency.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t('code') }}: <input v-model='currency.code' name='code' type='text'/></label><br/>
        <label>{{ $t('name') }}: <input v-model='currency.name' autocomplete='false' name='name'
                                        type='text'/></label><br/>
        <label>{{ $t('enabled') }}: <input v-model="currency.enabled" name="enabled" type="checkbox"></label><br/>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>