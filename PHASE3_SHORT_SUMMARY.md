# IT342 Phase 3 - Web Main Feature Short Summary

## 1. Description of the Main Feature
The main feature is the Wildcats Lounge web ordering system. Users can browse menu items by category, choose serving type (Hot/Iced/Blended where available), add items to cart, update quantities, and place an order for pickup.

## 2. Inputs and Validations Used
### Frontend inputs
- User session data (user id from login/session)
- Menu item selection
- Serving type selection (for items with variants)
- Quantity updates in cart
- Checkout action

### Backend request inputs
- `menuItemId` (required)
- `quantity` (required, minimum value = 1)
- `servingType` (required for items that have serving variants)
- `customizationNotes` (optional, max 255 characters)

### Validations and error handling
- Prevent checkout when cart is empty
- Reject unavailable menu items
- Reject invalid/missing serving type for variant items
- Return structured API error responses for invalid input or runtime issues
- Show success and error messages in the web UI

## 3. How the Feature Works
1. User logs in and opens the dashboard.
2. Frontend requests menu items, current cart, and recent orders.
3. User picks an item (and serving type when applicable) and adds it to cart.
4. User can increase/decrease quantity or remove items.
5. At checkout, backend creates an order from cart items, stores order details, and clears the cart.
6. Frontend refreshes cart/order state and displays confirmation.

## 4. API Endpoints Used
### Authentication and health
- `GET /api/auth/health`

### Menu
- `GET /api/menu-items`

### Cart
- `GET /api/carts/{userId}`
- `POST /api/carts/{userId}/items`
- `PUT /api/carts/{userId}/items/{cartItemId}`
- `DELETE /api/carts/{userId}/items/{cartItemId}`

### Orders
- `POST /api/orders/{userId}/checkout`
- `GET /api/orders/{userId}`

## 5. Database Tables Involved
- `users`
- `menu_items`
- `carts`
- `cart_items`
- `orders`
- `order_items`
