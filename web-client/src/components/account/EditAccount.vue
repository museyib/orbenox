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
const account = ref({});
const accountTypes = ref([]);

function init() {
  apiRequest("/api/accounts/" + route.params.id, "GET").then(response => {
    if (response.code === 200) {
      account.value = response.data;
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

  apiRequest("/api/lookups?types=accountTypes", "GET").then(response => {
    if (response.code === 200) {
      accountTypes.value = response.data.accountTypes || [];
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

function updateAccount() {
  apiRequest("/api/accounts/" + route.params.id, "PATCH", account.value).then(response => {
    if (response.code === 200) {
      info.value = t("account.updated");
      infoType.value = "success";
    } else if (response.code === 401) {
      refreshToken(() => updateAccount(), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("account.edit")'/>

    <section class="card">
      <form @submit.prevent='updateAccount'>
        <label>
          <input v-model='account.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t("code") }}: <input v-model='account.code' name='code' type='text'/></label>
        <label>{{ $t("name") }}: <input v-model='account.name' autocomplete='false' name='name'
                                        type='text'/></label>
        <label>{{ $t("accountType") }}:
          <select v-model="account.accountType">
            <option v-for="accountType in accountTypes" :key="accountType" :value="accountType">
              {{ accountType }}
            </option>
          </select>
        </label>
        <label>{{ $t("enabled") }}: <input v-model="account.enabled" type="checkbox"></label>
        <button class="btn btn-primary" type="submit">{{ $t("save") }}</button>
      </form>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

