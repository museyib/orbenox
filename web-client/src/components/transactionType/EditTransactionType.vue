<script setup>
import {onMounted, ref} from 'vue';
import {apiRequest, refreshToken} from '@/api.js';
import {useRoute, useRouter} from 'vue-router';
import {useI18n} from "vue-i18n";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const {t} = useI18n();
const router = useRouter();
const route = useRoute();
const info = ref('');
const infoType = ref('');
const transactionType = ref({});
const numberingPolicy = ref({});
const resetPeriods = ref([]);
const selectedResetPeriod = ref("");
const stockAffectDirections = ref([]);
const selectedStockAffectDirection = ref("");

function init() {
  apiRequest('/api/transactionTypes/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      transactionType.value = response.data;
      selectedStockAffectDirection.value = transactionType.value.stockAffectDirection;
      numberingPolicy.value = transactionType.value.numberingPolicy;
      selectedResetPeriod.value = numberingPolicy.value.resetPeriod;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });

  apiRequest('/api/lookups?types=resetPeriods,stockAffectDirections', 'GET').then((response) => {
    if (response.code === 200) {
      resetPeriods.value = response.data.resetPeriods;
      stockAffectDirections.value = response.data.stockAffectDirections;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

function updateTransactionType() {
  transactionType.value.stockAffectDirection = selectedStockAffectDirection;
  numberingPolicy.value.resetPeriod = selectedResetPeriod;
  transactionType.value.numberingPolicy = numberingPolicy;
  apiRequest('/api/transactionTypes/' + route.params.id, 'PATCH', transactionType.value).then((response) => {
    if (response.code === 200) {
      info.value = t('transactionType.updated');
      infoType.value = "success";
    } else if (response.code === 401) {
      refreshToken(() => updateTransactionType(), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("transactionType.edit")'/>

    <section class="card">
      <form @submit.prevent='updateTransactionType'>
        <label>
          <input v-model='transactionType.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t('code') }}: <input v-model='transactionType.code' name='code' type='text'/></label>
        <label>{{ $t('name') }}: <input v-model='transactionType.name' autocomplete='false' name='name' type='text'/></label>

        <label>{{ $t('stockAffectDirection') }}:
          <select v-model="selectedStockAffectDirection" name='stockAffectDirection'>
            <option v-for="stockAffectDirection in stockAffectDirections"
                    :key="stockAffectDirection"
                    :value="stockAffectDirection">
              {{ stockAffectDirection}}
            </option>
          </select>
        </label>
        <label>{{ $t('commercialAffected') }}: <input v-model="transactionType.commercialAffected" name="commercialAffected" type="checkbox"></label>
        <label>{{ $t('accountingAffected') }}: <input v-model="transactionType.accountingAffected" name="accountingAffected" type="checkbox"></label>
        <label>{{ $t('creditLimitChecked') }}: <input v-model="transactionType.creditLimitChecked" name="creditLimitChecked" type="checkbox"></label>
        <label>{{ $t('approvalRequired') }}: <input v-model="transactionType.approvalRequired" name="approvalRequired" type="checkbox"></label>
        <label>{{ $t('enabled') }}: <input v-model="transactionType.enabled" name="enabled" type="checkbox"></label>

        <label>{{ $t('prefix') }}: <input v-model='numberingPolicy.prefix' type='text'/></label>
        <label>{{ $t('resetPeriod') }}:
          <select v-model="selectedResetPeriod" name='resetPeriod'>
            <option v-for="resetPeriod in resetPeriods"
                    :key="resetPeriod"
                    :value="resetPeriod">
              {{ resetPeriod}}
            </option>
          </select>
        </label>
        <label>{{ $t('sequenceLength') }}: <input v-model.number='numberingPolicy.sequenceLength'
                                                 name='sequenceLength'
                                                 type='number'
                                                 min='0'
                                                 step='1'/></label>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

