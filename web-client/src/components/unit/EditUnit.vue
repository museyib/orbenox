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
const unit = ref({});

const units = ref([]);
const convertFrom = ref();
const convertTo = ref();
const convertValue = ref(1);
const convertResult = ref(1);

function convertNumber(value, from, to, scale = 6) {
  if (!value || !from || !to)
    return 0;
  if (from.code === to.code)
    return Number(value);

  const v = Number(value);
  const inBase = (v + Number(from.offsetToBase || 0)) * Number(from.factorToBase);
  const toVal = inBase / Number(to.factorToBase) - Number(to.offsetToBase || 0);

  return Number(Number(toVal).toFixed(scale));
}

const executeConvertLocal = () => {
  convertResult.value = convertNumber(convertValue.value, convertFrom.value, convertTo.value);
}

const unitDimensions = ref([]);

function init() {
  apiRequest('/api/units/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      unit.value = response.data;
      convertFrom.value = response.data;

      apiRequest('/api/units/byDimension/' + unit.value.unitDimension.id, 'GET').then((response) => {
        if (response.code === 200) {
          units.value = response.data;
          if (units.value.length > 0) {
            convertTo.value = units.value[0];
            convertResult.value = convertNumber(1, convertFrom.value, convertTo.value);
          }
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

  apiRequest('/api/lookups?types=unitDimensions', 'GET').then((response) => {
    if (response.code === 200) {
      unitDimensions.value = response.data.unitDimensions;
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

function updateUnit() {
  unit.value.unitDimensionId = unit.value.unitDimension?.id ?? null;
  apiRequest('/api/units/' + route.params.id, 'PATCH', unit.value).then((response) => {
    if (response.code === 200) {
      info.value = t('unit.updated');
      infoType.value = "success";
    } else if (response.code === 401) {
      refreshToken(() => updateUnit(), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("unit.edit")'/>

    <section class="card">
      <form @submit.prevent='updateUnit'>
        <label>
          <input v-model='unit.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t('code') }}: <input v-model='unit.code' name='code' type='text'/></label>
        <label>{{ $t('name') }}: <input v-model='unit.name' autocomplete='false' name='name' type='text'/></label>
        <label>{{ $t('factorToBase') }}:
          <input v-model='unit.factorToBase' name='factorToBase' type='number' step='0.000001'/></label>
        <label>{{ $t('offsetToBase') }}:
          <input v-model='unit.offsetToBase' name='offsetToBase' type='number' step='0.000001'/></label>
        <label>{{ $t('isBase') }}: <input v-model='unit.base' name='isBase' type='checkbox'/></label>
        <label>{{ $t('enabled') }}: <input v-model="unit.enabled" name="enabled" type="checkbox"></label>
        <label>{{ $t('unitDimension') }}:
          <select id='unitDimension' v-model='unit.unitDimension'>
            <option
                v-for='unitDimension in unitDimensions'
                :key='unitDimension.id'
                :selected='unit.unitDimension === unitDimension'
                :value='unitDimension'>
              {{ unitDimension.name }}
            </option>
          </select>
        </label>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>

      <div>
        <h3>Converter</h3>
        <input v-model.number='convertValue' name='value' type='number' step='0.000001'
               @input='executeConvertLocal'>
        <span>{{ unit.code }}</span>
        <input v-model.number='convertResult' name='result' type='number' step='0.000001'>
        <select id='convertTarget' v-model='convertTo' @change='executeConvertLocal'>
          <option
              v-for='unit in units'
              :key='unit.id'
              :value='unit'>
            {{ unit.name }}
          </option>
        </select>
      </div>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

