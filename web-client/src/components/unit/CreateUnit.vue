<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const router = useRouter();
const info = ref('');
const unitDimensions = ref([]);
const selectedUnitDimension = ref();
const isBase = ref(false);
const enabled = ref(true);

function init() {
  apiRequest('/api/lookups?types=unitDimensions', 'GET').then((response) => {
    if (response.code === 200) {
      unitDimensions.value = response.data.unitDimensions;
      if (unitDimensions.value.length > 0) {
        selectedUnitDimension.value = unitDimensions.value[0];
      }
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function createUnit(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.base = isBase.value;
  data.enabled = enabled.value;
  data.unitDimensionId = selectedUnitDimension.value?.id ?? null;
  apiRequest('/api/units', 'POST', data).then(response => {
    if (response.code === 200) {
      router.push('/ui/units');
    } else if (response.code === 401) {
      refreshToken(() => createUnit(event), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("unit.new")'/>

    <section class="card">
      <form @submit.prevent="createUnit">
        <label>{{ $t('code') }}: <input name="code" type="text"/></label><br/>
        <label>{{ $t('name') }}: <input autocomplete="false" name="name" type="text"/></label><br/>
        <label>{{ $t('factorToBase') }}: <input name="factorToBase" step="0.000001" type="number"/></label><br/>
        <label>{{ $t('offsetToBase') }}: <input name="offsetToBase" step="0.000001" type="number"/></label><br/>
        <label>{{ $t('isBase') }}: <input v-model='isBase' name="isBase" type="checkbox"/></label><br/>
        <label>{{ $t('enabled') }}: <input v-model="enabled" name="enabled" type="checkbox"></label>
        <label>{{ $t('unitDimension') }}:
          <select id="unitDimension" v-model="selectedUnitDimension">
            <option
                v-for="unitDimension in unitDimensions"
                :key="unitDimension.id"
                :value="unitDimension">
              {{ unitDimension.name }}
            </option>
          </select>
        </label><br/>
        <button class="btn btn-primary" type="submit">{{ $t('create') }}</button>
        <br/>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>
