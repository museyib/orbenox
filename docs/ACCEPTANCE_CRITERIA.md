# Acceptance Criteria (Example + Rules)

Use this document as the acceptance criteria template and ruleset for **every** change request.
Keep the project structure unchanged; only add or modify files required by the change.

## Rules For Every Change
1. Start each change with a short, explicit **Acceptance Criteria** section in the response.
2. Criteria must be testable and unambiguous.
3. State **what is in scope** and **what is out of scope**.
4. List **status/permission constraints** if applicable.
5. Include **localization requirements** when user-facing text changes.
6. Include **backward compatibility** or **API contract** notes if impacted.
7. If tests are not run, say so.

## Acceptance Criteria Template

**Change Title**
- One sentence summary of the change.

**Scope**
- In scope: …
- Out of scope: …

**Functional Requirements**
- …

**Status/Permissions Constraints**
- …

**Localization**
- New or changed user-facing strings must be localized (EN + AZ).
- Locale files must remain UTF-8.

**Quality & Compatibility**
- No breaking changes to existing public API endpoints unless explicitly requested.
- No changes to project structure unless explicitly requested.

**Verification**
- Tests to run: …
- If not run, state “Tests not run”.
