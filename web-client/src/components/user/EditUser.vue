<script setup>
import {onMounted, ref} from "vue";
import {apiRequest, refreshToken} from "@/api.js";
import {useRoute, useRouter} from 'vue-router';
import {useI18n} from "vue-i18n";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const {t} = useI18n();
const router = useRouter();
const route = useRoute();
const info = ref('');
const userTypes = ref([]);
const roles = ref([]);
const assignedRoles = ref([]);
const user = ref({});

function init() {
  apiRequest('/api/users/' + route.params.id, 'GET').then((response) => {
    if (response.code === 200) {
      user.value = response.data.user;
      assignedRoles.value = response.data.roles.filter(role => role.enabled);

      apiRequest('/api/lookups?types=userTypes,roles',
          'GET').then(response => {
        if (response.code === 200) {
          userTypes.value = response.data.userTypes;

          const assignedIds = new Set(assignedRoles.value.map(r => r.id));
          roles.value = response.data.roles.filter(role => role.enabled && !assignedIds.has(role.id));
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

function updateUser() {
  user.value.roles = assignedRoles;
  apiRequest('/api/users/' + route.params.id, 'PATCH', user.value).then((response) => {
    if (response.code === 200) {
      info.value = t('user.updated');
    } else if (response.code === 401) {
      refreshToken(() => updateUser(), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
    }
  }).catch(error => {
    info.value = error;
  });
}

function addToAssignedRoles(role) {
  roles.value = roles.value.filter(r => r.id !== role.id);
  assignedRoles.value.push(role);
}

function removeFromAssignedRoles(role) {
  assignedRoles.value = assignedRoles.value.filter(r => r.id !== role.id);
  roles.value.push(role);
}

onMounted(() => init());
</script>

<template>
  <MainLayout>
    <PageHeader :title='$t("user.edit")'/>

    <section class="card">
      <form @submit.prevent='updateUser'>
        <label>
          <input v-model='user.id' hidden="hidden" name="id" type="text"/>
        </label><br/>
        <label>{{ $t('user.username') }}:
          <input v-model='user.username' autocomplete='false' name="username" type="text"/></label><br/>
        <label>{{ $t('user.displayName') }}:
          <input v-model='user.displayName' autocomplete='false' name="displayName" type="text"/></label><br/>
        <label>{{ $t('user.userType') }}:
          <select id='userType' v-model='user.userType'>
            <option v-for="userType in userTypes"
                    :key="userType.id"
                    :value="userType"
            >
              {{ userType.name }}
            </option>
          </select>
        </label><br/>
        <label>{{ $t('assignedRoles') }}:
          <select id='assignedRoles' multiple>
            <option v-for="role in assignedRoles"
                    v-on:dblclick="removeFromAssignedRoles(role)">
              {{ role.code }}
            </option>
          </select>
        </label>
        <label>{{ $t('roles') }}:
          <select id='availableRoles' multiple>
            <option v-for="role in roles"
                    v-on:dblclick="addToAssignedRoles(role)">
              {{ role.code }}
            </option>
          </select>
        </label><br/>
        <label>{{ $t('enabled') }}: <input v-model="user.enabled" name="enabled" type="checkbox"></label> <br/>
        <button class="btn btn-primary" type="submit">{{ $t('save') }}</button>
      </form>
    </section>
    <InfoBar :info="info"/>
  </MainLayout>
</template>