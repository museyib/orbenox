<script setup>

import MainLayout from "@/components/MainLayout.vue";
import New from "@/components/New.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRoute, useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";

const route = useRoute();
const router = useRouter();
const info = ref('');
const currentProduct = ref();
const productUnits = ref([]);
const units = ref([]);
const unitsToInsert = ref([]);
const unitsToDelete = ref([]);

function init() {
  apiRequest('/api/productUnits/' + route.params.id, 'GET').then(response => {
    if (response.code === 200) {
      currentProduct.value = response.data.product;
      productUnits.value = response.data.units;
    } else if(response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });

  apiRequest('/api/lookups?types=units', 'GET').then(response => {
    if (response.code === 200) {
      units.value = response.data.units;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function newUnit() {
  const newUnit = {
    product: currentProduct.value,
    unit: units.value[0],
    factorToBase: 1
  }
  unitsToInsert.value.push(newUnit);
  productUnits.value.push(newUnit)
}

function deleteUnit(unitData) {
  productUnits.value = productUnits.value.filter(b => b.id !== unitData.id);
  unitsToDelete.value.push(unitData);
}

function updateUnits() {
  const unitData = {
    productId: currentProduct.value.id,
    unitsToUpdate: productUnits.value.filter(item => item.id),
    unitsToInsert: unitsToInsert.value,
    unitsToDelete: unitsToDelete.value
  };

  apiRequest('/api/productUnits', 'POST', unitData).then(response => {
    if (response.code === 200) {
      currentProduct.value = [];
      productUnits.value = [];
      unitsToInsert.value = [];
      unitsToDelete.value = [];
      init();
    } else if(response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
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
    <PageHeader :title="$t('productUnits')">
      <New :on-create="newUnit"/>
    </PageHeader>

    <section class="card">
      <div v-if="productUnits.length > 0" class="table-wrap">
        <div>
          <table class="data-table" role="table">
            <thead>
            <tr>
              <th>#</th>
              <th>{{ $t('unit.title') }}</th>
              <th>{{ $t('factorToBase') }}</th>
              <th aria-hidden="true" class="actions-col"></th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="unitData in productUnits">
              <td class="mono">{{ unitData.id }}</td>
              <td>
                <select v-model="unitData.unit" name="unit">
                  <option v-for="unit in units"
                          :key="unit.id"
                          :value="unit">
                    {{ unit.name }}
                  </option>
                </select>
              </td>
              <td><input type="text" name="factorToBase" v-model="unitData.factorToBase" /></td>
                <td class="actions-col">
                  <button class="btn btn-sm btn-danger" @click='deleteUnit(unitData)'>X</button>
                </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-else class="empty">
        {{ $t('noRecords') }}
      </div>
      <button class="btn btn-primary" type="submit" @click="updateUnits">{{ $t('save') }}</button>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>

<style scoped>

</style>