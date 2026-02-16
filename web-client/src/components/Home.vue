<script setup>
import {refreshToken} from "@/api";
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import MainLayout from "@/components/MainLayout.vue";

const router = useRouter();
const info = ref("");

function init() {
  refreshToken(() => {
  }, () => router.push('/ui/login'));
}

onMounted(() => init());
</script>
<template>
  <MainLayout/>
</template>