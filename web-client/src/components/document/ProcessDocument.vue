<script setup>
import {computed, onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import {useRoute, useRouter} from "vue-router";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import InfoBar from "@/components/InfoBar.vue";

const route = useRoute();
const router = useRouter();
const info = ref("");
const documentData = ref(null);

const actions = computed(() => {
  const doc = documentData.value;
  if (!doc) return {};
  return {
    submit: doc.documentStatus === "DRAFT",
    approve: doc.approvalStatus === "PENDING",
    reject: doc.approvalStatus === "PENDING",
    post: doc.documentStatus === "IN_PROGRESS" && (doc.approvalStatus === "AUTO_APPROVED" || doc.approvalStatus === "APPROVED"),
    close: doc.documentStatus === "POSTED",
    cancel: doc.documentStatus !== "POSTED" && doc.documentStatus !== "CLOSED" && doc.documentStatus !== "CANCELLED"
  };
});

const timeline = computed(() => {
  const doc = documentData.value;
  if (!doc) return [];

  const events = [
    {key: "draft", done: !!doc.id, text: "Draft created"},
    {key: "submitted", done: doc.documentStatus === "IN_PROGRESS" || doc.documentStatus === "POSTED" || doc.documentStatus === "CLOSED", text: "Submitted"},
    {key: "approval", done: doc.approvalStatus === "APPROVED" || doc.approvalStatus === "AUTO_APPROVED", text: `Approval: ${doc.approvalStatus || "N/A"}`},
    {key: "posted", done: doc.documentStatus === "POSTED" || doc.documentStatus === "CLOSED", text: "Posted"},
    {key: "closed", done: doc.documentStatus === "CLOSED", text: "Closed"}
  ];

  if (doc.documentStatus === "CANCELLED") {
    events.push({key: "cancelled", done: true, text: "Cancelled"});
  }

  return events;
});

function init() {
  apiRequest("/api/documents/" + route.params.id, "GET").then(response => {
    if (response.code === 200) {
      documentData.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function runAction(action) {
  apiRequest(`/api/documents/${route.params.id}/${action}`, "POST").then(response => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => runAction(action), () => router.push("/ui/login"));
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
    <PageHeader :title='$t("document.process")'/>

    <section v-if="documentData" class="card">
      <p><strong>{{ $t("documentNumber") }}:</strong> {{ documentData.documentNo }}</p>
      <p><strong>{{ $t("documentDate") }}:</strong> {{ documentData.documentDate }}</p>
      <p><strong>{{ $t("transactionType.title") }}:</strong> {{ documentData.typeCode || documentData.typeName }}</p>
      <p><strong>{{ $t("documentStatus") }}:</strong> {{ documentData.documentStatus }}</p>
      <p><strong>{{ $t("approvalStatus") }}:</strong> {{ documentData.approvalStatus }}</p>
      <p><strong>{{ $t("description") }}:</strong> {{ documentData.description }}</p>

      <h4>{{ $t("processTimeline") }}</h4>
      <ul>
        <li v-for="event in timeline" :key="event.key">
          <strong>{{ event.done ? "[x]" : "[ ]" }}</strong> {{ event.text }}
        </li>
      </ul>

      <h4>{{ $t("allowedActions") }}</h4>
      <button v-if="actions.submit" class="btn btn-sm" @click="runAction('submit')">{{ $t("submit") }}</button>
      <button v-if="actions.approve" class="btn btn-sm" @click="runAction('approve')">{{ $t("approve") }}</button>
      <button v-if="actions.reject" class="btn btn-sm btn-danger" @click="runAction('reject')">{{ $t("reject") }}</button>
      <button v-if="actions.post" class="btn btn-sm" @click="runAction('post')">{{ $t("post") }}</button>
      <button v-if="actions.close" class="btn btn-sm" @click="runAction('close')">{{ $t("close") }}</button>
      <button v-if="actions.cancel" class="btn btn-sm btn-danger" @click="runAction('cancel')">{{ $t("cancel") }}</button>
    </section>

    <InfoBar :info="info"/>
  </MainLayout>
</template>

