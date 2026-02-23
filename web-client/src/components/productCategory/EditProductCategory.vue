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
const infoType = ref('');
const productCategory = ref({});

function init() {
  apiRequest('/api/productCategories/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      productCategory.value = response.data;
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

function updateProductCategory() {
  apiRequest('/api/productCategories/' + route.params.id, 'PATCH', productCategory.value).then((response) => {
    if (response.code === 200) {
      info.value = t('productCategory.updated');
      infoType.value = "success";
    } else if (response.code === 401) {
      refreshToken(() => updateProductCategory(), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("productCategory.edit")'/>

    <section class="card">
      <form @submit.prevent='updateProductCategory'>
        <label>
          <input v-model='productCategory.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t('code') }}: <input v-model='productCategory.code' name='code' type='text'/></label>
        <label>{{ $t('name') }}: <input v-model='productCategory.name' autocomplete='false' name='name'
                                        type='text'/></label>
        <label>{{ $t('description') }}: <input v-model='productCategory.description' name='factorToBase'/></label>
        <label>{{ $t('enabled') }}: <input v-model="productCategory.enabled" name="enabled"
                                           type="checkbox"></label>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

