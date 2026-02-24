<script setup>
import {computed, onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import {useRoute, useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";
import ProductLine from "@/components/document/ProductLine.vue";

const {t} = useI18n();
const router = useRouter();
const route = useRoute();
const info = ref("");
const infoType = ref('');
const transactionTypes = ref([]);
const products = ref([]);
const partners = ref([]);
const priceLists = ref([]);
const warehouses = ref([]);
const documentData = ref({});

const canEdit = computed(() => documentData.value?.documentStatus === "DRAFT");

const selectedType = computed(() =>
    transactionTypes.value.find(t => documentData.value.typeItem && t.id === Number(documentData.value.typeItem.id)) || null);

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
  if (documentData.value.productLines)
    return documentData.value.productLines.reduce((sum, l) => {
      const q = Number(l.quantity || 0);
      const p = Number(l.unitPrice || 0);
      const d = Number(l.discountRatio || 0);
      const net = q * p;
      return sum + (net - ((net * d) / 100));
    }, 0);
  else return 0;
});

function loadLookups() {
  return apiRequest("/api/lookups?types=transactionTypes,products,businessPartners,priceLists,warehouses", "GET")
      .then(response => {
        if (response.code === 200) {
          transactionTypes.value = response.data.transactionTypes || [];
          products.value = response.data.products || [];
          partners.value = response.data.businessPartners || [];
          priceLists.value = response.data.priceLists || [];
          warehouses.value = response.data.warehouses || [];
        } else if (response.code === 401) {
          refreshToken(() => loadLookups(), () => router.push("/ui/login"));
        } else {
          info.value = response.message;
          infoType.value = "error";
        }
      }).catch(error => {
        info.value = error;
        infoType.value = "error";
      });
}

function loadDocument() {
  apiRequest("/api/documents/" + route.params.id, "GET").then(response => {
    if (response.code === 200) {
      documentData.value = response.data.documentItem;
      documentData.value.productLines = response.data.productLines;
    } else if (response.code === 401) {
      refreshToken(() => loadDocument(), () => router.push("/ui/login"));
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
  if (!canEdit.value) return;
  documentData.value.productLines.push({product: null, quantity: 1, unitPrice: 0, discountRatio: 0});
}

function removeLine(index) {
  if (!canEdit.value || documentData.value.productLines.length === 1) return;
  documentData.value.productLines.splice(index, 1);
}

function validate() {
  if (!documentData.value.documentDate || !documentData.value.typeItem) {
    info.value = t("document.validationDateType");
    infoType.value = "error";
    return false;
  }
  if (documentData.value.productLines.some(l => !l.product || Number(l.quantity) <= 0)) {
    info.value = t("document.validationLines");
    infoType.value = "error";
    return false;
  }
  if (scope.value.commercial) {
    if (!documentData.value.partnerId || !documentData.value.priceListId) {
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
    documentDate: documentData.value.documentDate,
    typeId: Number(documentData.value.typeItem.id),
    description: documentData.value.description,
    partnerId: commercial ? Number(documentData.value.partnerId) : null,
    paymentMethod: commercial ? documentData.value.paymentMethod : null,
    priceListId: commercial ? Number(documentData.value.priceListId) : null,
    sourceWarehouseId: stock && documentData.value.sourceWarehouse ? Number(documentData.value.sourceWarehouse.id) : null,
    targetWarehouseId: stock && documentData.value.targetWarehouse ? Number(documentData.value.targetWarehouse.id) : null,
    lines: documentData.value.productLines.map(l => ({
      productId: Number(l.product.id),
      quantity: Number(l.quantity),
      unitPrice: Number(l.unitPrice),
      discountRatio: Number(l.discountRatio || 0)
    }))
  };
}

function saveDocumentAndMaybeSubmit(shouldSubmit) {
  if (!canEdit.value) {
    info.value = t("document.editLocked");
    infoType.value = "error";
    return;
  }
  if (!validate()) return;

  const payload = buildPayload();
  apiRequest("/api/documents/" + route.params.id, "PATCH", payload).then(response => {
    if (response.code === 200) {
      documentData.value = response.data ?? documentData.value;
      if (shouldSubmit) {
        apiRequest(`/api/documents/${route.params.id}/submit`, "POST").then(submitResponse => {
          if (submitResponse.code === 200) {
            router.push(`/ui/documents/process/${route.params.id}`);
          } else {
            info.value = submitResponse.message;
            infoType.value = "error";
          }
        }).catch(error => {
          info.value = error;
          infoType.value = "error";
        });
      } else {
        info.value = t("document.updated");
        infoType.value = "success";
      }
    } else if (response.code === 401) {
      refreshToken(() => saveDocumentAndMaybeSubmit(shouldSubmit), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

onMounted(() => {
  loadLookups();
  loadDocument();
});
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("document.edit")'/>

    <section class="card">
      <div v-if="documentData" class="doc-summary">
        <p><strong>{{ $t("documentNumber") }}:</strong> {{ documentData.documentNo }}</p>
        <p><strong>{{ $t("documentStatus") }}:</strong> {{ documentData.documentStatus }}</p>
        <p><strong>{{ $t("approvalStatus") }}:</strong> {{ documentData.approvalStatus }}</p>
      </div>
      <p v-if="documentData && !canEdit" class="notice">{{ $t("document.editLocked") }}</p>

      <form @submit.prevent="saveDocumentAndMaybeSubmit(false)">
        <label>{{ $t("documentDate") }}:
          <input v-model="documentData.documentDate" :disabled="!canEdit" name="documentDate" type="date"/>
        </label>
        <label>{{ $t("transactionType.title") }}:
          <select v-model="documentData.typeItem" :disabled="!canEdit">
            <option v-for="type in transactionTypes"
                    :key="type.id"
                    :value="type">
              {{ type.code }} - {{ type.name }}
            </option>
          </select>
        </label>
        <label>{{ $t("description") }}:
          <input v-model="documentData.description" :disabled="!canEdit" name="description" type="text"/>
        </label>

        <p>
          {{ $t("processScope") }}:
          <strong>{{ scope.commercial ? "Commercial" : "-" }}</strong>
          <strong>{{ scope.stock ? " Stock" : "" }}</strong>
          <strong>{{ scope.accounting ? " Accounting" : "" }}</strong>
          <strong>{{ scope.approval ? " Approval-sensitive" : "" }}</strong>
        </p>

        <div v-if="scope.commercial">
          <label>{{ $t("businessPartner.title") }}:
            <select v-model="documentData.partnerId" :disabled="!canEdit">
              <option :value="null">-</option>
              <option v-for="partner in partners" :key="partner.id" :value="partner">
                {{ partner.code }} - {{ partner.name }}
              </option>
            </select>
          </label>
          <label>{{ $t("priceList.title") }}:
            <select v-model="documentData.priceListId" :disabled="!canEdit">
              <option :value="null">-</option>
              <option v-for="priceList in priceLists"
                      :key="priceList.id"
                      :value="priceList">
                {{ priceList.code }} - {{ priceList.name }}
              </option>
            </select>
          </label>
          <label>{{ $t("paymentMethod") }}:
            <input v-model="documentData.paymentMethod" :disabled="!canEdit" name="paymentMethod" type="text"/>
          </label>
        </div>

        <div v-if="scope.stock">
          <label>{{ $t("sourceWarehouse") }}:
            <select v-model="documentData.sourceWarehouse" :disabled="!canEdit">
              <option :value="null">-</option>
              <option v-for="warehouse in warehouses" :key="warehouse.id" :value="warehouse">
                {{ warehouse.code }} - {{ warehouse.name }}
              </option>
            </select>
          </label>
          <label>{{ $t("targetWarehouse") }}:
            <select v-model="documentData.targetWarehouse" :disabled="!canEdit">
              <option :value="null">-</option>
              <option v-for="warehouse in warehouses" :key="warehouse.id" :value="warehouse">
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
            <ProductLine v-for="(line, index) in documentData.productLines"
                         :key="index"
                         :products="products"
                         :product="{id: line.productId, code: line.productCode, name: line.productName}"
                         :priceListId="documentData.priceListId"
                         :line="line"
                         :disabled="!canEdit"
                         :onRemove="() => removeLine(index)"></ProductLine>
            </tbody>
          </table>
        </div>

        <button class="btn btn-sm" type="button" :disabled="!canEdit" @click="addLine">{{ $t("addLine") }}</button>
        <p><strong>{{ $t("total") }}:</strong> {{ totalAmount.toFixed(2) }}</p>

        <button class="btn btn-primary" type="submit" :disabled="!canEdit">{{ $t("saveDraft") }}</button>
        <button class="btn" type="button" :disabled="!canEdit" @click="saveDocumentAndMaybeSubmit(true)">{{ $t("saveAndSubmit") }}</button>
      </form>
    </section>

    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

<style scoped>
.doc-summary {
  display: grid;
  gap: .35rem;
  margin-bottom: .75rem;
  color: var(--muted);
}

.notice {
  margin-bottom: .75rem;
  color: var(--muted);
  font-size: .9rem;
}

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
