<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {useRouter} from "vue-router";
import {ref} from "vue";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const router = useRouter();
const info = ref('');
const infoType = ref('');
const enabled = ref(true);

function createProductType(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.enabled = enabled.value;
  apiRequest('/api/productTypes', 'POST', data).then(response => {
    if (response.code === 200) {
      router.push('/ui/productTypes');
    } else if (response.code === 401) {
      refreshToken(() => createProductType(event), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("productType.new")'/>

    <section class="card">
      <form @submit.prevent="createProductType">
        <label>{{ $t('code') }}: <input name="code" type="text"/></label>
        <label>{{ $t('name') }}: <input autocomplete="false" name="name" type="text"/></label>
        <label>{{ $t('description') }}: <input name="description" type="text"/></label>
        <label>{{ $t('enabled') }}: <input v-model="enabled" name="enabled" type="checkbox"></label>
        <button class="btn btn-primary" type="submit">{{ $t('create') }}</button>

      </form>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

