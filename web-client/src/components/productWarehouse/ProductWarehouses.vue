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
const productWarehouses = ref([]);
const warehouses = ref([]);
const warehousesToInsert = ref([]);
const warehousesToDelete = ref([]);

function init() {
  apiRequest('/api/productWarehouses/' + route.params.id, 'GET').then(response => {
    if (response.code === 200) {
      currentProduct.value = response.data.product;
      productWarehouses.value = response.data.warehouses;
    } else if(response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });

  apiRequest('/api/lookups?types=warehouses', 'GET').then(response => {
    if (response.code === 200) {
      warehouses.value = response.data.warehouses;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function newWarehouse() {
  const newWarehouse = {
    product: currentProduct.value,
    warehouse: warehouses.value[0],
    quantity: 0,
    reservedQuantity: 0,
    minQuantity: 0,
    maxQuantity: 999999999
  }
  warehousesToInsert.value.push(newWarehouse);
  productWarehouses.value.push(newWarehouse)
}

function deleteWarehouse(warehouseData) {
  productWarehouses.value = productWarehouses.value.filter(w => w.id !== warehouseData.id);
  warehousesToDelete.value.push(warehouseData);
}

function updateWarehouses() {
  const warehouseData = {
    productId: currentProduct.value.id,
    warehousesToUpdate: productWarehouses.value.filter(item => item.id),
    warehousesToInsert: warehousesToInsert.value,
    warehousesToDelete: warehousesToDelete.value
  };

  apiRequest('/api/productWarehouses', 'POST', warehouseData).then(response => {
    if (response.code === 200) {
      currentProduct.value = [];
      productWarehouses.value = [];
      warehousesToInsert.value = [];
      warehousesToDelete.value = [];
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
    <PageHeader :title="$t('productWarehouses')">
      <New :on-create="newWarehouse"/>
    </PageHeader>

    <section class="card">
      <div v-if="productWarehouses.length > 0" class="table-wrap">
        <div>
          <table class="data-table" role="table">
            <thead>
            <tr>
              <th>#</th>
              <th>{{ $t('warehouse.title') }}</th>
              <th>{{ $t('quantity') }}</th>
              <th>{{ $t('reservedQuantity') }}</th>
              <th>{{ $t('freeQuantity') }}</th>
              <th>{{ $t('minQuantity') }}</th>
              <th>{{ $t('maxQuantity') }}</th>
              <th aria-hidden="true" class="actions-col"></th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="warehouseData in productWarehouses">
              <td class="mono">{{ warehouseData.id }}</td>
              <td>
                <select v-model="warehouseData.warehouse" name="warehouse">
                  <option v-for="warehouse in warehouses"
                          :key="warehouse.id"
                          :value="warehouse">
                    {{ warehouse.name }}
                  </option>
                </select>
              </td>
              <td>{{ warehouseData.quantity }}</td>
              <td>{{ warehouseData.reservedQuantity }}</td>
              <td>{{ warehouseData.freeQuantity }}</td>
              <td><input type="text" name="minQuantity" v-model="warehouseData.minQuantity" /></td>
              <td><input type="text" name="maxQuantity" v-model="warehouseData.maxQuantity" /></td>
                <td class="actions-col">
                  <button class="btn btn-sm btn-danger" @click='deleteWarehouse(warehouseData)'>X</button>
                </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-else class="empty">
        {{ $t('noRecords') }}
      </div>
      <button class="btn btn-primary" type="submit" @click="updateWarehouses">{{ $t('save') }}</button>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>

<style scoped>

</style>