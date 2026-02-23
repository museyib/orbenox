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
const postingRule = ref({});
const transactionTypes = ref([]);
const accounts = ref([]);
const amountSources = ref([]);
const partnerSides = ref([]);
const selectedTransactionType = ref();
const selectedDebitAccount = ref();
const selectedCreditAccount = ref();

function init() {
  apiRequest("/api/postingRules/" + route.params.id, "GET").then(response => {
    if (response.code === 200) {
      postingRule.value = response.data;
      selectedTransactionType.value = {
        id: postingRule.value.transactionTypeId,
        code: postingRule.value.transactionTypeCode
      };
      selectedDebitAccount.value = {
        id: postingRule.value.debitAccountId,
        code: postingRule.value.debitAccountCode
      };
      selectedCreditAccount.value = {
        id: postingRule.value.creditAccountId,
        code: postingRule.value.creditAccountCode
      };
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

  apiRequest("/api/lookups?types=transactionTypes,accounts,amountSources,partnerSides", "GET").then(response => {
    if (response.code === 200) {
      transactionTypes.value = response.data.transactionTypes || [];
      accounts.value = response.data.accounts || [];
      amountSources.value = response.data.amountSources || [];
      partnerSides.value = response.data.partnerSides || [];
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

function updatePostingRule() {
  postingRule.value.transactionTypeId = selectedTransactionType.value?.id ?? null;
  postingRule.value.debitAccountId = selectedDebitAccount.value?.id ?? null;
  postingRule.value.creditAccountId = selectedCreditAccount.value?.id ?? null;

  apiRequest("/api/postingRules/" + route.params.id, "PATCH", postingRule.value).then(response => {
    if (response.code === 200) {
      info.value = t("postingRule.updated");
      infoType.value = "success";
    } else if (response.code === 401) {
      refreshToken(() => updatePostingRule(), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("postingRule.edit")'/>

    <section class="card">
      <form @submit.prevent='updatePostingRule'>
        <label>
          <input v-model='postingRule.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t("sequence") }}: <input v-model='postingRule.sequence' name='sequence' type='number'/></label>
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
          <select v-model="postingRule.amountSource">
            <option v-for="amountSource in amountSources" :key="amountSource" :value="amountSource">
              {{ amountSource }}
            </option>
          </select>
        </label>
        <label>{{ $t("partnerSide") }}:
          <select v-model="postingRule.partnerSide">
            <option v-for="partnerSide in partnerSides" :key="partnerSide" :value="partnerSide">
              {{ partnerSide }}
            </option>
          </select>
        </label>
        <button class="btn btn-primary" type="submit">{{ $t("save") }}</button>
      </form>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

