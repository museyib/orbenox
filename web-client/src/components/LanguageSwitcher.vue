<script setup>
import {ensureLocale} from "@/i18n.js";
import {ref} from "vue";

const locales = [
  {code: 'en', label: 'English'},
  {code: 'az', label: 'Azərbaycan'},
]

const current = ref(localStorage.getItem('locale') || 'en');

async function changeLocale(e) {
  const code = e.target.value;
  current.value = code;
  await ensureLocale(code);
}
</script>
<template>
  <select id="language" :value="current" @change="changeLocale">
    <option v-for="l in locales" :key="l.code" :value="l.code">
      {{ l.label }}
    </option>
  </select>
</template>