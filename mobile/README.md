# Wildcats Lounge Mobile

Native Android app for the student-facing Wildcats Lounge experience, aligned with the web student portal.

## Included Student Flow

- Login (email or student ID) with session restore
- Registration with email verification code (send code → verify → auto sign-in)
- Home dashboard: lounge occupancy, active order, featured menu, today's events
- Menu: All / Coffees / Drinks / Treats tabs, customization sheet (serving, sugar, add-ons, notes, quantity)
- Cart with grouped add-ons and checkout
- My Orders screen (auto-refresh every 5 seconds)
- Events list
- Profile (name, email, student ID, role) and logout

The app uses the same Spring Boot `/api/auth/*` routes as the web student app.

## Run Locally

1. Start the backend from `backend/` (port `8080`).
2. Open `mobile/` in Android Studio and sync Gradle.
3. Run on an emulator (recommended) or a physical device.

## API Base URL

Debug and release builds default to:

```text
http://10.0.2.2:8080/api/
```

`10.0.2.2` is the emulator alias for your machine's `localhost`. For a **physical device**, change `API_BASE_URL` in `app/build.gradle.kts` to your computer's LAN IP, e.g. `http://192.168.1.10:8080/api/`, and ensure the device is on the same network.

Bearer tokens are attached automatically via `AuthTokenProvider` after login or registration.
