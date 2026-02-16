<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const router = useRouter();
const info = ref('');
const enabled = ref(true);
const priceLists = ref([]);
const parentPrice = ref(null);
const currencies = ref([]);
const selectedCurrency = ref();
const factorToParent = ref(1);
const roundLength = ref(4);

function init() {
  apiRequest('/api/lookups?types=currencies', 'GET').then(response => {
    if (response.code === 200) {
      currencies.value = response.data.currencies;
      if (currencies.value.length > 0) {
        selectedCurrency.value = currencies.value[0];
      }
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });

  apiRequest('/api/priceLists', 'GET').then(response => {
    if (response.code === 200) {
      priceLists.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function createPrice(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.enabled = enabled.value;
  data.currencyId = selectedCurrency.value.id;
  data.parent = parentPrice.value;
  data.factorToParent = factorToParent.value;
  data.roundLength = roundLength.value;
  apiRequest('/api/priceLists', 'POST', data).then(response => {
    if (response.code === 200) {
      router.push('/ui/priceLists');
    } else if (response.code === 401) {
      refreshToken(() => createPrice(event), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("priceList.new")'/>

    <section class="card">
      <form @submit.prevent="createPrice">
        <label>{{ $t('code') }}: <input name="code" type="text"/></label><br/>
        <label>{{ $t('name') }}: <input autocomplete="false" name="name" type="text"/></label><br/>
        <label>{{ $t('factorToParent') }}: <input v-model.number="factorToParent" name="factorToParent" step="0.000001"/>
        </label><br/>
        <label>{{ $t('roundLength') }}:
          <input v-model.number="roundLength" name="roundLength"/>
        </label><br/>
        <label>{{ $t('enabled') }}: <input v-model="enabled" name="enabled" type="checkbox"></label><br/>
        <label>{{ $t('currency.title') }}:
          <select id="currency" v-model="selectedCurrency">
            <option v-for="currency in currencies"
                    :key="currency.id"
                    :value="currency">
              {{ currency.name }}
            </option>
          </select>
        </label><br/>
        <label>{{ $t('priceList.parent') }}:
          <select id="parentPrice" v-model="parentPrice">
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
        <button class="btn btn-primary" type="submit">{{ $t('create') }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>