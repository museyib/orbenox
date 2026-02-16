<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {useRouter} from "vue-router";
import {ref} from "vue";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const router = useRouter();
const info = ref('');
const enabled = ref(true);

function createWarehouse(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.enabled = enabled.value;
  apiRequest('/api/warehouses', 'POST', data).then(response => {
    if (response.code === 200) {
      router.push('/ui/warehouses');
    } else if (response.code === 401) {
      refreshToken(() => createWarehouse(event), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("warehouse.new")'/>

    <section class="card">
      <form @submit.prevent="createWarehouse">
        <label>{{ $t('code') }}: <input name="code" type="text"/></label><br/>
        <label>{{ $t('name') }}: <input autocomplete="false" name="name" type="text"/></label><br/>
        <label>{{ $t('enabled') }}: <input v-model="enabled" name="enabled" type="checkbox"></label><br/>
        <button class="btn btn-primary" type="submit">{{ $t('create') }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>