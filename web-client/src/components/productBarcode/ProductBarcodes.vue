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
const productBarcodes = ref([]);
const units = ref([]);
const barcodesToInsert = ref([]);
const barcodesToDelete = ref([]);

function init() {
  apiRequest('/api/productBarcodes/' + route.params.id, 'GET').then(response => {
    if (response.code === 200) {
      currentProduct.value = response.data.product;
      productBarcodes.value = response.data.barcodes;
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

function newBarcode() {
  const newBarcode = {
    product: currentProduct.value,
    unit: units.value[0],
    barcode: ''
  }
  barcodesToInsert.value.push(newBarcode);
  productBarcodes.value.push(newBarcode)
}

function deleteBarcode(barcodeData) {
  productBarcodes.value = productBarcodes.value.filter(b => b.id !== barcodeData.id);
  barcodesToDelete.value.push(barcodeData);
}

function updateBarcodes() {
  const toBarcodeDto = (item, includeId = false) => {
    const dto = {
      productId: currentProduct.value.id,
      unitId: item.unit?.id ?? item.unitId,
      barcode: item.barcode
    };
    if (includeId) {
      dto.id = item.id;
    }
    return dto;
  };

  const barcodeData = {
    productId: currentProduct.value.id,
    barcodesToUpdate: productBarcodes.value.filter(item => item.id).map(item => toBarcodeDto(item, true)),
    barcodesToInsert: barcodesToInsert.value.map(item => toBarcodeDto(item)),
    barcodesToDelete: barcodesToDelete.value.map(item => toBarcodeDto(item, true))
  };

  apiRequest('/api/productBarcodes', 'POST', barcodeData).then(response => {
    if (response.code === 200) {
      currentProduct.value = [];
      productBarcodes.value = [];
      barcodesToInsert.value = [];
      barcodesToDelete.value = [];
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
    <PageHeader :title="$t('productBarcodes')">
      <New :on-create="newBarcode"/>
    </PageHeader>

    <section class="card">
      <div v-if="productBarcodes.length > 0" class="table-wrap">
        <div>
          <table class="data-table" role="table">
            <thead>
            <tr>
              <th>#</th>
              <th>{{ $t('unit.title') }}</th>
              <th>{{ $t('barcode') }}</th>
              <th aria-hidden="true" class="actions-col"></th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="barcodeData in productBarcodes">
              <td class="mono">{{ barcodeData.id }}</td>
              <td>
                <select v-model="barcodeData.unit" name="unit">
                  <option v-for="unit in units"
                          :key="unit.id"
                          :value="unit">
                    {{ unit.name }}
                  </option>
                </select>
              </td>
              <td><input type="text" name="barcode" v-model="barcodeData.barcode" /></td>
                <td class="actions-col">
                  <div v-if="currentProduct.value && currentProduct.value.defaultBarcode === barcodeData.barcode">
                  <button class="btn btn-sm btn-danger" @click='deleteBarcode(barcodeData)'>X</button>
                  </div>
                </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-else class="empty">
        {{ $t('noRecords') }}
      </div>
      <button class="btn btn-primary" type="submit" @click="updateBarcodes">{{ $t('save') }}</button>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>

<style scoped>

</style>
