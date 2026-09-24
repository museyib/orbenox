<script setup>
import {computed, onMounted, ref} from "vue";
import {apiRequest, createIdempotencyKey, refreshToken} from "@/api.js";
import {useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";
import ProductLine from "@/components/document/ProductLine.vue";
import {documentEndpoint} from "@/components/document/documentApi.js";

const props = defineProps({
  typeCode: {type: String, required: true}
});

const {t} = useI18n();
const router = useRouter();
const info = ref("");
const infoType = ref("");
const transactionType = ref(null);
const products = ref([]);
const partners = ref([]);
const priceLists = ref([]);
const warehouses = ref([]);
const isSalesOrder = computed(() => props.typeCode === "SALES_ORDER");
const endpoint = computed(() => documentEndpoint(props.typeCode));
const form = ref({
  documentDate: new Date().toISOString().slice(0, 10),
  description: "",
  partnerId: null,
  paymentMethod: "",
  priceListId: null,
  warehouseId: null,
  lines: []
});

const totalAmount = computed(() => form.value.lines.reduce((sum, line) => {
  const net = Number(line.quantity || 0) * Number(line.unitPrice || 0);
  return sum + net - net * Number(line.discountRatio || 0) / 100;
}, 0));

function init() {
  const types = isSalesOrder.value
      ? "transactionTypes,products,businessPartners,priceLists,warehouses"
      : "transactionTypes,products,warehouses";
  apiRequest(`/api/lookups?types=${types}`, "GET").then(response => {
    if (response.code === 200) {
      const data = response.data || {};
      transactionType.value = (data.transactionTypes || []).find(type => type.code === props.typeCode) || null;
      products.value = data.products || [];
      partners.value = data.businessPartners || [];
      priceLists.value = data.priceLists || [];
      warehouses.value = data.warehouses || [];
      if (!transactionType.value) {
        info.value = t("document.validationDateType");
        infoType.value = "error";
      }
    } else if (response.code === 401) {
      refreshToken(init, () => router.push("/ui/login"));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

function addLine() {
  form.value.lines.push({product: null, quantity: 1, unitPrice: 0, discountRatio: 0, autoOpenPicker: true});
}

function removeEmptyLines() {
  form.value.lines = form.value.lines.filter(line => line.product);
}

function validate() {
  if (!form.value.documentDate || !transactionType.value) {
    info.value = t("document.validationDateType");
    infoType.value = "error";
    return false;
  }
  if (form.value.lines.length === 0 || form.value.lines.some(line => !line.product?.id || Number(line.quantity) <= 0)) {
    info.value = t("document.validationLines");
    infoType.value = "error";
    return false;
  }
  if (isSalesOrder.value && (!form.value.partnerId || !form.value.priceListId || !form.value.warehouseId)) {
    info.value = t("document.validationCommercial");
    infoType.value = "error";
    return false;
  }
  if (!isSalesOrder.value && !form.value.warehouseId) {
    info.value = t("document.validationWarehouse");
    infoType.value = "error";
    return false;
  }
  return true;
}

function buildPayload() {
  const payload = {
    documentDate: form.value.documentDate,
    description: form.value.description,
    typeId: Number(transactionType.value.id),
    lines: form.value.lines.map(line => ({
      productId: Number(line.product.id),
      quantity: Number(line.quantity),
      unitPrice: isSalesOrder.value ? Number(line.unitPrice) : 0,
      discountRatio: isSalesOrder.value ? Number(line.discountRatio || 0) : 0
    }))
  };

  if (isSalesOrder.value) {
    payload.partnerId = Number(form.value.partnerId);
    payload.paymentMethod = form.value.paymentMethod;
    payload.priceListId = Number(form.value.priceListId);
    payload.sourceWarehouseId = Number(form.value.warehouseId);
  } else {
    payload.targetWarehouseId = Number(form.value.warehouseId);
  }
  return payload;
}

function createDocumentAndMaybeSubmit(shouldSubmit, idempotencyKey = null) {
  if (!validate()) return;
  idempotencyKey = idempotencyKey || createIdempotencyKey();
  apiRequest(endpoint.value, "POST", buildPayload(), idempotencyKey).then(response => {
    if (response.code === 200) {
      const documentId = response.data?.id;
      if (shouldSubmit && documentId) {
        apiRequest(`${endpoint.value}/${documentId}/submit`, "POST").then(submitResponse => {
          if (submitResponse.code === 200) {
            router.push(`/ui/documents/process/${documentId}`);
          } else {
            info.value = submitResponse.message;
            infoType.value = "error";
          }
        }).catch(error => {
          info.value = error;
          infoType.value = "error";
        });
      } else if (documentId) {
        router.push(`/ui/documents/process/${documentId}`);
      } else {
        router.push("/ui/documents");
      }
    } else if (response.code === 401) {
      refreshToken(() => createDocumentAndMaybeSubmit(shouldSubmit, idempotencyKey), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

onMounted(init);
</script>

<template>
  <MainLayout>
    <PageHeader :title='isSalesOrder ? $t("document.salesOrder.new") : $t("document.productApprove.new")'/>
    <section class="card">
      <form @submit.prevent="createDocumentAndMaybeSubmit(false)">
        <label>{{ $t("documentDate") }}: <input v-model="form.documentDate" name="documentDate" type="date"/></label>
        <label>{{ $t("description") }}: <input v-model="form.description" name="description" type="text"/></label>

        <template v-if="isSalesOrder">
          <label>{{ $t("businessPartner.title") }}:
            <select v-model="form.partnerId">
              <option :value="null">-</option>
              <option v-for="partner in partners" :key="partner.id" :value="partner.id">{{ partner.code }} - {{ partner.name }}</option>
            </select>
          </label>
          <label>{{ $t("priceList.title") }}:
            <select v-model="form.priceListId">
              <option :value="null">-</option>
              <option v-for="priceList in priceLists" :key="priceList.id" :value="priceList.id">{{ priceList.code }} - {{ priceList.name }}</option>
            </select>
          </label>
          <label>{{ $t("paymentMethod") }}: <input v-model="form.paymentMethod" name="paymentMethod" type="text"/></label>
          <label>{{ $t("sourceWarehouse") }}:
            <select v-model="form.warehouseId" name="sourceWarehouse">
              <option :value="null">-</option>
              <option v-for="warehouse in warehouses" :key="warehouse.id" :value="warehouse.id">{{ warehouse.code }} - {{ warehouse.name }}</option>
            </select>
          </label>
        </template>
        <label v-else>{{ $t("targetWarehouse") }}:
          <select v-model="form.warehouseId" name="targetWarehouse">
            <option :value="null">-</option>
            <option v-for="warehouse in warehouses" :key="warehouse.id" :value="warehouse.id">{{ warehouse.code }} - {{ warehouse.name }}</option>
          </select>
        </label>

        <h4>{{ $t("lines") }}</h4>
        <div class="line-table">
          <table class="lines-table" role="table">
            <thead>
            <tr>
              <th>{{ $t("code") }}</th>
              <th>{{ $t("name") }}</th>
              <th class="num-col">{{ $t("quantity") }}</th>
              <th v-if="isSalesOrder" class="num-col">{{ $t("price") }}</th>
              <th v-if="isSalesOrder" class="num-col">{{ $t("discountRatio") }}</th>
              <th class="actions-col"></th>
            </tr>
            </thead>
            <tbody>
            <ProductLine v-for="(line, index) in form.lines" :key="index" :products="products"
                         :line="line" :price-list-id="isSalesOrder ? Number(form.priceListId) : null"
                         :show-pricing="isSalesOrder" :on-remove="() => form.lines.splice(index, 1)"
                         :on-close="removeEmptyLines"/>
            </tbody>
          </table>
        </div>
        <button class="btn btn-sm" type="button" @click="addLine">{{ $t("addLine") }}</button>
        <p v-if="isSalesOrder"><strong>{{ $t("total") }}:</strong> {{ totalAmount.toFixed(2) }}</p>
        <button class="btn btn-primary" type="submit">{{ $t("saveDraft") }}</button>
        <button class="btn" type="button" @click="createDocumentAndMaybeSubmit(true)">{{ $t("saveAndSubmit") }}</button>
      </form>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

<style scoped>
.line-table { overflow-x: auto; }
.lines-table { width: 100%; border-collapse: collapse; min-width: 520px; }
.lines-table th { text-align: left; font-weight: 600; font-size: .84rem; color: var(--muted); padding: .4rem .35rem; border-bottom: 1px solid var(--border); }
.lines-table td { padding: .35rem; border-bottom: 1px dashed var(--glass); vertical-align: middle; }
.num-col { text-align: right; }
</style>
