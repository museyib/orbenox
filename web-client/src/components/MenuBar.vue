<script setup>
import {ref, watch} from "vue";
import {useRoute} from "vue-router";

const route = useRoute();

const menuGroups = [
  {
    id: "security",
    title: "security",
    items: [
      {to: "/ui/users", prefix: "/ui/users", label: "users"},
      {to: "/ui/roles", prefix: "/ui/roles", label: "roles"},
      {to: "/ui/resources", prefix: "/ui/resources", label: "resources"}
    ]
  },
  {
    id: "masterData",
    title: "masterData",
    items: [
      {to: "/ui/units", prefix: "/ui/units", label: "units"},
      {to: "/ui/unitDimensions", prefix: "/ui/unitDimensions", label: "unitDimensions"},
      {to: "/ui/currencies", prefix: "/ui/currencies", label: "currencies"},
      {to: "/ui/countries", prefix: "/ui/countries", label: "countries"},
      {to: "/ui/warehouses", prefix: "/ui/warehouses", label: "warehouses"},
      {to: "/ui/priceLists", prefix: "/ui/priceLists", label: "priceLists"}
    ]
  },
  {
    id: "productSetup",
    title: "productSetup",
    items: [
      {to: "/ui/brands", prefix: "/ui/brands", label: "brands"},
      {to: "/ui/producers", prefix: "/ui/producers", label: "producers"},
      {to: "/ui/productTypes", prefix: "/ui/productTypes", label: "productTypes"},
      {to: "/ui/productClasses", prefix: "/ui/productClasses", label: "productClasses"},
      {to: "/ui/productCategories", prefix: "/ui/productCategories", label: "productCategories"},
      {to: "/ui/productGroups", prefix: "/ui/productGroups", label: "productGroups"},
      {to: "/ui/products", prefix: "/ui/products", label: "products"}
    ]
  },
  {
    id: "operations",
    title: "operations",
    items: [
      {to: "/ui/transactionTypes", prefix: "/ui/transactionTypes", label: "transactionTypes"},
      {to: "/ui/accounts", prefix: "/ui/accounts", label: "accounts"},
      {to: "/ui/postingRules", prefix: "/ui/postingRules", label: "postingRules"}
    ]
  },
  {
    id: "businessPartners",
    title: "businessPartners",
    items: [
      {to: "/ui/businessPartners", prefix: "/ui/businessPartners", label: "businessPartners"},
      {to: "/ui/businessPartnerRoles", prefix: "/ui/businessPartnerRoles", label: "businessPartnerRoles"},
    ]
  },
  {
    id: "documents",
    title: "documents",
    items: [
      {to: "/ui/documents", prefix: "/ui/documents", label: "documents"}
    ]
  }
];

function isActive(prefix) {
  return route.path.startsWith(prefix);
}

function findActiveGroupId() {
  const activeGroup = menuGroups.find(group =>
      group.items.some(item => isActive(item.prefix))
  );
  return activeGroup ? activeGroup.id : menuGroups[0].id;
}

const openGroupId = ref(findActiveGroupId());

function toggleGroup(groupId) {
  openGroupId.value = openGroupId.value === groupId ? "" : groupId;
}

watch(
    () => route.path,
    () => {
      openGroupId.value = findActiveGroupId();
    },
    {immediate: true}
);
</script>
<template>
  <nav aria-label="Main navigation" class="sidebar-nav">
    <section v-for="group in menuGroups" :key="group.id" class="menu-group">
      <button
          :aria-expanded="openGroupId === group.id"
          class="group-toggle"
          type="button"
          @click="toggleGroup(group.id)"
      >
        <span>{{ $t(group.title) }}</span>
        <span class="chevron" :class="{open: openGroupId === group.id}">^</span>
      </button>

      <div v-show="openGroupId === group.id" class="group-links">
        <router-link
            v-for="item in group.items"
            :key="item.to"
            :class="{active: isActive(item.prefix)}"
            :to="item.to"
        >
          {{ $t(item.label) }}
        </router-link>
      </div>
    </section>
  </nav>
</template>

<style scoped>
.sidebar-nav {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-top: 0.5rem;
}

.menu-group {
  border-bottom: 1px solid #eef1f5;
}

.group-toggle {
  width: 100%;
  border: none;
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.6rem 1.2rem;
  color: #111827;
  font-size: 0.85rem;
  font-weight: 600;
  letter-spacing: 0.03em;
}

.group-toggle:hover {
  background: rgba(37, 99, 235, 0.06);
}

.chevron {
  display: inline-flex;
  transition: transform 0.2s ease;
}

.chevron.open {
  transform: rotate(180deg);
}

.group-links a {
  display: block;
  padding: 0.65rem 1.2rem;
  text-decoration: none;
  color: #374151;
  font-size: 0.95rem;
  border-left: 3px solid transparent;
  transition: 0.2s;
}

.group-links a:hover {
  background: rgba(37, 99, 235, 0.08);
  border-left-color: #2563eb;
  color: #1d4ed8;
}

.group-links a.router-link-active, .group-links a.active {
  background: rgba(37, 99, 235, 0.12);
  border-left-color: #2563eb;
  color: #1d4ed8;
}

@media (max-width: 820px) {
  .group-toggle {
    padding: 0.55rem 0.6rem;
    justify-content: center;
    gap: 0.35rem;
    font-size: 0.72rem;
  }

  .group-links a {
    padding: 0.6rem;
    text-align: center;
    font-size: 0.8rem;
  }
}
</style>
