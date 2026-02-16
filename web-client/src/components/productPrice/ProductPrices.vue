<script setup>
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {onMounted, ref} from "vue";
import New from "@/components/New.vue";
import {apiRequest, refreshToken} from "@/api.js";
import {useRoute, useRouter} from "vue-router";
import InfoBar from "@/components/InfoBar.vue";
import {round} from "@/tools.js";

const route = useRoute();
const router = useRouter();
const info = ref('');
const currentProduct = ref();
const productPrices = ref([]);
const priceLists = ref([]);
const selectedPriceList = ref();
const units = ref([]);
const selectedUnit = ref();
const assignedPriceKeys = ref([]);

function init() {
  apiRequest('/api/productPrices/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      currentProduct.value = response.data.product;
      productPrices.value = response.data.prices;
      productPrices.value.every(price => {
        assignedPriceKeys.value.push(price.unit.code + ':' + price.priceList.code);
      });

      apiRequest('/api/lookups?types=units,priceLists', 'GET').then(response => {
        if (response.code === 200) {
          units.value = response.data.units;
          if (units.value.length > 0)
            selectedUnit.value = units.value[0];

          priceLists.value = response.data.priceLists;
          if (priceLists.value.length > 0)
            selectedPriceList.value = priceLists.value[0];
        } else if (response.code === 401) {
          refreshToken(() => init(), () => router.push('/ui/login'));
        } else {
          info.value = response.message;
        }
      }).catch(error => {
        info.value = error;
      });
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
  const priceKey = selectedUnit.value.code + ':' + selectedPriceList.value.code;
  if (!assignedPriceKeys.value.includes(priceKey)) {
    const newProductPrice = {
      product: currentProduct.value,
      priceList: selectedPriceList.value,
      unit: selectedUnit.value,
      price: getBasePrice(selectedPriceList.value, selectedUnit.value) * selectedPriceList.value.factorToParent,
      factorToParent: selectedPriceList.value.factorToParent,
      roundLength: selectedPriceList.value.roundLength,
      discountRatioLimit: 0,
      fixedPrice: false,
      parent: selectedPriceList.value.parent
    };
    productPrices.value.push(newProductPrice);
    assignedPriceKeys.value.push(priceKey);
  }
}

function updatePrices() {
  const toPriceDto = (item, includeId = false) => {
    const dto = {
      productId: currentProduct.value.id,
      priceListId: item.priceList?.id ?? item.priceListId,
      unitId: item.unit?.id ?? item.unitId,
      price: item.price,
      factorToParent: item.factorToParent,
      fixedPrice: item.fixedPrice,
      roundLength: item.roundLength,
      discountRatioLimit: item.discountRatioLimit
    };
    if (includeId) {
      dto.id = item.id;
    }
    return dto;
  };

  const priceData = {
    productId: currentProduct.value.id,
    priceListToUpdate: productPrices.value.filter(item => item.id).map(item => toPriceDto(item, true)),
    priceListToInsert: productPrices.value.filter(item => !item.id).map(item => toPriceDto(item))
  }

  apiRequest('/api/productPrices', 'POST', priceData).then(response => {
    if (response.code === 200) {
      assignedPriceKeys.value = [];
      productPrices.value = [];
      init();
    } else if (response.code === 401) {
      refreshToken(() => updatePrices(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  })
}

function getBasePrice(priceList, unit) {
  let price = 0;
  if (priceList.parent) {
    price = productPrices.value.find(item => {
      return priceList.parent.id === item.priceList.id && unit.id === item.unit.id;
    }).price;
  } else if (priceList.price) {
    price = priceList.price;
  }
  return price;
}

const handlePriceChange = (priceData) => {
  priceData.fixedPrice = true;
  changeSubPrices(priceData);
}

const handleFixAndFactorChange = (priceData) => {
  if (priceData.parent && !priceData.fixedPrice) {
    priceData.price = round(priceData.factorToParent * getBasePrice(priceData, priceData.unit), priceData.roundLength);
    changeSubPrices(priceData);
  }
}

function changeSubPrices(priceData) {
  productPrices.value.map(item => {
    if (item.parent &&
        item.parent.id === priceData.priceList.id &&
        item.unit.id === priceData.unit.id &&
        !item.fixedPrice) {
      item.price = round(item.factorToParent * priceData.price, item.roundLength);
      changeSubPrices(item);
    }
  })
}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title="$t('productPrices')">
      <New :on-create="create"/>
    </PageHeader>

    <section class="card">
      <select id="selectedPriceList" v-model="selectedPriceList">
        <option v-for="priceList in priceLists"
                :key="priceList.id"
                :value="priceList">
          {{ priceList.name }}
        </option>
      </select>
      <select id="selectedUnit" v-model="selectedUnit">
        <option v-for="unit in units"
                :key="unit.id"
                :value="unit">
          {{ unit.name }}
        </option>
      </select>
      <div v-if="productPrices.length > 0" class="table-wrap">
        <div>
          <table class="data-table" role="table">
            <thead>
            <tr>
              <th>#</th>
              <th>{{ $t('priceList.title') }}</th>
              <th>{{ $t('unit.title') }}</th>
              <th>{{ $t('price') }}</th>
              <th>{{ $t('factorToParent') }}</th>
              <th>{{ $t('roundLength') }}</th>
              <th>{{ $t('parentPrice')}}</th>
              <th>{{ $t('fixedPrice') }}</th>
              <th>{{ $t('discountRatioLimit') }}</th>
              <th aria-hidden="true" class="actions-col"></th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="priceData in productPrices">
              <td class="mono">{{ priceData.id }}</td>
              <td>{{ priceData.priceList.code }}</td>
              <td>{{ priceData.unit.code }}</td>
              <td><input v-model.number="priceData.price" type="text" @change.prevent="handlePriceChange(priceData)"/>
              </td>
              <td><input v-model.number="priceData.factorToParent"
                         @change.prevent="handleFixAndFactorChange(priceData)"/></td>
              <td><input v-model.number="priceData.roundLength"
                         @change.prevent="handleFixAndFactorChange(priceData)"/></td>
              <td>{{ getBasePrice(priceData, priceData.unit)}}</td>
              <td><input v-model="priceData.fixedPrice" type="checkbox"
                         @change.prevent="handleFixAndFactorChange(priceData)"/></td>
              <td><input v-model.number="priceData.discountRatioLimit"/></td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-else class="empty">
        {{ $t('noRecords') }}
      </div>
      <button class="btn btn-primary" type="submit" @click="updatePrices">{{ $t('save') }}</button>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>

<style scoped>

</style>
