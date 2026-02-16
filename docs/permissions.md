# Permissions Matrix

## Overview

Orbenox uses a **Resource-Action-Permission** based authorization model. Each user is assigned one or more roles, and
each role has a set of permissions that define what resources they can access and what actions they can perform.

## Authorization Model

```
User → Role → Permission (Resource + Action)
```

### Core Concepts

- **Resource**: A domain entity or feature (e.g., PRODUCT, USER, PRICE_LIST)
- **Action**: An operation (READ, CREATE, UPDATE, DELETE)
- **Permission**: Combination of Resource + Action (e.g., PRODUCT:READ, USER:UPDATE)
- **Role**: Collection of permissions (e.g., ADMIN, MANAGER, OPERATOR)

## Resources

### Product Domain

- `PRODUCT` - Product master data
- `PRODUCT_CATEGORY` - Product categorization
- `PRODUCT_CLASS` - Product classification
- `PRODUCT_TYPE` - Product type definitions
- `PRODUCT_GROUP` - Product grouping
- `BRAND` - Product brands
- `PRODUCER` - Product manufacturers
- `PRODUCT_UNIT` - Product unit of measure relationships
- `PRODUCT_BARCODE` - Product barcode management
- `PRODUCT_PRICE` - Product pricing
- `PRODUCT_WAREHOUSE` - Product-warehouse relationships

### Inventory Domain

- `WAREHOUSE` - Warehouse management
- `STOCK` - Stock/inventory operations
- `DOCUMENT` - Inventory documents (receipt, issue, transfer)
- `TRANSACTION_TYPE` - Transaction type configuration

### Finance Domain

- `PRICE_LIST` - Price list management
- `CURRENCY` - Currency definitions
- `ACCOUNTING` - Accounting entries

### Reference Data

- `COUNTRY` - Country master data
- `UNIT` - Unit of measure
- `UNIT_DIMENSION` - Unit dimension (weight, volume, etc.)
- `RESOURCE` - Resource definitions
- `ACTION` - Action definitions
- `LOOKUP` - General lookup data

### Security Domain

- `USER` - User management
- `ROLE` - Role management
- `PERMISSION` - Permission management
- `USER_TYPE` - User type configuration

## Actions

- `READ` - View/query resource
- `CREATE` - Create new resource
- `UPDATE` - Modify existing resource
- `DELETE` - Soft delete resource
- `APPROVE` - Approve operations (documents, etc.)
- `POST` - Post documents to accounting
- `CANCEL` - Cancel operations

## Standard Roles

### ADMIN (Super Administrator)

Full access to all resources and actions.

**Permissions:**

- All resources: READ, CREATE, UPDATE, DELETE
- Special: APPROVE, POST, CANCEL on all documents

**Use Case:** System administrators, IT staff

---

### MANAGER

Full operational access except system configuration.

**Permissions:**

| Resource         | Actions                             |
|------------------|-------------------------------------|
| PRODUCT          | READ, CREATE, UPDATE, DELETE        |
| BRAND            | READ, CREATE, UPDATE                |
| PRODUCER         | READ, CREATE, UPDATE                |
| PRODUCT_CATEGORY | READ, CREATE, UPDATE                |
| PRICE_LIST       | READ, CREATE, UPDATE                |
| WAREHOUSE        | READ                                |
| DOCUMENT         | READ, CREATE, UPDATE, APPROVE, POST |
| STOCK            | READ                                |
| USER             | READ                                |
| LOOKUP           | READ                                |

**Use Case:** Department managers, operations managers

---

### OPERATOR

Standard operational user with limited permissions.

**Permissions:**

| Resource   | Actions              |
|------------|----------------------|
| PRODUCT    | READ                 |
| BRAND      | READ                 |
| PRODUCER   | READ                 |
| PRICE_LIST | READ                 |
| WAREHOUSE  | READ                 |
| DOCUMENT   | READ, CREATE, UPDATE |
| STOCK      | READ                 |
| LOOKUP     | READ                 |

**Use Case:** Data entry staff, warehouse operators

---

### VIEWER (Read-Only)

Read-only access for reporting and viewing.

**Permissions:**

- All resources: READ only

**Use Case:** Auditors, reporting staff, executives

---

## Permission Examples

### Product Management

```java
@PreAuthorize("hasPermission('PRODUCT', 'READ')")
public List<ProductItem> getAllProducts() {  }

@PreAuthorize("hasPermission('PRODUCT', 'CREATE')")
public ProductItem createProduct(ProductCreateDto dto) {  }

@PreAuthorize("hasPermission('PRODUCT', 'UPDATE')")
public ProductItem updateProduct(Long id, ProductUpdateDto dto) {  }

@PreAuthorize("hasPermission('PRODUCT', 'DELETE')")
public void deleteProduct(Long id) {  }
```

### Document Operations

```java
@PreAuthorize("hasPermission('DOCUMENT', 'READ')")
public DocumentItem getDocument(Long id) {  }

@PreAuthorize("hasPermission('DOCUMENT', 'CREATE')")
public DocumentItem createDocument(DocumentCreateDto dto) {  }

@PreAuthorize("hasPermission('DOCUMENT', 'APPROVE')")
public void approveDocument(Long id) {  }

@PreAuthorize("hasPermission('DOCUMENT', 'POST')")
public void postDocument(Long id) {  }
```

## Permission Inheritance

Roles can be hierarchical, but currently the system uses flat role assignment. Each user can have multiple roles, and
permissions are cumulative (union of all role permissions).

## Custom Permissions

New permissions can be created by:

1. Adding new Resource via database
2. Defining Actions for that resource
3. Creating Permission entries (Resource + Action combinations)
4. Assigning permissions to roles

## Security Implementation

### CustomPermissionEvaluator

Located in `com.orbenox.erp.security.service.CustomPermissionEvaluator`

Evaluates permission expressions like `hasPermission('PRODUCT', 'READ')` by:

1. Loading user's roles from security context
2. Fetching all permissions for those roles
3. Checking if requested resource+action combination exists

### Method Security

Enabled via `@EnableMethodSecurity` in `MethodSecurityConfig`

Uses Spring Security's `@PreAuthorize` annotation on controller methods.

## Lookup Permissions

Special permission: `LOOKUP:READ`

Required for accessing reference data endpoints:

- `/api/lookups/products`
- `/api/lookups/brands`
- `/api/lookups/units`
- etc.

## Best Practices

1. **Principle of Least Privilege**: Assign minimum necessary permissions
2. **Role-Based Assignment**: Assign roles, not individual permissions
3. **Regular Audits**: Review user permissions periodically
4. **Document Custom Roles**: Document any custom roles created
5. **Test Permissions**: Test permission changes in non-production first

## Permission Testing

Use Spring Security Test for testing:

```java
@WithMockUser(username = "testuser", authorities = {"PRODUCT:READ", "PRODUCT:CREATE"})
@Test
public void testCreateProduct() {
    // Test implementation
}
```

## Troubleshooting

### Access Denied Issues

1. Check user's roles: `SELECT * FROM user_role WHERE user_id = ?`
2. Check role permissions: `SELECT * FROM role_permission WHERE role_id = ?`
3. Verify resource and action exist
4. Check logs for permission evaluation details

### Adding New Resource

1. Insert into `resource` table
2. Create actions for resource
3. Create permission entries
4. Assign to appropriate roles

---

_Last updated: 2026-02-12_
