<script setup>

import LanguageSwitcher from "@/components/LanguageSwitcher.vue";
import MenuBar from "@/components/MenuBar.vue";
import {useRouter} from "vue-router";

const router = useRouter();

const home = () => {
  router.push('/');
}
</script>

<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="brand" @click="home">Orbenox</div>
      </div>

      <MenuBar/>
      <div class="sidebar-footer">
        <LanguageSwitcher/>
      </div>
    </aside>

    <main class="content">
      <slot/>
    </main>
  </div>
</template>

<style scoped>
.layout {
  display: grid;
  grid-template-columns: 230px 1fr;
  height: 100vh;
  background: #f6f8fa;
}

.sidebar {
  background: #ffffff;
  border-right: 1px solid #e3e6eb;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
  padding: 1rem 0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.sidebar-header {
  padding: 0 1.2rem 1.2rem;
  border-bottom: 1px solid #f1f3f5;
}

.brand {
  font-weight: 700;
  font-size: 1.2rem;
  color: #2563eb;
  cursor: pointer;
}

.sidebar-footer {
  margin-top: auto;
  padding: 1rem;
  border-top: 1px solid #f1f3f5;
}

.content {
  padding: 1.2rem 1.5rem;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: auto;
}

.language-switcher select {
  padding: .3rem .5rem;
  border-radius: 6px;
  border: 1px solid var(--border);
}

@media (max-width: 820px) {
  .layout {
    grid-template-columns: 70px 1fr;
  }

  .brand {
    font-size: 1rem;
    text-align: center;
  }
}

@media (max-width: 640px) {
  .layout {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: fixed;
    height: 100vh;
    width: 220px;
    transform: translateX(-100%);
    transition: transform .3s ease;
    z-index: 999;
  }

  .sidebar.open {
    transform: translateX(0);
  }
}
</style>
