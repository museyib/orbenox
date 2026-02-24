<script setup>
import {computed, onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import {useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";
import ProductLine from "@/components/document/ProductLine.vue";

const {t} = useI18n();
const router = useRouter();
const info = ref("");
const infoType = ref('');
const transactionTypes = ref([]);
const products = ref([]);
const partners = ref([]);
const priceLists = ref([]);
const warehouses = ref([]);

const form = ref({
  documentDate: new Date().toISOString().slice(0, 10),
  typeId: null,
  description: "",
  partnerId: null,
  paymentMethod: "",
  priceListId: null,
  sourceWarehouseId: null,
  targetWarehouseId: null,
  lines: [{product: null, quantity: 1, unitPrice: 0, discountRatio: 0}]
});

const selectedType = computed(() => transactionTypes.value.find(t => t.id === Number(form.value.typeId)) || null);

const scope = computed(() => {
  const code = (selectedType.value?.code || "").toUpperCase();
  const isSales = code.includes("SALES");
  const isTransfer = code.includes("TRANSFER");
  const isStockOnly = code.includes("APPROVE") || code.includes("STOCK");

  return {
    commercial: isSales,
    stock: isSales || isTransfer || isStockOnly,
    accounting: isSales,
    approval: isSales
  };
});

const totalAmount = computed(() => {
  return form.value.lines.reduce((sum, l) => {
    const q = Number(l.quantity || 0);
    const p = Number(l.unitPrice || 0);
    const d = Number(l.discountRatio || 0);
    const net = q * p;
    return sum + (net - ((net * d) / 100));
  }, 0);
});

function init() {
  apiRequest("/api/lookups?types=transactionTypes,products,businessPartners,priceLists,warehouses", "GET").then(response => {
    if (response.code === 200) {
      transactionTypes.value = response.data.transactionTypes || [];
      products.value = response.data.products || [];
      partners.value = response.data.businessPartners || [];
      priceLists.value = response.data.priceLists || [];
      warehouses.value = response.data.warehouses || [];
      if (!form.value.typeId && transactionTypes.value.length > 0) {
        form.value.typeId = transactionTypes.value[0].id;
      }
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push("/ui/login"));
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
  form.value.lines.push({productId: null, quantity: 1, unitPrice: 0, discountRatio: 0});
}

function removeLine(index) {
  if (form.value.lines.length === 1) return;
  form.value.lines.splice(index, 1);
}

function validate() {
  if (!form.value.documentDate || !form.value.typeId) {
    info.value = t("document.validationDateType");
    infoType.value = "error";
    return false;
  }
  if (form.value.lines.some(l => !l.product || Number(l.quantity) <= 0)) {
    info.value = t("document.validationLines");
    infoType.value = "error";
    return false;
  }
  if (scope.value.commercial) {
    if (!form.value.partnerId || !form.value.priceListId) {
      info.value = t("document.validationCommercial");
      infoType.value = "error";
      return false;
    }
  }
  return true;
}

function buildPayload() {
  const commercial = scope.value.commercial;
  const stock = scope.value.stock;

  return {
    number: form.value.number,
    documentDate: form.value.documentDate,
    typeId: Number(form.value.typeId),
    description: form.value.description,
    partnerId: commercial ? Number(form.value.partnerId) : null,
    paymentMethod: commercial ? form.value.paymentMethod : null,
    priceListId: commercial ? Number(form.value.priceListId) : null,
    sourceWarehouseId: stock && form.value.sourceWarehouseId ? Number(form.value.sourceWarehouseId) : null,
    targetWarehouseId: stock && form.value.targetWarehouseId ? Number(form.value.targetWarehouseId) : null,
    lines: form.value.lines.map(l => ({
      productId: Number(l.product.id),
      quantity: Number(l.quantity),
      unitPrice: Number(l.unitPrice),
      discountRatio: Number(l.discountRatio || 0)
    }))
  };
}

function createDocumentAndMaybeSubmit(shouldSubmit) {
  if (!validate()) return;

  const payload = buildPayload();
  apiRequest("/api/documents", "POST", payload).then(response => {
    if (response.code === 200) {
      const documentId = response.data?.id;
      if (shouldSubmit && documentId) {
        apiRequest(`/api/documents/${documentId}/submit`, "POST").then(submitResponse => {
          if (submitResponse.code === 200) {
            router.push(`/ui/documents/process/${documentId}`);
          } else {
            info.value = submitResponse.message;
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
      refreshToken(() => createDocumentAndMaybeSubmit(shouldSubmit), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("document.new")'/>

    <section class="card">
      <form @submit.prevent="createDocumentAndMaybeSubmit(false)">
        <label>{{ $t("documentDate") }}: <input v-model="form.documentDate" name="documentDate" type="date"/></label>
        <label>{{ $t("transactionType.title") }}:
          <select v-model="form.typeId">
            <option v-for="type in transactionTypes" :key="type.id" :value="type.id">
              {{ type.code }} - {{ type.name }}
            </option>
          </select>
        </label>
        <label>{{ $t("description") }}: <input v-model="form.description" name="description" type="text"/></label>

        <p>
          {{ $t("processScope") }}:
          <strong>{{ scope.commercial ? "Commercial" : "-" }}</strong>
          <strong>{{ scope.stock ? " Stock" : "" }}</strong>
          <strong>{{ scope.accounting ? " Accounting" : "" }}</strong>
          <strong>{{ scope.approval ? " Approval-sensitive" : "" }}</strong>
        </p>

        <div v-if="scope.commercial">
          <label>{{ $t("businessPartner.title") }}:
            <select v-model="form.partnerId">
              <option :value="null">-</option>
              <option v-for="partner in partners" :key="partner.id" :value="partner.id">
                {{ partner.code }} - {{ partner.name }}
              </option>
            </select>
          </label>
          <label>{{ $t("priceList.title") }}:
            <select v-model="form.priceListId">
              <option :value="null">-</option>
              <option v-for="priceList in priceLists"
                      :key="priceList.id"
                      :value="priceList.id">
                {{ priceList.code }} - {{ priceList.name }}
              </option>
            </select>
          </label>
          <label>{{ $t("paymentMethod") }}: <input v-model="form.paymentMethod" name="paymentMethod" type="text"/></label>
        </div>

        <div v-if="scope.stock">
          <label>{{ $t("sourceWarehouse") }}:
            <select v-model="form.sourceWarehouseId">
              <option :value="null">-</option>
              <option v-for="warehouse in warehouses" :key="warehouse.id" :value="warehouse.id">
                {{ warehouse.code }} - {{ warehouse.name }}
              </option>
            </select>
          </label>
          <label>{{ $t("targetWarehouse") }}:
            <select v-model="form.targetWarehouseId">
              <option :value="null">-</option>
              <option v-for="warehouse in warehouses" :key="warehouse.id" :value="warehouse.id">
                {{ warehouse.code }} - {{ warehouse.name }}
              </option>
            </select>
          </label>
        </div>

        <h4>{{ $t("lines") }}</h4>
        <div class="line-table">
          <table class="lines-table" role="table">
            <thead>
            <tr>
              <th>{{ $t("product.title") }}</th>
              <th class="num-col">{{ $t("quantity") }}</th>
              <th class="num-col">{{ $t("price") }}</th>
              <th class="num-col">{{ $t("discountRatio") }}</th>
              <th class="actions-col"></th>
            </tr>
            </thead>
            <tbody>
            <ProductLine v-for="(line, index) in form.lines"
                         :key="index"
                         :products="products"
                         :product="line.product"
                         :priceListId="form.priceListId"
                         :line="line"
                         :onRemove="() => removeLine(index)"></ProductLine>
            </tbody>
          </table>
        </div>

        <button class="btn btn-sm" type="button" @click="addLine">{{ $t("addLine") }}</button>
        <p><strong>{{ $t("total") }}:</strong> {{ totalAmount.toFixed(2) }}</p>

        <button class="btn btn-primary" type="submit">{{ $t("saveDraft") }}</button>
        <button class="btn" type="button" @click="createDocumentAndMaybeSubmit(true)">{{ $t("saveAndSubmit") }}</button>
      </form>
    </section>

    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

<style scoped>
.line-table {
  overflow-x: auto;
}

.lines-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 640px;
}

.lines-table th {
  text-align: left;
  font-weight: 600;
  font-size: .84rem;
  color: var(--muted);
  padding: .4rem .35rem;
  border-bottom: 1px solid var(--border);
}

.lines-table td {
  padding: .35rem;
  border-bottom: 1px dashed var(--glass);
  vertical-align: middle;
}

.num-col {
  text-align: right;
}
</style>

