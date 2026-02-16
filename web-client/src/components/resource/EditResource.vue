<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import {useRoute, useRouter} from 'vue-router';
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const router = useRouter();
const route = useRoute();
const info = ref('');
const resource = ref({});
const actions = ref([]);
const assignedActions = ref([]);

function init() {
  apiRequest('/api/resources/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      resource.value = response.data.resource;
      assignedActions.value = response.data.actions;

      apiRequest('/api/lookups?types=actions', 'GET').then((response) => {
        if (response.code === 200) {
          actions.value = response.data.actions.filter(action => !assignedActions.value.includes(action));
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

function updateRole() {
  resource.value.actions = assignedActions;
  apiRequest('/api/resources/' + route.params.id, 'PATCH', resource.value).then((response) => {
    if (response.code === 200) {
      info.value = 'Resource updated';
    } else if (response.code === 401) {
      refreshToken(() => updateRole(), () => router.push('/ui/login'));
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
    <PageHeader :title='$t("resource.edit")'/>

    <section class="card">
      <form @submit.prevent='updateRole'>
        <label>
          <input v-model='resource.id' hidden="hidden" name="id" type="text"/>
        </label>
        <label>{{ $t('code') }}: <input v-model='resource.code' name="code" type="text"/></label><br/>
        <label>{{ $t('name') }}: <input v-model='resource.name' autocomplete='false' name="name"
                                        type="text"/></label><br/>
        <label>{{ $t('enabled') }}: <input v-model="resource.enabled" type="checkbox"/></label><br/>
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
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>