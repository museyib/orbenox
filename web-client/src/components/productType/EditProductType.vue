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
const productType = ref({});

function init() {
  apiRequest('/api/productTypes/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      productType.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function updateProductType() {
  apiRequest('/api/productTypes/' + route.params.id, 'PATCH', productType.value).then((response) => {
    if (response.code === 200) {
      info.value = t('productType.updated');
    } else if (response.code === 401) {
      refreshToken(() => updateProductType(), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("productType.edit")'/>

    <section class="card">
      <form @submit.prevent='updateProductType'>
        <label>
          <input v-model='productType.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t('code') }}: <input v-model='productType.code' name='code' type='text'/></label><br/>
        <label>{{ $t('name') }}: <input v-model='productType.name' autocomplete='false' name='name'
                                        type='text'/></label><br/>
        <label>{{ $t('description') }}: <input v-model='productType.description' name='factorToBase'/></label><br/>
        <label>{{ $t('enabled') }}: <input v-model="productType.enabled" name="enabled" type="checkbox"></label><br/>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>