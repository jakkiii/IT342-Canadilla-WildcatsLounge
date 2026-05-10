# Software Test Plan — Wildcats Lounge

## 1. Overview
- **Project:** Wildcats Lounge (IT342-Canadilla-WildcatsLounge)
- **Purpose:** Define scope, strategy, resources, and schedules for testing the vertical-slice refactor across backend, web, and mobile.
- **Authors:** Automated assistant (Copilot)
- **Date:** 2026-05-10

## 2. Scope
- In-scope: Auth, Menu browsing, Cart operations, Checkout/Orders, basic Events/Profile flows across Backend (Spring Boot), Web (Next.js), Mobile (Android).
- Out-of-scope: Admin/staff features not yet implemented, performance/stress testing, external infra provisioning.

## 3. Objectives
- Verify functional parity after vertical-slice refactor.
- Run automated test suites and smoke builds for each platform.
- Produce reproducible test artifacts and a regression report.

## 4. Test Items
- Backend: `backend/` (unit/integration tests via Maven)
- Web: `web/` (build + TypeScript checks + manual UI smoke flows)
- Mobile: `mobile/` (Gradle assemble — environment permitting)

## 5. Test Strategy
- Automated unit/integration: Run `mvn test` for backend.
- Build verification: `npm run build` for web; Gradle assemble for mobile.
- Smoke tests: Manual + scripted API calls (HTTP requests) to verify endpoints.
- Regression: Execute baseline tests (unit + smoke) and record results.

## 6. Entry/Exit Criteria
- Entry: Code merged into branch `vertical-slice-refactor` and environment variables configured.
- Exit: Backend test suite passes and web build succeeds. Mobile build success is required where environment supports Java toolchain; otherwise document failure and provide steps.

## 7. Environment
- Backend: Java 17 (recommended), Maven 3.9+, PostgreSQL / Supabase (or embedded H2 for local tests).
- Web: Node 20+, npm 9+, Next.js 14.
- Mobile: JDK 17 recommended; Android SDK installed; Gradle wrapper present in repository.

## 8. Test Cases (Representative)
- Auth
  - TC-A1: Register new user — POST /api/auth/register — expect success, valid user and tokens.
  - TC-A2: Login with email — POST /api/auth/login — expect token and user payload.
  - TC-A3: Health check — GET /api/auth/health — expect 200 + success message.

- Menu
  - TC-M1: GET /api/menu-items — expect list of available items.
  - TC-M2: Menu filtering by category — query param `category` returns subset.

- Cart
  - TC-C1: Create/get cart for user — GET /api/carts/{userId} — expect cart structure.
  - TC-C2: Add cart item — POST /api/carts/{userId}/items — expect updated cart and item counts.
  - TC-C3: Update cart item — PUT /api/carts/{userId}/items/{cartItemId} — expect quantity updates.
  - TC-C4: Remove cart item — DELETE /api/carts/{userId}/items/{cartItemId} — expect removal.

- Orders
  - TC-O1: Checkout cart — POST /api/orders/{userId}/checkout — expect order record and order number.
  - TC-O2: Get user orders — GET /api/orders/{userId} — expect list including newly placed order.

## 9. Test Data
- Use seeded menu via `MenuSeedConfig` (already present). Create test user(s) via API register endpoints.

## 10. Test Tools & Scripts
- Backend: Maven (`mvn test`)
- Web: Node/npm (`npm run build`)
- Mobile: Gradle wrapper (`gradlew -p mobile assembleDebug`)
- API sanity: `curl` or HTTP client (HTTPie, Postman), and example scripts in `docs/` if desired.

## 11. Responsibilities
- Assistant (automated): Run builds/tests, generate reports, fix straightforward refactor issues.
- User/Developer: Provide JDK 17 on CI agent or local machine for mobile build if needed.

## 12. Risks & Mitigations
- Risk: Host JDK version incompatible with Kotlin/Gradle -> Mitigation: set JAVA_HOME to JDK 17 or run mobile build in CI with appropriate toolchain.

## 13. Schedule
- Immediate: Run backend tests and web build (completed). Mobile build attempted; failed due to local Java version mismatch (see regression report).

---

End of Test Plan.
