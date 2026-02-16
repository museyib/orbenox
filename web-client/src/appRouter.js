import Users from "@/components/user/Users.vue";
import Roles from "@/components/role/Roles.vue";
import Login from "@/components/Login.vue";
import {createRouter, createWebHistory} from "vue-router";
import CreateUser from "@/components/user/CreateUser.vue";
import EditUser from "@/components/user/EditUser.vue";
import EditRole from "@/components/role/EditRole.vue";
import CreateRole from "@/components/role/CreateRole.vue";
import Resources from "@/components/resource/Resources.vue"
import EditResource from "@/components/resource/EditResource.vue";
import CreateResource from "@/components/resource/CreateResource.vue";
import UserPermission from "@/components/permission/UserPermission.vue";
import RolePermission from "@/components/permission/RolePermission.vue";
import Units from "@/components/unit/Units.vue";
import CreateUnit from "@/components/unit/CreateUnit.vue";
import EditUnit from "@/components/unit/EditUnit.vue";
import Home from "@/components/Home.vue";
import Brands from "@/components/brand/Brands.vue";
import CreateBrand from "@/components/brand/CreateBrand.vue";
import EditBrand from "@/components/brand/EditBrand.vue";
import Producers from "@/components/producer/Producers.vue";
import CreateProducer from "@/components/producer/CreateProducer.vue";
import EditProducer from "@/components/producer/EditProducer.vue";
import ProductTypes from "@/components/productType/ProductTypes.vue";
import CreateProductType from "@/components/productType/CreateProductType.vue";
import EditProductType from "@/components/productType/EditProductType.vue";
import ProductClasses from "@/components/productClass/ProductClasses.vue";
import CreateProductClass from "@/components/productClass/CreateProductClass.vue";
import EditProductClass from "@/components/productClass/EditProductClass.vue";
import ProductCategories from "@/components/productCategory/ProductCategories.vue";
import CreateProductCategory from "@/components/productCategory/CreateProductCategory.vue";
import EditProductCategory from "@/components/productCategory/EditProductCategory.vue";
import ProductGroups from "@/components/productGroup/ProductGroups.vue";
import CreateProductGroup from "@/components/productGroup/CreateProductGroup.vue";
import EditProductGroup from "@/components/productGroup/EditProductGroup.vue";
import Products from "@/components/product/Products.vue";
import CreateProduct from "@/components/product/CreateProduct.vue";
import EditProduct from "@/components/product/EditProduct.vue";
import PriceLists from "@/components/priceList/PriceLists.vue";
import CreatePriceList from "@/components/priceList/CreatePriceList.vue";
import EditPriceList from "@/components/priceList/EditPriceList.vue";
import Currencies from "@/components/currency/Currencies.vue";
import CreateCurrency from "@/components/currency/CreateCurrency.vue";
import EditCurrency from "@/components/currency/EditCurrency.vue";
import Countries from "@/components/country/Countries.vue";
import CreateCountry from "@/components/country/CreateCountry.vue";
import EditCountry from "@/components/country/EditCountry.vue";
import ProductPrices from "@/components/productPrice/ProductPrices.vue";
import ProductBarcodes from "@/components/productBarcode/ProductBarcodes.vue";
import ProductUnits from "@/components/productUnit/ProductUnits.vue";
import Warehouses from "@/components/warehouse/Warehouses.vue";
import CreateWarehouse from "@/components/warehouse/CreateWarehouse.vue";
import EditWarehouse from "@/components/warehouse/EditWarehouse.vue";
import ProductWarehouses from "@/components/productWarehouse/ProductWarehouses.vue";
import TransactionTypes from "@/components/transactionType/TransactionTypes.vue";
import CreateTransactionType from "@/components/transactionType/CreateTransactionType.vue";
import EditTransactionType from "@/components/transactionType/EditTransactionType.vue";

const routes = [
    {path: '/', component: Home, meta: {title: 'Home'}},
    {path: '/ui/login', component: Login, meta: {title: 'Login'}},
    {path: '/ui/users', component: Users, meta: {title: 'Users'}},
    {path: '/ui/users/create', component: CreateUser, meta: {title: 'Create User'}},
    {path: '/ui/users/edit/:id', component: EditUser, meta: {title: 'Edit User'}},
    {path: '/ui/roles', component: Roles, meta: {title: 'Roles'}},
    {path: '/ui/roles/edit/:id', component: EditRole, meta: {title: 'Edit Role'}},
    {path: '/ui/roles/create', component: CreateRole, meta: {title: 'Create Role'}},
    {path: '/ui/resources', component: Resources, meta: {title: 'Resources'}},
    {path: '/ui/resources/edit/:id', component: EditResource, meta: {title: 'Edit Resource'}},
    {path: '/ui/resources/create', component: CreateResource, meta: {title: 'Create Resource'}},
    {path: '/ui/users/:id/permission', component: UserPermission, meta: {title: 'User Permission'}},
    {path: '/ui/roles/:id/permission', component: RolePermission, meta: {title: 'Role Permission'}},
    {path: '/ui/units', component: Units, meta: {title: 'Units'}},
    {path: '/ui/units/create', component: CreateUnit, meta: {title: 'Create Unit'}},
    {path: '/ui/units/edit/:id', component: EditUnit, meta: {title: 'Edit Unit'}},
    {path: '/ui/currencies', component: Currencies, meta: {title: 'Currencies'}},
    {path: '/ui/currencies/create', component: CreateCurrency, meta: {title: 'Create Currency'}},
    {path: '/ui/currencies/edit/:id', component: EditCurrency, meta: {title: 'Edit Currency'}},
    {path: '/ui/brands', component: Brands, meta: {title: 'Brands'}},
    {path: '/ui/brands/create', component: CreateBrand, meta: {title: 'Create Brand'}},
    {path: '/ui/brands/edit/:id', component: EditBrand, meta: {title: 'Edit Brand'}},
    {path: '/ui/producers', component: Producers, meta: {title: 'Producers'}},
    {path: '/ui/producers/create', component: CreateProducer, meta: {title: 'Create Producer'}},
    {path: '/ui/producers/edit/:id', component: EditProducer, meta: {title: 'Edit Producer'}},
    {path: '/ui/productTypes', component: ProductTypes, meta: {title: 'Product Types'}},
    {path: '/ui/productTypes/create', component: CreateProductType, meta: {title: 'Create Product Type'}},
    {path: '/ui/productTypes/edit/:id', component: EditProductType, meta: {title: 'Edit Product Type'}},
    {path: '/ui/productClasses', component: ProductClasses, meta: {title: 'Product Classes'}},
    {path: '/ui/productClasses/create', component: CreateProductClass, meta: {title: 'Create Product Class'}},
    {path: '/ui/productClasses/edit/:id', component: EditProductClass, meta: {title: 'Edit Product Class'}},
    {path: '/ui/productCategories', component: ProductCategories, meta: {title: 'Product Categories'}},
    {path: '/ui/productCategories/create', component: CreateProductCategory, meta: {title: 'Create Product Category'}},
    {path: '/ui/productCategories/edit/:id', component: EditProductCategory, meta: {title: 'Edit Product Category'}},
    {path: '/ui/productGroups', component: ProductGroups, meta: {title: 'Product Groups'}},
    {path: '/ui/productGroups/create', component: CreateProductGroup, meta: {title: 'Create Product Group'}},
    {path: '/ui/productGroups/edit/:id', component: EditProductGroup, meta: {title: 'Edit Product Group'}},
    {path: '/ui/products', component: Products, meta: {title: 'Products'}},
    {path: '/ui/products/create', component: CreateProduct, meta: {title: 'Create Product'}},
    {path: '/ui/products/edit/:id', component: EditProduct, meta: {title: 'Edit Product'}},
    {path: '/ui/priceLists', component: PriceLists, meta: {title: 'Price Lists'}},
    {path: '/ui/priceLists/create', component: CreatePriceList, meta: {title: 'Create Price List'}},
    {path: '/ui/priceLists/edit/:id', component: EditPriceList, meta: {title: 'Edit Price List'}},
    {path: '/ui/countries', component: Countries, meta: {title: 'Countries'}},
    {path: '/ui/countries/create', component: CreateCountry, meta: {title: 'Create Country'}},
    {path: '/ui/countries/edit/:id', component: EditCountry, meta: {title: 'Edit Country'}},
    {path: '/ui/products/:id/prices', component: ProductPrices, meta: {title: 'Product Prices'}},
    {path: '/ui/products/:id/barcodes', component: ProductBarcodes, meta: {title: 'Product Barcodes'}},
    {path: '/ui/products/:id/units', component: ProductUnits, meta: {title: 'Product Units'}},
    {path: '/ui/products/:id/warehouses', component: ProductWarehouses, meta: {title: 'Product Warehouses'}},
    {path: '/ui/warehouses', component: Warehouses, meta: {title: 'Warehouses'}},
    {path: '/ui/warehouses/create', component: CreateWarehouse, meta: {title: 'Create Warehouse'}},
    {path: '/ui/warehouses/edit/:id', component: EditWarehouse, meta: {title: 'Edit Warehouse'}},
    {path: '/ui/transactionTypes', component: TransactionTypes, meta: {title: 'TransactionTypes'}},
    {path: '/ui/transactionTypes/create', component: CreateTransactionType, meta: {title: 'Create TransactionType'}},
    {path: '/ui/transactionTypes/edit/:id', component: EditTransactionType, meta: {title: 'Edit TransactionType'}},
]

const appRouter = createRouter({
    history: createWebHistory(),
    routes
})

appRouter.beforeEach((to, from, next) => {
    if (to.meta.title)
        document.title = to.meta.title;
    next();
});


export default appRouter;