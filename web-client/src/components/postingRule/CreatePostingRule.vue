<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";

const router = useRouter();
const info = ref("");
const infoType = ref('');
const transactionTypes = ref([]);
const accounts = ref([]);
const amountSources = ref([]);
const partnerSides = ref([]);
const selectedTransactionType = ref();
const selectedDebitAccount = ref();
const selectedCreditAccount = ref();
const selectedAmountSource = ref();
const selectedPartnerSide = ref();

function init() {
  apiRequest("/api/lookups?types=transactionTypes,accounts,amountSources,partnerSides", "GET").then(response => {
    if (response.code === 200) {
      transactionTypes.value = response.data.transactionTypes || [];
      accounts.value = response.data.accounts || [];
      amountSources.value = response.data.amountSources || [];
      partnerSides.value = response.data.partnerSides || [];

      if (transactionTypes.value.length > 0) selectedTransactionType.value = transactionTypes.value[0];
      if (accounts.value.length > 0) {
        selectedDebitAccount.value = accounts.value[0];
        selectedCreditAccount.value = accounts.value[0];
      }
      if (amountSources.value.length > 0) selectedAmountSource.value = amountSources.value[0];
      if (partnerSides.value.length > 0) selectedPartnerSide.value = partnerSides.value[0];
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

function createPostingRule(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.transactionTypeId = selectedTransactionType.value?.id ?? null;
  data.debitAccountId = selectedDebitAccount.value?.id ?? null;
  data.creditAccountId = selectedCreditAccount.value?.id ?? null;
  data.amountSource = selectedAmountSource.value;
  data.partnerSide = selectedPartnerSide.value;

  apiRequest("/api/postingRules", "POST", data).then(response => {
    if (response.code === 200) {
      router.push("/ui/postingRules");
    } else if (response.code === 401) {
      refreshToken(() => createPostingRule(event), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("postingRule.new")'/>

    <section class="card">
      <form @submit.prevent="createPostingRule">
        <label>{{ $t("sequence") }}: <input name="sequence" type="number"/></label>
        <label>{{ $t("transactionType.title") }}:
          <select v-model="selectedTransactionType">
            <option v-for="transactionType in transactionTypes" :key="transactionType.id" :value="transactionType">
              {{ transactionType.code }}
            </option>
          </select>
        </label>
        <label>{{ $t("debitAccount") }}:
          <select v-model="selectedDebitAccount">
            <option v-for="account in accounts" :key="account.id" :value="account">
              {{ account.code }}
            </option>
          </select>
        </label>
        <label>{{ $t("creditAccount") }}:
          <select v-model="selectedCreditAccount">
            <option v-for="account in accounts" :key="account.id" :value="account">
              {{ account.code }}
            </option>
          </select>
        </label>
        <label>{{ $t("amountSource") }}:
          <select v-model="selectedAmountSource">
            <option v-for="amountSource in amountSources" :key="amountSource" :value="amountSource">
              {{ amountSource }}
            </option>
          </select>
        </label>
        <label>{{ $t("partnerSide") }}:
          <select v-model="selectedPartnerSide">
            <option v-for="partnerSide in partnerSides" :key="partnerSide" :value="partnerSide">
              {{ partnerSide }}
            </option>
          </select>
        </label>
        <button class="btn btn-primary" type="submit">{{ $t("create") }}</button>
      </form>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

