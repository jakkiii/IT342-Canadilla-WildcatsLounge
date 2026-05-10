# Regression Test Report — Wildcats Lounge

**Branch:** vertical-slice-refactor
**Date:** 2026-05-10
**Scope:** Backend, Web, Mobile (refactor + verification)

## 1. Summary
This report records the results of the regression checks executed after the vertical-slice refactor. The latest CI-like run completed successfully for backend tests, web build, and mobile assemble.

## 2. Environment
- OS: Windows (user workstation)
- Backend: Maven 3.9.11, Java runtime detected in shell (used by `mvn` successfully)
- Web: Node/Next — Next.js 14 build completed successfully
- Mobile: Gradle wrapper present; JDK 17 was used for the successful assemble run

## 3. Actions & Results
- Backend
  - Command: `mvn -f backend/pom.xml test`
  - Result: BUILD SUCCESS — all tests (if any) passed.
  - Artifact: `artifacts/ci-runs/20260510-020353/backend-test.log`

- Web
  - Command: `cd web; npm run build`
  - Result: Build succeeded; Next.js production build generated; static pages prerendered.
  - Artifact: `artifacts/ci-runs/20260510-020353/web-build.log`

- Mobile
  - Command: `D:\Documents\WildcatsLounge\mobile\gradlew.bat assembleDebug`
  - Result: BUILD SUCCESS.
  - Artifact: `artifacts/ci-runs/20260510-020353/mobile-assemble.log`
  - Java home used: `C:\Program Files\Java\jdk-17`

## 4. Failures & Diagnostic Logs
- No blocking failures in the latest run.

## 5. Impact Assessment
- Backend, web, and mobile all validated in the latest run; the refactor is build-stable across the three target surfaces.

## 6. Remediation & Recommendations
- Keep the CI-like artifact run under version control for future regression comparisons.
- Add endpoint-level tests for auth, cart, and checkout flows if broader functional coverage is still desired.

## 7. Evidence & Attachments
- Artifact directory: `artifacts/ci-runs/20260510-020353`
- Run info: `artifacts/ci-runs/20260510-020353/run-info.txt`
- Backend log: `artifacts/ci-runs/20260510-020353/backend-test.log`
- Web log: `artifacts/ci-runs/20260510-020353/web-build.log`
- Mobile log: `artifacts/ci-runs/20260510-020353/mobile-assemble.log`

## 8. Next Steps (recommended)
- Convert this markdown to PDF when ready for submission.
- Re-run the validation with the reusable script in [scripts/validate-regression.ps1](scripts/validate-regression.ps1).

## 9. Reproducible Validation
Run the full backend/web/mobile sequence from the repository root with:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\validate-regression.ps1 -JavaHome "C:\Program Files\Java\jdk-17"
```

The script writes a fresh timestamped artifact folder under `artifacts/ci-runs/` and emits `summary.md` and `summary.json` for submission review.

---

## Part 5 — Full Regression Test Report

**Project Information**
- **Project:** Wildcats Lounge — IT342-Canadilla-WildcatsLounge
- **Repository:** jakkiii/IT342-Canadilla-WildcatsLounge
- **Branch:** vertical-slice-refactor
- **Date:** 2026-05-10
- **Scope:** Backend (Spring Boot), Web (Next.js), Mobile (Android/Kotlin)

**Refactoring Summary**
- **Goal:** Apply Vertical Slice Architecture across backend, web, and mobile.
- **What changed:** Code reorganized by feature (auth, menu, cart, order, profile, etc.). Shared utilities and cross-cutting concerns moved to `common`/`core` modules.
- **Impact:** Improves feature isolation, reduces cross-module coupling, simplifies testing and ownership.

**Updated Project Structure**
- **Backend:** feature slices under [backend/src/main/java/edu/cit/canadilla/wildcatslounge/feature/](backend/src/main/java/edu/cit/canadilla/wildcatslounge/feature/) (e.g., auth, menu, cart, order).
- **Web:** feature modules under [web/features/](web/features/) (auth, menu, cart, order, shared types/api).
- **Mobile:** feature packages under [mobile/app/src/main/java/.../feature/](mobile/app/src/main/java/edu/cit/canadilla/wildcatslounge/mobile/feature/) and shared core under [mobile/app/src/main/java/.../core/](mobile/app/src/main/java/edu/cit/canadilla/wildcatslounge/mobile/core/).

**Test Plan Documentation**
- **Primary test plan:** see [docs/SOFTWARE_TEST_PLAN.md](docs/SOFTWARE_TEST_PLAN.md) for scope, criteria, and test cases (unit, integration, build validation).
- **Execution commands:** backend tests via `mvn -f backend/pom.xml test`, web build via `cd web && npm run build`, mobile assemble via `mobile\gradlew.bat assembleDebug` (run from repository root by the provided script).

**Automated Test Evidence**
- **Validation script:** [scripts/validate-regression.ps1](scripts/validate-regression.ps1)
- **Artifact folder:** artifacts/ci-runs/<timestamp> (example: [artifacts/ci-runs/20260510-020353](artifacts/ci-runs/20260510-020353)) contains `backend-test.log`, `web-build.log`, `mobile-assemble.log`, `summary.md`, and `summary.json`.
- **Representative artifacts:** [artifacts/ci-runs/20260510-020353/backend-test.log](artifacts/ci-runs/20260510-020353/backend-test.log), [artifacts/ci-runs/20260510-020353/web-build.log](artifacts/ci-runs/20260510-020353/web-build.log), [artifacts/ci-runs/20260510-020353/mobile-assemble.log](artifacts/ci-runs/20260510-020353/mobile-assemble.log).

**Regression Test Results**
- **Backend:** `mvn -f backend/pom.xml test` — BUILD SUCCESS (all executed tests passed).
- **Web:** `cd web && npm run build` — Build succeeded; Next.js production build completed.
- **Mobile:** `mobile\gradlew.bat assembleDebug` — BUILD SUCCESS when run with JDK 17.
- **Overall status:** PASS — build and validation succeeded across backend, web, and mobile for this run. See `summary.md` in the artifact folder for per-stage timings and exit codes.

**Issues Found**
- **Web log redirection error:** initial PowerShell redirection produced a "filename or directory syntax" error when capturing logs; root cause: quoting/redirect differences between PowerShell and cmd.exe. (Status: resolved)
- **Mobile JDK mismatch:** Gradle/Kotlin failed when host Java was an unsupported newer major release; root cause: JDK version (host Java v25 incompatible with Android toolchain). (Status: resolved by switching to JDK 17)

**Fixes Applied**
- **Web logging:** updated CI-like invocation and runner script to use a safe wrapper (`cmd /c` or PowerShell `Start-Process` with `-RedirectStandardOutput`) so `web-build.log` is reliably produced.
- **Mobile JDK:** located and set `JAVA_HOME` to a JDK 17 installation (e.g., `C:\Program Files\Java\jdk-17` or `C:\Users\John Aaron\\.jdks\ms-17.0.18`) before running `gradlew.bat`; updated `scripts/validate-regression.ps1` to accept `-JavaHome` and set the environment for the mobile stage.
- **Artifacts & report:** added `scripts/validate-regression.ps1` and wrote `artifacts/ci-runs/<timestamp>/summary.md` and `summary.json` to capture evidence for submission.

**Submission Notes**
- The full regression report (this document) plus artifact folder `artifacts/ci-runs/20260510-020353` and the validation script constitute the submission package.
- To re-run and reproduce, execute the validation script from the repository root:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\validate-regression.ps1 -JavaHome "C:\Program Files\Java\jdk-17"
```

---

End of Regression Test Report.
