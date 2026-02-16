<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import InfoBar from "@/components/InfoBar.vue";
import Toolbar from "@/components/Toolbar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useRouter} from "vue-router";

const info = ref("");
const postingRules = ref([]);
const searchQuery = ref("");
const router = useRouter();

function init() {
  apiRequest("/api/postingRules", "GET").then(response => {
    if (response.code === 200) {
      postingRules.value = response.data;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function create() {
  router.push("/ui/postingRules/create");
}

function edit(postingRuleId) {
  router.push("/ui/postingRules/edit/" + postingRuleId);
}

function deletePostingRule(postingRuleId) {
  apiRequest("/api/postingRules/" + postingRuleId, "DELETE").then(response => {
    if (response.code === 200) {
      init();
    } else if (response.code === 401) {
      refreshToken(() => deletePostingRule(postingRuleId), () => router.push("/ui/login"));
    } else {
      info.value = response.message;
    }
  });
}

function onSearch() {
}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("postingRules")'>
      <Toolbar :on-create="create" :on-refresh="init" :on-search="onSearch" :search-query="searchQuery"/>
    </PageHeader>

    <section class="card">
      <div v-if="postingRules.length > 0" class="table-wrap">
        <table class="data-table" role="table">
          <thead>
          <tr>
            <th>#</th>
            <th>{{ $t("sequence") }}</th>
            <th>{{ $t("transactionType.title") }}</th>
            <th>{{ $t("debitAccount") }}</th>
            <th>{{ $t("creditAccount") }}</th>
            <th>{{ $t("amountSource") }}</th>
            <th>{{ $t("partnerSide") }}</th>
            <th aria-hidden="true" class="actions-col"></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="postingRule in postingRules" :key="postingRule.id">
            <td class="mono">{{ postingRule.id }}</td>
            <td>{{ postingRule.sequence }}</td>
            <td>{{ postingRule.transactionTypeCode }}</td>
            <td>{{ postingRule.debitAccountCode }}</td>
            <td>{{ postingRule.creditAccountCode }}</td>
            <td>{{ postingRule.amountSource }}</td>
            <td>{{ postingRule.partnerSide }}</td>
            <td class="actions-col">
              <button class="btn btn-sm" @click="edit(postingRule.id)">{{ $t("edit") }}</button>
              <button class="btn btn-sm btn-danger" @click="deletePostingRule(postingRule.id)">
                {{ $t("delete") }}
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div v-else class="empty">
        {{ $t("noRecords") }}
      </div>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>
