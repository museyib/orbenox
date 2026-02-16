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
const businessPartnerRole = ref({});
const businessPartners = ref([]);
const partnerRoles = ref([]);
const selectedBusinessPartner = ref();

function init() {
  apiRequest("/api/businessPartnerRoles/" + route.params.id, "GET").then(response => {
    if (response.code === 200) {
      businessPartnerRole.value = response.data;
      selectedBusinessPartner.value = {
        id: businessPartnerRole.value.partnerId,
        code: businessPartnerRole.value.partnerCode,
        name: businessPartnerRole.value.partnerName
      };
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });

  apiRequest("/api/lookups?types=businessPartners,partnerRoles", "GET").then(response => {
    if (response.code === 200) {
      businessPartners.value = response.data.businessPartners || [];
      partnerRoles.value = response.data.partnerRoles || [];
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function updateBusinessPartnerRole() {
  businessPartnerRole.value.partnerId = selectedBusinessPartner.value?.id ?? null;
  apiRequest("/api/businessPartnerRoles/" + route.params.id, "PATCH", businessPartnerRole.value).then(response => {
    if (response.code === 200) {
      info.value = t("businessPartnerRole.updated");
    } else if (response.code === 401) {
      refreshToken(() => updateBusinessPartnerRole(), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("businessPartnerRole.edit")'/>

    <section class="card">
      <form @submit.prevent='updateBusinessPartnerRole'>
        <label>
          <input v-model='businessPartnerRole.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t("businessPartner.title") }}:
          <select v-model="selectedBusinessPartner">
            <option v-for="businessPartner in businessPartners" :key="businessPartner.id" :value="businessPartner">
              {{ businessPartner.code }} - {{ businessPartner.name }}
            </option>
          </select>
        </label><br/>
        <label>{{ $t("partnerRole") }}:
          <select v-model="businessPartnerRole.role">
            <option v-for="partnerRole in partnerRoles" :key="partnerRole" :value="partnerRole">
              {{ partnerRole }}
            </option>
          </select>
        </label><br/>
        <label>{{ $t("enabled") }}: <input v-model="businessPartnerRole.enabled" type="checkbox"></label><br/>
        <button class="btn btn-primary" type="submit">{{ $t("save") }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>
