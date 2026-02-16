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
const businessPartner = ref({});
const partnerTypes = ref([]);

function init() {
  apiRequest("/api/businessPartners/" + route.params.id, "GET").then(response => {
    if (response.code === 200) {
      businessPartner.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });

  apiRequest("/api/lookups?types=partnerTypes", "GET").then(response => {
    if (response.code === 200) {
      partnerTypes.value = response.data.partnerTypes || [];
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function updateBusinessPartner() {
  apiRequest("/api/businessPartners/" + route.params.id, "PATCH", businessPartner.value).then(response => {
    if (response.code === 200) {
      info.value = t("businessPartner.updated");
    } else if (response.code === 401) {
      refreshToken(() => updateBusinessPartner(), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("businessPartner.edit")'/>

    <section class="card">
      <form @submit.prevent='updateBusinessPartner'>
        <label>
          <input v-model='businessPartner.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t("code") }}: <input v-model='businessPartner.code' name='code' type='text'/></label><br/>
        <label>{{ $t("name") }}: <input v-model='businessPartner.name' autocomplete='false' name='name'
                                        type='text'/></label><br/>
        <label>{{ $t("taxId") }}: <input v-model='businessPartner.taxId' name='taxId' type='text'/></label><br/>
        <label>{{ $t("partnerType") }}:
          <select v-model="businessPartner.type">
            <option v-for="partnerType in partnerTypes" :key="partnerType" :value="partnerType">
              {{ partnerType }}
            </option>
          </select>
        </label><br/>
        <label>{{ $t("enabled") }}: <input v-model="businessPartner.enabled" type="checkbox"></label><br/>
        <button class="btn btn-primary" type="submit">{{ $t("save") }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>
