# Orbenox – Project Documentation

This documentation is intended for **developers and integrators**, not library consumers. Most classes and methods are self-descriptive; therefore, this documentation focuses on **architecture, concepts, and rules** rather than JavaDoc-level details.

---

## 1. High-Level Architecture

**Backend**: Spring Boot (REST API)

**Frontend**: Vue 3 + Vite (test client)

**Core concepts**:
- Resource–Action–Permission based authorization
- Soft delete strategy
- Lookup-based reference data
- DTO separation for create/update/read
- Centralized API response structure

---

## 2. Authentication & Authorization

### 2.1 Authentication
- JWT-based authentication
- CustomAuthenticationEntryPoint handles unauthenticated access
- Tokens are required for all protected endpoints

### 2.2 Authorization Model
Authorization is **resource-action based**, not role-method based.

**Entities involved**:
- `Resource`
- `Action`
- `Role`
- `Permission`
- `User`

A user is authorized when:
```
User → Role → Permission(Resource + Action)
```

Examples:
- `PRODUCT : READ`
- `PRICE_LIST : UPDATE`

Authorization is enforced centrally via Spring Security.

---

## 3. Permission Resolution Flow

1. Incoming request hits controller
2. Spring Security filter intercepts
3. Required permission is resolved from endpoint metadata
4. User roles are loaded
5. Permissions are checked
6. Access is granted or denied

Denied access is handled by:
- `CustomAccessDeniedHandler`

---

## 4. Lookup System

Lookups provide **reference data** required by multiple entities.

Examples:
- Product → Brand
- Product → Unit
- PriceList → Currency
- Resource → Action

### 4.1 Lookup Characteristics
- Read-only endpoints
- Cached aggressively
- Permission protected (usually `LOOKUP : READ`)
- May return **user-filtered results** in the future

### 4.2 Why Lookups Are Centralized
- Avoid duplication
- Consistent filtering rules
- Central permission control

---

## 5. Soft Delete Strategy

Entities are never physically deleted.

Instead:
```java
boolean deleted;
```

Rules:
- `deleted = true` means logically removed
- All queries filter `deleted = false`
- Unique constraints are validated manually

In some cases, previously deleted entities may be **revived** instead of recreated.

---

## 6. DTO Strategy

DTOs are separated by responsibility:

- `CreateXDto`
- `UpdateXDto`
- `XItem / XResponseDto`

### Why?
- Clear API contracts
- Avoid misleading required fields
- Better validation control

---

## 7. Validation & Localization

- Bean Validation (`@NotNull`, `@NotBlank`, etc.)
- Messages are localized using `messages.properties`
- Client receives localized validation errors

---

## 8. API Response Format

All endpoints return:
```json
{
  "success": true,
  "code": 0,
  "message": "...",
  "messageKey": "...",
  "data": { }
}
```

Handled centrally via:
- GlobalExceptionHandler
- Custom security handlers

---

## 9. Caching Strategy

Used selectively for:
- Lookups
- Small, frequently accessed reference data

Not used when:
- Data changes frequently outside the service
- Cache invalidation is unreliable

---

## 10. Frontend Notes

The Vue client is a **minimal test UI**, not production-ready.

Goals:
- Validate backend APIs
- Test permissions
- Verify localization

Future improvements:
- Central API service
- Better state management

---

## 11. Suggested Docs Expansion

If the project grows, add:
- `permissions.md` (deep dive)
- `lookups.md`
- `caching.md`
- `deployment.md`
- `contributing.md`

---

## 12. Philosophy

> Prefer clarity over cleverness.
> Prefer explicit design over hidden magic.
> Abstract only when patterns stabilize.

---

_End of documentation overview._

