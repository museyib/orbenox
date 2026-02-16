<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRouter} from "vue-router";

const info = ref('');
const products = ref([]);
const searchQuery = ref('');
const router = useRouter();

function init() {
  apiRequest('/api/products', 'GET').then(response => {
    if (response.code === 200) {
      products.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function create() {
  router.push('/ui/products/create');
}

function edit(productId) {
  router.push('/ui/products/edit/' + productId);
}

function deleteProduct(productId) {
  apiRequest('/api/products/' + productId, 'DELETE').then((response) => {
    if (response.code === 200) {
      refreshToken(() => deleteProduct(productId), () => router.push('/ui/login'));
    } else if (response.code === 401) {
      router.push('/ui/login');
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function onSearch() {

}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("products")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card">
      <div v-if="products.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t('code') }}</th>
            <th>{{ $t('name') }}</th>
            <th>{{ $t('description') }}</th>
            <th>{{ $t('barcode') }}</th>
            <th>{{ $t('unit.title') }}</th>
            <th>{{ $t('brand.title') }}</th>
            <th>{{ $t('productType.title') }}</th>
            <th>{{ $t('productClass.title') }}</th>
            <th>{{ $t('productGroup.title') }}</th>
            <th>{{ $t('productCategory.title') }}</th>
            <th>{{ $t('producer.title') }}</th>
            <th>{{ $t('country.title') }}</th>
            <th>{{ $t('enabled') }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="product in products" :key="product.id">
            <td>{{ product.id }}</td>
            <td>{{ product.code }}</td>
            <td>{{ product.name }}</td>
            <td>{{ product.description }}</td>
            <td>{{ product.defaultBarcode }}</td>
            <td v-if="product.defaultUnit">{{ product.defaultUnit.name }}</td>
            <td v-else></td>
            <td v-if="product.brand">{{ product.brand.code }}</td>
            <td v-else></td>
            <td v-if="product.productType">{{ product.productType.code }}</td>
            <td v-else></td>
            <td v-if="product.productClass">{{ product.productClass.code }}</td>
            <td v-else></td>
            <td v-if="product.productGroup">{{ product.productGroup.code }}</td>
            <td v-else></td>
            <td v-if="product.productCategory">{{ product.productCategory.code }}</td>
            <td v-else></td>
            <td v-if="product.producer">{{ product.producer.code }}</td>
            <td v-else></td>
            <td v-if="product.country">{{ product.country.code }}</td>
            <td v-else></td>
            <td><input v-model="product.enabled" name="enabled" type="checkbox"/></td>
            <td class="actions-col">
              <button class="btn btn-sm" @click='edit(product.id)'>{{ $t('edit') }}</button>
              <button class="btn btn-sm btn-danger" @click='deleteProduct(product.id)'>{{ $t('delete') }}</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div v-else class="empty">
        {{ $t('noRecords') }}
      </div>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>