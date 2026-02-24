<script setup>
import {computed, onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import {useRouter} from "vue-router";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";

const info = ref("");
const infoType = ref('');
const documents = ref([]);
const searchQuery = ref("");
const router = useRouter();

const filteredDocuments = computed(() => {
  const q = searchQuery.value.trim().toLowerCase();
  if (!q) return documents.value;
  return documents.value.filter(d => {
    const number = String(d.documentNo || "").toLowerCase();
    const status = String(d.documentStatus || "").toLowerCase();
    const approval = String(d.approvalStatus || "").toLowerCase();
    const type = String(d.typeCode || d.typeName || "").toLowerCase();
    return number.includes(q) || status.includes(q) || approval.includes(q) || type.includes(q);
  });
});

function init() {
  apiRequest("/api/documents", "GET").then(response => {
    if (response.code === 200) {
      documents.value = response.data || [];
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

function create() {
  router.push("/ui/documents/create");
}

function openProcess(documentId) {
  router.push("/ui/documents/process/" + documentId);
}

function editDocument(documentId) {
  router.push("/ui/documents/edit/" + documentId);
}

function canSubmit(doc) {
  return doc.documentStatus === "DRAFT";
}

function canEdit(doc) {
  return doc.documentStatus === "DRAFT";
}

function canApprove(doc) {
  return doc.approvalStatus === "PENDING";
}

function canReject(doc) {
  return doc.approvalStatus === "PENDING";
}

function canPost(doc) {
  if (doc.documentStatus !== "IN_PROGRESS") return false;
  return doc.approvalStatus === "AUTO_APPROVED" || doc.approvalStatus === "APPROVED";
}

function canClose(doc) {
  return doc.documentStatus === "POSTED";
}

function canCancel(doc) {
  return doc.documentStatus !== "POSTED" && doc.documentStatus !== "CLOSED" && doc.documentStatus !== "CANCELLED";
}

function runAction(documentId, action) {
  apiRequest(`/api/documents/${documentId}/${action}`, "POST").then(response => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => runAction(documentId, action), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

function onSearch() {
}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("documents")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card list-card">
      <div v-if="filteredDocuments.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t("documentNumber") }}</th>
            <th>{{ $t("documentDate") }}</th>
            <th>{{ $t("transactionType.title") }}</th>
            <th>{{ $t("documentStatus") }}</th>
            <th>{{ $t("approvalStatus") }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="document in filteredDocuments" :key="document.id">
            <td class="mono">{{ document.id }}</td>
            <td>{{ document.documentNo }}</td>
            <td>{{ document.documentDate }}</td>
            <td>{{ document.typeCode || document.typeName }}</td>
            <td>{{ document.documentStatus }}</td>
            <td>{{ document.approvalStatus }}</td>
            <td class="actions-col">
              <button class="btn btn-sm" @click="openProcess(document.id)">{{ $t("process") }}</button>
              <button v-if="canEdit(document)" class="btn btn-sm" @click="editDocument(document.id)">{{ $t("edit") }}</button>
              <button v-if="canSubmit(document)" class="btn btn-sm" @click="runAction(document.id, 'submit')">{{ $t("submit") }}</button>
              <button v-if="canApprove(document)" class="btn btn-sm" @click="runAction(document.id, 'approve')">{{ $t("approve") }}</button>
              <button v-if="canReject(document)" class="btn btn-sm btn-danger" @click="runAction(document.id, 'reject')">{{ $t("reject") }}</button>
              <button v-if="canPost(document)" class="btn btn-sm" @click="runAction(document.id, 'post')">{{ $t("post") }}</button>
              <button v-if="canClose(document)" class="btn btn-sm" @click="runAction(document.id, 'close')">{{ $t("close") }}</button>
              <button v-if="canCancel(document)" class="btn btn-sm btn-danger" @click="runAction(document.id, 'cancel')">{{ $t("cancel") }}</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div v-else class="empty">
        {{ $t("noRecords") }}
      </div>
    </section>

    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

