<script setup>

import {apiRequest, refreshToken} from "@/api.js";
import {onMounted, ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";
import {useI18n} from "vue-i18n";

const {t} = useI18n();
const router = useRouter();
const route = useRoute();
const info = ref('');
const actions = ref([]);
const resources = ref([]);
const currentUser = ref();
const currentResource = ref({});
const userPermissions = ref([]);
const permissionsToInsert = ref([]);
const permissionsToDelete = ref([]);

function init() {
  apiRequest('/api/permissions/user/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      currentUser.value = response.data.user;
      userPermissions.value = response.data.permissions;
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });

  apiRequest('/api/resources', 'GET').then((response) => {
    if (response.code === 200) {
      resources.value = response.data;
      if (resources.value.length > 0) {
        currentResource.value = resources.value[0];
      }
    } else if (response.code === 401) {
      refreshToken(() => init(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function updatePermission() {
  const data = {
    id: route.params.id,
    permissionsToInsert: permissionsToInsert.value,
    permissionsToDelete: permissionsToDelete.value
  }

  apiRequest('/api/permissions/user', 'PATCH', data).then((response) => {
    if (response.code === 200) {
      info.value = t('permission.updated');
    } else if (response.code === 401) {
      refreshToken(() => updatePermission(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function addToPermissions(action) {
  const permission = {
    appUser: currentUser.value,
    resource: currentResource.value,
    action: action,
  };
  actions.value = actions.value.filter(a => a.id !== action.id);
  userPermissions.value.push(permission);
  permissionsToInsert.value.push(permission);
}

function removeFromPermissions(permission) {
  const action = permission.action;
  userPermissions.value = userPermissions.value.filter(p => p.id !== permission.id);
  if (permission.resource.id === currentResource.value.id)
    actions.value.push(action);
  permissionsToDelete.value.push(permission);
}

const changeResource = (event) => {
  resources.value.forEach((resource => {
    const selectedId = Number(event.target.value);
    if (resource.id === selectedId) {
      currentResource.value = resource;
    }
  }));
}

function getResourceActions(resourceId) {
  apiRequest('/api/permissions/user/' + route.params.id + '/availableResourceActions?resourceId=' + resourceId, 'GET').then((response) => {
    if (response.code === 200) {
      const givenPermissionIDs = new Set(userPermissions.value.map((p) => p.resource.id + p.action.code));
      actions.value = response.data.filter(a => !givenPermissionIDs.has(resourceId + a.code));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

watch(currentResource, (newValue) => {
  if (newValue) {
    getResourceActions(newValue.id);
  }
});

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("permissions")'/>

    <section class="card">
      <form @submit.prevent="updatePermission">
        <label>
          <input id="userId" hidden="hidden" name="userId" type="text"/>
        </label>
        <label>{{ $t('grantedPermissions') }}:
          <select id="assignedPermissions" multiple>
            <option v-for="permission in userPermissions"
                    :value="permission"
                    v-on:dblclick="removeFromPermissions(permission)">
              {{ permission.resource.code + ':' + permission.action.code }}
            </option>
          </select>
        </label>
        <label>{{ $t('resource.title') }}:
          <select id="resource"
                  v-model='currentResource'
                  @change='changeResource'>
            <option v-for="resource in resources"
                    :key="resource.id"
                    :value="resource">
              {{ resource.code }}
            </option>
          </select>
        </label>
        <label>{{ $t('actions') }}:
          <select id='availableActions' multiple>
            <option v-for="action in actions"
                    :key="action.id"
                    :value="action.id"
                    v-on:dblclick="addToPermissions(action)">
              {{ action.code }}
            </option>
          </select>
        </label><br/>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>