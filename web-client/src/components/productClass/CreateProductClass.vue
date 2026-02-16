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

function createProductClass(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.enabled = enabled.value;
  apiRequest('/api/productClasses', 'POST', data).then(response => {
    if (response.code === 200) {
      router.push('/ui/productClasses');
    } else if (response.code === 401) {
      refreshToken(() => createProductClass(event), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("productClass.new")'/>

    <section class="card">
      <form @submit.prevent="createProductClass">
        <label>{{ $t('code') }}: <input name="code" type="text"/></label><br/>
        <label>{{ $t('name') }}: <input autocomplete="false" name="name" type="text"/></label><br/>
        <label>{{ $t('description') }}: <input name="description" type="text"/></label><br/>
        <label>{{ $t('enabled') }}: <input v-model="enabled" name="enabled" type="checkbox"></label><br/>
        <button class="btn btn-primary" type="submit">{{ $t('create') }}</button>
        <br/>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>