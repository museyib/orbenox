<script setup>
import {computed, onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import {useRouter} from "vue-router";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";

const router = useRouter();
const info = ref("");
const transactionTypes = ref([]);
const products = ref([]);
const partners = ref([]);
const priceLists = ref([]);
const warehouses = ref([]);

const form = ref({
  number: "",
  documentDate: new Date().toISOString().slice(0, 10),
  typeId: null,
  description: "",
  partnerId: null,
  paymentMethod: "",
  priceListId: null,
  sourceWarehouseId: null,
  targetWarehouseId: null,
  lines: [{productId: null, quantity: 1, unitPrice: 0, discountRatio: 0}]
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
    }
  }).catch(error => {
    info.value = error;
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
  if (!form.value.number || !form.value.documentDate || !form.value.typeId) {
    info.value = "Document number, date and transaction type are required";
    return false;
  }
  if (form.value.lines.some(l => !l.productId || Number(l.quantity) <= 0)) {
    info.value = "Each line requires product and quantity > 0";
    return false;
  }
  if (scope.value.commercial) {
    if (!form.value.partnerId || !form.value.priceListId) {
      info.value = "Commercial transaction requires partner and price list";
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
      productId: Number(l.productId),
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
    }
  }).catch(error => {
    info.value = error;
  });
}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("document.new")'/>

    <section class="card">
      <form @submit.prevent="createDocumentAndMaybeSubmit(false)">
        <label>{{ $t("documentNumber") }}: <input v-model="form.number" name="number" type="text"/></label><br/>
        <label>{{ $t("documentDate") }}: <input v-model="form.documentDate" name="documentDate" type="date"/></label><br/>
        <label>{{ $t("transactionType.title") }}:
          <select v-model="form.typeId">
            <option v-for="type in transactionTypes" :key="type.id" :value="type.id">
              {{ type.code }} - {{ type.name }}
            </option>
          </select>
        </label><br/>
        <label>{{ $t("description") }}: <input v-model="form.description" name="description" type="text"/></label><br/>

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
          </label><br/>
          <label>{{ $t("priceList.title") }}:
            <select v-model="form.priceListId">
              <option :value="null">-</option>
              <option v-for="priceList in priceLists" :key="priceList.id" :value="priceList.id">
                {{ priceList.code }} - {{ priceList.name }}
              </option>
            </select>
          </label><br/>
          <label>{{ $t("paymentMethod") }}: <input v-model="form.paymentMethod" name="paymentMethod" type="text"/></label><br/>
        </div>

        <div v-if="scope.stock">
          <label>{{ $t("sourceWarehouse") }}:
            <select v-model="form.sourceWarehouseId">
              <option :value="null">-</option>
              <option v-for="warehouse in warehouses" :key="warehouse.id" :value="warehouse.id">
                {{ warehouse.code }} - {{ warehouse.name }}
              </option>
            </select>
          </label><br/>
          <label>{{ $t("targetWarehouse") }}:
            <select v-model="form.targetWarehouseId">
              <option :value="null">-</option>
              <option v-for="warehouse in warehouses" :key="warehouse.id" :value="warehouse.id">
                {{ warehouse.code }} - {{ warehouse.name }}
              </option>
            </select>
          </label><br/>
        </div>

        <h4>{{ $t("lines") }}</h4>
        <div v-for="(line, index) in form.lines" :key="index" style="margin-bottom: 12px; padding: 8px; border: 1px solid #ddd;">
          <label>{{ $t("product.title") }}:
            <select v-model="line.productId">
              <option :value="null">-</option>
              <option v-for="product in products" :key="product.id" :value="product.id">
                {{ product.code }} - {{ product.name }}
              </option>
            </select>
          </label><br/>
          <label>{{ $t("quantity") }}: <input v-model="line.quantity" min="0" step="0.0001" type="number"/></label><br/>
          <label>{{ $t("price") }}: <input v-model="line.unitPrice" min="0" step="0.0001" type="number"/></label><br/>
          <label>{{ $t("discountRatio") }}: <input v-model="line.discountRatio" min="0" step="0.01" type="number"/></label><br/>
          <button class="btn btn-sm btn-danger" type="button" @click="removeLine(index)">{{ $t("removeLine") }}</button>
        </div>

        <button class="btn btn-sm" type="button" @click="addLine">{{ $t("addLine") }}</button>
        <p><strong>{{ $t("total") }}:</strong> {{ totalAmount.toFixed(2) }}</p>

        <button class="btn btn-primary" type="submit">{{ $t("saveDraft") }}</button>
        <button class="btn" type="button" @click="createDocumentAndMaybeSubmit(true)">{{ $t("saveAndSubmit") }}</button>
      </form>
    </section>

    <InfoBar :info="info"/>
  </MainLayout>
</template>

