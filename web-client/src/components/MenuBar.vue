<script setup>
import {useRoute} from "vue-router";

const route = useRoute();

function isActive(prefix) {
  return route.path.startsWith(prefix);
}
</script>
<template>
  <nav aria-label="Main navigation" class="sidebar-nav">
    <router-link :class="{active: isActive('/ui/users')}" to="/ui/users">{{ $t('users') }}</router-link>
    <router-link :class="{active: isActive('/ui/roles')}" to="/ui/roles">{{ $t('roles') }}</router-link>
    <router-link :class="{active: isActive('/ui/resources')}" to="/ui/resources">{{ $t('resources') }}</router-link>
    <router-link :class="{active: isActive('/ui/units')}" to="/ui/units">{{ $t('units') }}</router-link>
    <router-link :class="{active: isActive('/ui/unitDimensions')}" to="/ui/unitDimensions">{{ $t('unitDimensions') }}</router-link>
    <router-link :class="{active: isActive('/ui/currencies')}" to="/ui/currencies">{{ $t('currencies') }}</router-link>
    <router-link :class="{active: isActive('/ui/brands')}" to="/ui/brands">{{ $t('brands') }}</router-link>
    <router-link :class="{active: isActive('/ui/producers')}" to="/ui/producers">{{ $t('producers') }}</router-link>
    <router-link :class="{active: isActive('/ui/countries')}" to="/ui/countries">{{ $t('countries') }}</router-link>
    <router-link :class="{active: isActive('/ui/productTypes')}" to="/ui/productTypes">
      {{$t('productTypes') }}</router-link>
    <router-link :class="{active: isActive('/ui/productClasses')}" to="/ui/productClasses">
      {{$t('productClasses') }}</router-link>
    <router-link :class="{active: isActive('/ui/productCategories')}" to="/ui/productCategories">
      {{ $t('productCategories') }}</router-link>
    <router-link :class="{active: isActive('/ui/productGroups')}" to="/ui/productGroups">
      {{$t('productGroups') }}</router-link>
    <router-link :class="{active: isActive('/ui/products')}" to="/ui/products">{{ $t('products') }}</router-link>
    <router-link :class="{active: isActive('/ui/priceLists')}" to="/ui/priceLists">{{ $t('priceLists') }}</router-link>
    <router-link :class="{active: isActive('/ui/warehouses')}" to="/ui/warehouses">{{ $t('warehouses') }}</router-link>
    <router-link :class="{active: isActive('/ui/transactionTypes')}" to="/ui/transactionTypes">{{ $t('transactionTypes') }}</router-link>
    <router-link :class="{active: isActive('/ui/accounts')}" to="/ui/accounts">{{ $t('accounts') }}</router-link>
    <router-link :class="{active: isActive('/ui/businessPartners')}" to="/ui/businessPartners">{{ $t('businessPartners') }}</router-link>
    <router-link :class="{active: isActive('/ui/businessPartnerRoles')}" to="/ui/businessPartnerRoles">{{ $t('businessPartnerRoles') }}</router-link>
    <router-link :class="{active: isActive('/ui/postingRules')}" to="/ui/postingRules">{{ $t('postingRules') }}</router-link>
    <router-link :class="{active: isActive('/ui/documents')}" to="/ui/documents">{{ $t('documents') }}</router-link>
  </nav>
</template>

<style scoped>
.sidebar-nav {
  display: flex;
  flex-direction: column;
  padding-top: 0.5rem;
}

.sidebar-nav a {
  padding: 0.65rem 1.2rem;
  text-decoration: none;
  color: #374151;
  font-size: 0.95rem;
  border-left: 3px solid transparent;
  transition: 0.2s;
}

.sidebar-nav a:hover {
  background: rgba(37, 99, 235, 0.08);
  border-left-color: #2563eb;
  color: #1d4ed8;
}

.sidebar-nav a.router-link-active, a.active {
  background: rgba(37, 99, 235, 0.12);
  border-left-color: #2563eb;
  color: #1d4ed8;
}

@media (max-width: 820px) {
  .sidebar-nav a {
    padding: 0.6rem;
    text-align: center;
    font-size: 0.8rem;
  }
}
</style>
