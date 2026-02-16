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
const priceList = ref({});
const priceLists = ref([]);
const currencies = ref([]);

function init() {
  apiRequest('/api/priceLists/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      priceList.value = response.data;

      apiRequest('/api/priceLists/getExcluded/' + priceList.value.id, 'GET').then(response => {
        if (response.code === 200) {
          priceLists.value = response.data;
        } else if (response.code === 401) {
          refreshToken(() => init(), () => router.push('/ui/login'));
        } else {
          info.value = response.message;
        }
      });
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });

  apiRequest('/api/lookups?types=currencies', 'GET').then(response => {
    if (response.code === 200) {
      currencies.value = response.data.currencies;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function updatePrice() {
  priceList.value.currencyId = priceList.value.currency?.id ?? null;
  priceList.value.parentId = priceList.value.parent?.id ?? null;
  apiRequest('/api/priceLists/' + route.params.id, 'PATCH', priceList.value).then((response) => {
    if (response.code === 200) {
      info.value = t('priceList.updated');
    } else if (response.code === 401) {
      refreshToken(() => updatePrice(), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("priceList.edit")'/>

    <section class="card">
      <form @submit.prevent='updatePrice'>
        <label>
          <input v-model='priceList.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t('code') }}: <input v-model='priceList.code' name='code' type='text'/></label><br/>
        <label>{{ $t('name') }}: <input v-model='priceList.name' autocomplete='false' name='name'
                                        type='text'/></label><br/>
        <label>{{ $t('enabled') }}: <input v-model="priceList.enabled" name="enabled" type="checkbox"></label><br/>
        <label>{{ $t('factorToParent') }}:
          <input v-model.number="priceList.factorToParent"
                 name="factorToParent"
                 step="0.000001"/>
        </label><br/>
        <label>{{ $t('roundLength') }}:
        <input v-model.number="priceList.roundLength" name="roundLength"/>
        </label><br/>
        <label>{{ $t('currency.title') }}:
          <select id="currency" v-model="priceList.currency">
            <option v-for="currency in currencies"
                    :key="currency.id"
                    :value="currency">
              {{ currency.name }}
            </option>
          </select>
        </label><br/>
        <label>{{ $t('priceList.parent') }}:
          <select id="parentPrice" v-model="priceList.parent">
            <option :key="null"
                    :value="null">{{ $t('selectAsParent') }}
            </option>
            <option v-for="priceList in priceLists"
                    :key="priceList.id"
                    :value="priceList">
              {{ priceList.name }}
            </option>
          </select>
        </label><br/>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>
