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
const product = ref({});
const brands = ref([]);
const productTypes = ref([]);
const productClasses = ref([]);
const productGroups = ref([]);
const productCategories = ref([]);
const producers = ref([]);
const countries = ref([]);
const units = ref([]);
const barcodes = ref([]);

function init() {
  apiRequest('/api/products/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      product.value = response.data;

      apiRequest('/api/productBarcodes/' + product.value.id, 'GET').then((response) => {
        if (response.code === 200) {
          barcodes.value = response.data.barcodes;
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

  apiRequest('/api/lookups?types=brands,productTypes,productClasses,productGroups,units,countries,productCategories,producers',
      'GET').then(response => {
    if (response.code === 200) {
      brands.value = response.data.brands;
      productTypes.value = response.data.productTypes;
      productClasses.value = response.data.productClasses;
      productGroups.value = response.data.productGroups;
      productCategories.value = response.data.productCategories;
      producers.value = response.data.producers;
      countries.value = response.data.countries;
      units.value = response.data.units;
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

function updateProduct() {
  product.value.brandId = product.value.brand?.id ?? null;
  product.value.productTypeId = product.value.productType?.id ?? null;
  product.value.productClassId = product.value.productClass?.id ?? null;
  product.value.productGroupId = product.value.productGroup?.id ?? null;
  product.value.productCategoryId = product.value.productCategory?.id ?? null;
  product.value.producerId = product.value.producer?.id ?? null;
  product.value.countryId = product.value.country?.id ?? null;
  product.value.defaultUnitId = product.value.defaultUnit?.id ?? null;
  apiRequest('/api/products/' + route.params.id, 'PATCH', product.value).then((response) => {
    if (response.code === 200) {
      info.value = t('product.updated');
      infoType.value = "success";
    } else if (response.code === 401) {
      refreshToken(() => updateProduct(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

function productPrices(productId) {
  router.push('/ui/products/' + productId + '/prices');
}

function productBarcodes(productId) {
  router.push('/ui/products/' + productId + '/barcodes');
}

function productUnits(productId) {
  router.push('/ui/products/' + productId + '/units');
}

function productWarehouses(productId) {
  router.push('/ui/products/' + productId + '/warehouses');
}

function stockBalance(productId) {
  router.push('/ui/products/' + productId + '/stock');
}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("product.edit")'/>

    <section class="card">
      <form @submit.prevent='updateProduct'>
        <label>
          <input v-model='product.id' hidden='hidden' name='id' type='text'/>
        </label>
        <label>{{ $t('code') }}: <input v-model='product.code' name='code' type='text'/></label>
        <label>{{ $t('name') }}: <input v-model='product.name' autocomplete='false' name='name'
                                        type='text'/></label>
        <label>{{ $t('description') }}: <input v-model='product.description' name='description'/></label>
        <label>{{ $t('barcode') }}:
          <input v-model='product.defaultBarcode' name='defaultBarcode'/>
          <select v-model="product.defaultBarcode">
            <option
                v-for="barcodeData in barcodes"
                :key="barcodeData.id"
                :value="barcodeData.barcode">
              {{ barcodeData.barcode }}
            </option>
          </select>
        </label>
        <label>{{ $t('enabled') }}: <input v-model="product.enabled" name="enabled" type="checkbox"></label>

        <label>{{ $t('brand.title') }}:
          <select id="brand" v-model="product.brand">
            <option
                v-for="brand in brands"
                :key="brand.id"
                :value="brand">
              {{ brand.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('productType.title') }}:
          <select id="productType" v-model="product.productType">
            <option
                v-for="productType in productTypes"
                :key="productType.id"
                :value="productType">
              {{ productType.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('productClass.title') }}:
          <select id="productClass" v-model="product.productClass">
            <option
                v-for="productClass in productClasses"
                :key="productClass.id"
                :value="productClass">
              {{ productClass.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('productGroup.title') }}:
          <select id="productGroup" v-model="product.productGroup">
            <option
                v-for="productGroup in productGroups"
                :key="productGroup.id"
                :value="productGroup">
              {{ productGroup.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('productCategory.title') }}:
          <select id="productCategory" v-model="product.productCategory">
            <option
                v-for="productCategory in productCategories"
                :key="productCategory.id"
                :value="productCategory">
              {{ productCategory.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('producer.title') }}:
          <select id="producer" v-model="product.producer">
            <option
                v-for="producer in producers"
                :key="producer.id"
                :value="producer">
              {{ producer.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('country.title') }}:
          <select id="country" v-model="product.country">
            <option
                v-for="country in countries"
                :key="country.id"
                :value="country">
              {{ country.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('unit.title') }}:
          <select id="unit" v-model="product.defaultUnit">
            <option
                v-for="unit in units"
                :key="unit.id"
                :value="unit">
              {{ unit.name }}
            </option>
          </select>
        </label>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
      <button class="btn btn-sm" @click='productPrices(product.id)'>{{ $t('productPrices') }}</button>
      <button class="btn btn-sm" @click='productBarcodes(product.id)'>{{ $t('productBarcodes') }}</button>
      <button class="btn btn-sm" @click='productUnits(product.id)'>{{ $t('productUnits') }}</button>
      <button class="btn btn-sm" @click='productWarehouses(product.id)'>{{ $t('productWarehouses') }}</button>
      <button class="btn btn-sm" @click='stockBalance(product.id)'>{{ $t('stockBalance') }}</button>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

