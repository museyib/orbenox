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
const partnerTypes = ref([]);
const selectedPartnerType = ref("");

function init() {
  apiRequest("/api/lookups?types=partnerTypes", "GET").then(response => {
    if (response.code === 200) {
      partnerTypes.value = response.data.partnerTypes || [];
      if (partnerTypes.value.length > 0) {
        selectedPartnerType.value = partnerTypes.value[0];
      }
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function createBusinessPartner(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.enabled = enabled.value;
  data.type = selectedPartnerType.value;
  apiRequest("/api/businessPartners", "POST", data).then(response => {
    if (response.code === 200) {
      router.push("/ui/businessPartners");
    } else if (response.code === 401) {
      refreshToken(() => createBusinessPartner(event), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("businessPartner.new")'/>

    <section class="card">
      <form @submit.prevent="createBusinessPartner">
        <label>{{ $t("code") }}: <input name="code" type="text"/></label><br/>
        <label>{{ $t("name") }}: <input autocomplete="false" name="name" type="text"/></label><br/>
        <label>{{ $t("taxId") }}: <input name="taxId" type="text"/></label><br/>
        <label>{{ $t("partnerType") }}:
          <select v-model="selectedPartnerType">
            <option v-for="partnerType in partnerTypes" :key="partnerType" :value="partnerType">
              {{ partnerType }}
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
