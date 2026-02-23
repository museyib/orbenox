<script setup>
import {apiRequest, refreshToken} from "@/api.js";
import {useRouter} from 'vue-router';
import {onMounted, ref} from "vue";
import InfoBar from "@/components/InfoBar.vue";
import MainLayout from "@/components/MainLayout.vue";
import PageHeader from "@/components/PageHeader.vue";

const router = useRouter();
const info = ref('');
const infoType = ref('');
const userTypes = ref([]);
const selectedUserType = ref();
const roles = ref([]);
const assignedRoles = ref([]);
const enabled = ref(true);

function init() {
  apiRequest('/api/lookups?types=userTypes,roles',
      'GET').then(response => {
    if (response.code === 200) {
      userTypes.value = response.data.userTypes.filter(userType => userType.enabled);
      roles.value = response.data.roles.filter(role => role.enabled);
      if (userTypes.value.length > 0) {
        selectedUserType.value = userTypes.value[0];
      }
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
  });
}

function createUser(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  data.enabled = enabled.value;
  data.userTypeId = selectedUserType.value?.id ?? null;
  data.roleIds = assignedRoles.value.map(role => role.id);
  apiRequest('/api/users', 'POST', data).then(response => {
    if (response.code === 200) {
      router.push('/ui/users');
    } else if (response.code === 401) {
      refreshToken(() => createUser(event), () => router.push('/ui/login'));
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  }).catch(error => {
    info.value = error;
    infoType.value = "error";
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
    <PageHeader :title='$t("user.new")'/>

    <section class="card">
      <form @submit.prevent="createUser">
        <label>{{ $t('user.username') }}: <input name="username" type="text"/></label>
        <label>{{ $t('user.password') }}: <input name="password" type="password"/></label>
        <label>{{ $t('user.displayName') }}: <input name="displayName" type="text"/></label>
        <label>{{ $t('user.userType') }}:
          <select id='userType' v-model="selectedUserType">
            <option v-for="userType in userTypes"
                    :key="userType.id"
                    :value="userType"
            >
              {{ userType.name }}
            </option>
          </select>
        </label>
        <div class="dual-selects">
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
          </label>
        </div>
        <label>{{ $t('enabled') }}: <input v-model="enabled" name="enabled" type="checkbox"></label>
        <button class="btn btn-primary" type="submit">{{ $t('create') }}</button>
      </form>
    </section>
    <InfoBar :info="info" :type="infoType"/>
  </MainLayout>
</template>

