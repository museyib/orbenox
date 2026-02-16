<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const router = useRouter();
const info = ref('');
const enabled = ref(true);
const productGroups = ref([]);
const selectedProductGroup = ref(null);

function init() {
  apiRequest('/api/productGroups', 'GET').then(response => {
    if (response.code === 200) {
      productGroups.value = response.data;
      if (productGroups.value.length > 0) {
        selectedProductGroup.value = productGroups.value[0];
      }
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function createProductGroup(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.enabled = enabled.value;
  data.parentId = selectedProductGroup.value ? selectedProductGroup.value.id : null;
  apiRequest('/api/productGroups', 'POST', data).then(response => {
    if (response.code === 200) {
      router.push('/ui/productGroups');
    } else if (response.code === 401) {
      refreshToken(() => createProductGroup(event), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("productGroup.new")'/>

    <section class="card">
      <form @submit.prevent="createProductGroup">
        <label>{{ $t('code') }}: <input name="code" type="text"/></label><br/>
        <label>{{ $t('name') }}: <input autocomplete="false" name="name" type="text"/></label><br/>
        <label>{{ $t('description') }}: <input name="description" type="text"/></label><br/>
        <label>{{ $t('enabled') }}: <input v-model="enabled" name="enabled" type="checkbox"></label><br/>
        <label>{{ $t('parentGroup') }}:
          <select id="parentGroup" v-model="selectedProductGroup">
            <option :key="null"
                    :value="null">{{ $t('selectAsParent') }}
            </option>
            <option
                v-for="productGroup in productGroups"
                :key="productGroup.id"
                :value="productGroup">
              {{ productGroup.name }}
            </option>
          </select>
        </label><br/>
        <button class="btn btn-primary" type="submit">{{ $t('create') }}</button>
        <br/>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>
