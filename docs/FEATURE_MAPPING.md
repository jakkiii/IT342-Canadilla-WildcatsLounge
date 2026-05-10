# Feature Mapping — Wildcats Lounge

Generated: 2026-05-10
Branch: vertical-slice-refactor

## Backend (feature slices)
Location: `backend/src/main/java/edu/cit/canadilla/wildcatslounge/feature/`

- auth
  - AuthController.java (controller)
  - UserService.java (service)
  - User.java (entity)
  - LoginRequest.java (dto)
- menu
  - MenuController.java
  - MenuService.java
  - MenuItem.java (entity)
  - MenuSeedConfig.java
- cart
  - CartController.java
  - CartService.java
  - Cart.java (entity)
  - CartItem.java
- order
  - OrderController.java
  - OrderService.java
  - Order.java (entity)

(Representative backend files discovered under `backend/src/main/java/.../feature/`.)

## Web (feature modules)
Location: `web/features/`

- shared
  - api.ts
  - types.ts
- auth
  - api.ts
- menu
  - api.ts
- cart
  - api.ts
- order
  - api.ts

(Representative web files discovered under `web/features/`.)

## Mobile (feature packages + core)
Location: `mobile/app/src/main/java/edu/cit/canadilla/wildcatslounge/`

- core
  - core/network/RetrofitClient.kt
  - core/model/ApiModels.kt
  - core/util/SessionManager.kt
  - core/util/InputValidators.kt
- feature/auth
  - auth/data/AuthRepository.kt
  - auth/data/AuthApiService.kt
  - auth/ui/LoginActivity.kt
  - auth/ui/RegisterActivity.kt
- feature/menu
  - menu/data/MenuRepository.kt
  - menu/data/MenuApiService.kt
  - menu/ui/MenuFragment.kt
  - menu/ui/adapter/MenuAdapter.kt
- feature/cart
  - cart/data/CartRepository.kt
  - cart/data/CartApiService.kt
  - cart/ui/CartFragment.kt
  - cart/ui/adapter/CartAdapter.kt
- feature/order
  - order/data/OrderRepository.kt
  - order/data/OrderApiService.kt
- feature/profile
  - profile/ui/ProfileFragment.kt
- feature/dashboard
  - dashboard/ui/DashboardActivity.kt
- feature/home
  - home/ui/HomeFragment.kt
- feature/events
  - events/ui/EventsFragment.kt

(Representative mobile files discovered under `mobile/app/src/main/java/.../feature/` and `.../core/`.)

## Notes
- This mapping lists representative files found during an automated scan; it is not exhaustive but highlights primary feature folders and key files.
- To generate a complete per-file inventory, run a workspace file search or request a CSV listing.

End of mapping.
