<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const router = useRouter();
const info = ref('');
const infoType = ref('');
const enabled = ref(true);
const brands = ref([]);
const selectedBrand = ref();
const productTypes = ref([]);
const selectedProductType = ref();
const productClasses = ref([]);
const selectedProductClass = ref();
const productGroups = ref([]);
const selectedProductGroup = ref();
const productCategories = ref([]);
const selectedProductCategory = ref();
const producers = ref([]);
const selectedProducer = ref();
const countries = ref([]);
const selectedCountry = ref();
const units = ref([]);
const selectedUnit = ref();

function init() {


  apiRequest('/api/lookups?types=brands,productTypes,productClasses,productGroups,units,countries,productCategories,producers',
      'GET').then(response => {
    if (response.code === 200) {
      brands.value = response.data.brands;
      if (brands.value.length > 0) {
        selectedBrand.value = brands.value[0];
      }

      productTypes.value = response.data.productTypes;
      if (productTypes.value.length > 0) {
        selectedProductType.value = productTypes.value[0];
      }

      productClasses.value = response.data.productClasses;
      if (productClasses.value.length > 0) {
        selectedProductClass.value = productClasses.value[0];
      }

      productGroups.value = response.data.productGroups;
      if (productGroups.value.length > 0) {
        selectedProductGroup.value = productGroups.value[0];
      }

      productCategories.value = response.data.productCategories;
      if (productCategories.value.length > 0) {
        selectedProductCategory.value = productCategories.value[0];
      }

      producers.value = response.data.producers;
      if (producers.value.length > 0) {
        selectedProducer.value = producers.value[0];
      }

      countries.value = response.data.countries;
      if (countries.value.length > 0) {
        selectedCountry.value = countries.value[0];
      }

      units.value = response.data.units;
      if (units.value.length > 0) {
        selectedUnit.value = units.value[0];
      }
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

function createProduct(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.enabled = enabled.value;
  data.brandId = selectedBrand.value?.id ?? null;
  data.productTypeId = selectedProductType.value?.id ?? null;
  data.productClassId = selectedProductClass.value?.id ?? null;
  data.productGroupId = selectedProductGroup.value?.id ?? null;
  data.productCategoryId = selectedProductCategory.value?.id ?? null;
  data.producerId = selectedProducer.value?.id ?? null;
  data.countryId = selectedCountry.value?.id ?? null;
  data.defaultUnitId = selectedUnit.value?.id ?? null;
  apiRequest('/api/products', 'POST', data).then(response => {
    if (response.code === 200) {
      router.push('/ui/products');
    } else if (response.code === 401) {
      refreshToken(() => createProduct(event), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("product.new")'/>

    <section class="card">
      <form @submit.prevent="createProduct">
        <label>{{ $t('code') }}: <input name="code" type="text"/></label>
        <label>{{ $t('name') }}: <input autocomplete="false" name="name" type="text"/></label>
        <label>{{ $t('description') }}: <input name="description" type="text"/></label>
        <label>{{ $t('barcode') }}: <input name="defaultBarcode" type="text"/></label>
        <label>{{ $t('enabled') }}: <input v-model="enabled" name="enabled" type="checkbox"></label>

        <label>{{ $t('brand.title') }}:
          <select id="brand" v-model="selectedBrand">
            <option
                v-for="brand in brands"
                :key="brand.id"
                :value="brand">
              {{ brand.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('productType.title') }}:
          <select id="productType" v-model="selectedProductType">
            <option
                v-for="productType in productTypes"
                :key="productType.id"
                :value="productType">
              {{ productType.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('productClass.title') }}:
          <select id="productClass" v-model="selectedProductClass">
            <option
                v-for="productClass in productClasses"
                :key="productClass.id"
                :value="productClass">
              {{ productClass.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('productGroup.title') }}:
          <select id="productGroup" v-model="selectedProductGroup">
            <option
                v-for="productGroup in productGroups"
                :key="productGroup.id"
                :value="productGroup">
              {{ productGroup.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('productCategory.title') }}:
          <select id="productCategory" v-model="selectedProductCategory">
            <option
                v-for="productCategory in productCategories"
                :key="productCategory.id"
                :value="productCategory">
              {{ productCategory.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('producer.title') }}:
          <select id="producer" v-model="selectedProducer">
            <option
                v-for="producer in producers"
                :key="producer.id"
                :value="producer">
              {{ producer.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('country.title') }}:
          <select id="country" v-model="selectedCountry">
            <option
                v-for="country in countries"
                :key="country.id"
                :value="country">
              {{ country.name }}
            </option>
          </select>
        </label>

        <label>{{ $t('unit.title') }}:
          <select id="unit" v-model="selectedUnit">
            <option
                v-for="unit in units"
                :key="unit.id"
                :value="unit">
              {{ unit.name }}
            </option>
          </select>
        </label>
        <button class="btn btn-primary" type="submit">{{ $t('create') }}</button>
      </form>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

