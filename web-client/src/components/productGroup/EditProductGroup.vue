<script setup>
import {useI18n} from 'vue-i18n';
import {onMounted, ref} from 'vue';
import {apiRequest, refreshToken} from '@/api.js';
import {useRoute, useRouter} from 'vue-router';
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";

const {t} = useI18n();
const router = useRouter();
const route = useRoute();
const info = ref('');
const infoType = ref('');
const productGroups = ref([]);
const parentGroup = ref(null);
const productGroup = ref({});

function init() {
  apiRequest('/api/productGroups/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      productGroup.value = response.data;

      apiRequest('/api/productGroups/getExcluded/' + productGroup.value.id, 'GET').then(response => {
        if (response.code === 200) {
          productGroups.value = response.data;
          parentGroup.value = productGroup.value.parent;
        } else if (response.code === 401) {
          refreshToken(() => init(), () => router.push('/ui/login'));
        } else {
          info.value = response.message;
          infoType.value = "error";
        }
      });
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

function updateProductGroup() {
  productGroup.value.parentId = parentGroup.value?.id ?? null;
  apiRequest('/api/productGroups/' + route.params.id, 'PATCH', productGroup.value).then((response) => {
    if (response.code === 200) {
      info.value = t('productGroup.updated');
      infoType.value = "success";
    } else if (response.code === 401) {
      refreshToken(() => updateProductGroup(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  });
}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("productGroup.edit")'/>

    <section class="card">
      <form @submit.prevent='updateProductGroup'>
        <label>
          <input v-model='productGroup.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t('code') }}: <input v-model='productGroup.code' name='code' type='text'/></label>
        <label>{{ $t('name') }}: <input v-model='productGroup.name' autocomplete='false' name='name'
                                        type='text'/></label>
        <label>{{ $t('description') }}: <input v-model='productGroup.description' name='factorToBase'/></label>
        <label>{{ $t('enabled') }}: <input v-model="productGroup.enabled" name="enabled" type="checkbox"></label>
        <label>{{ $t('parentGroup') }}:
          <select id="parentGroup" v-model="parentGroup">
            <option :key="null"
                    :value="null">{{ $t('selectAsParent') }}
            </option>
            <option
                v-for="productGroup in productGroups"
                :key="productGroup.id"
                :value="productGroup"
            >
              {{ productGroup.name }}
            </option>
          </select>
        </label>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

