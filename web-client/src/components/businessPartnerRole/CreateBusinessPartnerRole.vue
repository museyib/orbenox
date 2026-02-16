<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";

const router = useRouter();
const info = ref("");
const enabled = ref(true);
const businessPartners = ref([]);
const partnerRoles = ref([]);
const selectedBusinessPartner = ref();
const selectedPartnerRole = ref();

function init() {
  apiRequest("/api/lookups?types=businessPartners,partnerRoles", "GET").then(response => {
    if (response.code === 200) {
      businessPartners.value = response.data.businessPartners || [];
      partnerRoles.value = response.data.partnerRoles || [];

      if (businessPartners.value.length > 0) selectedBusinessPartner.value = businessPartners.value[0];
      if (partnerRoles.value.length > 0) selectedPartnerRole.value = partnerRoles.value[0];
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function createBusinessPartnerRole(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.enabled = enabled.value;
  data.partnerId = selectedBusinessPartner.value?.id ?? null;
  data.role = selectedPartnerRole.value;

  apiRequest("/api/businessPartnerRoles", "POST", data).then(response => {
    if (response.code === 200) {
      router.push("/ui/businessPartnerRoles");
    } else if (response.code === 401) {
      refreshToken(() => createBusinessPartnerRole(event), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("businessPartnerRole.new")'/>

    <section class="card">
      <form @submit.prevent="createBusinessPartnerRole">
        <label>{{ $t("businessPartner.title") }}:
          <select v-model="selectedBusinessPartner">
            <option v-for="businessPartner in businessPartners" :key="businessPartner.id" :value="businessPartner">
              {{ businessPartner.code }} - {{ businessPartner.name }}
            </option>
          </select>
        </label><br/>
        <label>{{ $t("partnerRole") }}:
          <select v-model="selectedPartnerRole">
            <option v-for="partnerRole in partnerRoles" :key="partnerRole" :value="partnerRole">
              {{ partnerRole }}
            </option>
          </select>
        </label><br/>
        <label>{{ $t("enabled") }}: <input v-model="enabled" type="checkbox"></label><br/>
        <button class="btn btn-primary" type="submit">{{ $t("create") }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>
