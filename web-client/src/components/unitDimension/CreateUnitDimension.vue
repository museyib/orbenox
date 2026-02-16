<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {ref} from "vue";
import {useRouter} from "vue-router";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";

const router = useRouter();
const info = ref("");
const enabled = ref(true);

function createUnitDimension(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.enabled = enabled.value;
  apiRequest("/api/unitDimensions", "POST", data).then(response => {
    if (response.code === 200) {
      router.push("/ui/unitDimensions");
    } else if (response.code === 401) {
      refreshToken(() => createUnitDimension(event), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("unitDimensionEntity.new")'/>

    <section class="card">
      <form @submit.prevent="createUnitDimension">
        <label>{{ $t("code") }}: <input name="code" type="text"/></label><br/>
        <label>{{ $t("name") }}: <input autocomplete="false" name="name" type="text"/></label><br/>
        <label>{{ $t("enabled") }}: <input v-model="enabled" type="checkbox"></label><br/>
        <button class="btn btn-primary" type="submit">{{ $t("create") }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>
