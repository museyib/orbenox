<script setup>
import {ref} from "vue";
import {apiRequest} from "@/api.js";
import {useRouter} from "vue-router";
import InfoBar from "@/components/InfoBar.vue";

const router = useRouter();
const info = ref("");
const infoType = ref('');

function handleLogin(event) {
  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());
  localStorage.removeItem('token');
  apiRequest('/api/auth/login', 'POST', data).then(response => {
    if (response.code === 200) {
      localStorage.setItem('token', response.data);
      localStorage.setItem('username', data.username);
      localStorage.setItem('password', data.password);
      router.push('/');
    } else {
      info.value = response.message;
      infoType.value = "error";
    }
  });
}
</script>
<template>
  <h1>Please log in</h1>
  <form id="loginForm" @submit.prevent="handleLogin">
    <label>Username: <input name="username" type="text"/></label>
    <label>Password: <input name="password" type="password"/></label>
    <button class="btn btn-sm" type="submit">Login</button>
  </form>
  <InfoBar :info="info" :type="infoType"/>
</template>
