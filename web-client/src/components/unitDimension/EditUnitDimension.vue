<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import {useRoute, useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const {t} = useI18n();
const router = useRouter();
const route = useRoute();
const info = ref("");
const infoType = ref('');
const unitDimension = ref({});

function init() {
  apiRequest("/api/unitDimensions/" + route.params.id, "GET").then(response => {
    if (response.code === 200) {
      unitDimension.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

function updateUnitDimension() {
  apiRequest("/api/unitDimensions/" + route.params.id, "PATCH", unitDimension.value).then(response => {
    if (response.code === 200) {
      info.value = t("unitDimensionEntity.updated");
      infoType.value = "success";
    } else if (response.code === 401) {
      refreshToken(() => updateUnitDimension(), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("unitDimensionEntity.edit")'/>

    <section class="card">
      <form @submit.prevent='updateUnitDimension'>
        <label>
          <input v-model='unitDimension.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t("code") }}: <input v-model='unitDimension.code' name='code' type='text'/></label>
        <label>{{ $t("name") }}: <input v-model='unitDimension.name' autocomplete='false' name='name'
                                        type='text'/></label>
        <label>{{ $t("enabled") }}: <input v-model="unitDimension.enabled" type="checkbox"></label>
        <button class="btn btn-primary" type="submit">{{ $t("save") }}</button>
      </form>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

