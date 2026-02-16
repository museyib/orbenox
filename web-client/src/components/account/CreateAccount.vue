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
const accountTypes = ref([]);
const selectedAccountType = ref("");

function init() {
  apiRequest("/api/lookups?types=accountTypes", "GET").then(response => {
    if (response.code === 200) {
      accountTypes.value = response.data.accountTypes || [];
      if (accountTypes.value.length > 0) {
        selectedAccountType.value = accountTypes.value[0];
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

function createAccount(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.enabled = enabled.value;
  data.accountType = selectedAccountType.value;
  apiRequest("/api/accounts", "POST", data).then(response => {
    if (response.code === 200) {
      router.push("/ui/accounts");
    } else if (response.code === 401) {
      refreshToken(() => createAccount(event), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("account.new")'/>

    <section class="card">
      <form @submit.prevent="createAccount">
        <label>{{ $t("code") }}: <input name="code" type="text"/></label><br/>
        <label>{{ $t("name") }}: <input autocomplete="false" name="name" type="text"/></label><br/>
        <label>{{ $t("accountType") }}:
          <select v-model="selectedAccountType">
            <option v-for="accountType in accountTypes" :key="accountType" :value="accountType">
              {{ accountType }}
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
