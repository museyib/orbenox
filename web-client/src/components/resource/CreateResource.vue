<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const router = useRouter();
const info = ref('');
const enabled = ref(true);
const actions = ref([]);
const assignedActions = ref([]);

function init() {
  apiRequest('/api/lookups?types=actions', 'GET').then((response) => {
    if (response.code === 200) {
      actions.value = response.data.actions;
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function createResource(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.enabled = enabled.value;
  data.actions = assignedActions.value;
  apiRequest('/api/resources', 'POST', data).then(response => {
    if (response.code === 200) {
      router.push('/ui/resources');
    } else if (response.code === 401) {
      refreshToken(() => createResource(event), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function addToAssignedActions(action) {
  actions.value = actions.value.filter(a => a !== action);
  assignedActions.value.push(action);
}

function removeFromAssignedActions(action) {
  assignedActions.value = assignedActions.value.filter(a => a !== action);
  actions.value.push(action);
}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("resource.new")'/>

    <section class="card">
      <form @submit.prevent="createResource">
        <label>{{ $t('code') }}: <input name="code" type="text"/></label><br/>
        <label>{{ $t('name') }}: <input autocomplete="false" name="name" type="text"/></label><br/>
        <label>{{ $t('enabled') }}: <input v-model="enabled" name="enabled" type="checkbox"/></label><br/>
        <label>{{ $t('assignedActions') }}:
          <select id="assignedActions" multiple>
            <option v-for="action in assignedActions"
                    v-on:dblclick='removeFromAssignedActions(action)'
            >{{ action }}
            </option>
          </select>
        </label>
        <label>{{ $t('actions') }}:
          <select id='availableActions' multiple>
            <option v-for="action in actions"
                    v-on:dblclick='addToAssignedActions(action)'
            >{{ action }}
            </option>
          </select><br/>
        </label><br/>
        <button class="btn btn-primary" type="submit">{{ $t('create') }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>