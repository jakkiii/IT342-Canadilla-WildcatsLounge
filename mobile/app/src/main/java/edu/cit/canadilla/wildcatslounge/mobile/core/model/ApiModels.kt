package edu.cit.canadilla.wildcatslounge.mobile.core.model

data class ApiResponse<T>(
	val success: Boolean,
	val data: T? = null,
	val error: String? = null,
	val timestamp: String? = null
)

data class AuthResponse(
	val user: UserData,
	val accessToken: String,
	val refreshToken: String
)

data class UserData(
	val id: Long,
	val email: String,
	val firstname: String,
	val lastname: String,
	val studentId: String?,
	val role: String?
)

data class RegisterRequest(
	val email: String,
	val password: String,
	val firstname: String,
	val lastname: String,
	val studentId: String?
)

data class LoginRequest(
	val identifier: String,
	val password: String
)

enum class MenuCategory {
	COFFEE,
	FLAVORED_LATTE,
	MATCHA_SERIES,
	BEVERAGES,
	COFFEE_ADD_ON
}

enum class ServingType {
	HOT,
	ICED,
	BLENDED,
	NONE
}

data class MenuItemData(
	val id: Long,
	val name: String,
	val description: String,
	val category: MenuCategory,
	val price: Double,
	val hotPrice: Double?,
	val icedPrice: Double?,
	val blendedPrice: Double?,
	val availableServingTypes: List<ServingType>?,
	val isAvailable: Boolean,
	val imageUrl: String?
)

data class AddCartItemRequest(
	val menuItemId: Long,
	val quantity: Int,
	val servingType: ServingType,
	val customizationNotes: String
)

data class UpdateCartItemRequest(
	val quantity: Int,
	val servingType: ServingType,
	val customizationNotes: String
)

data class CartItemData(
	val id: Long,
	val menuItemId: Long,
	val itemName: String,
	val category: MenuCategory,
	val unitPrice: Double,
	val quantity: Int,
	val servingType: ServingType,
	val customizationNotes: String?,
	val imageUrl: String?,
	val lineTotal: Double
)

data class CartData(
	val cartId: Long,
	val userId: Long,
	val itemCount: Int,
	val subtotal: Double,
	val updatedAt: String,
	val items: List<CartItemData>
)

enum class OrderStatus {
	PENDING,
	PREPARING,
	READY,
	COMPLETED
}

data class OrderItemData(
	val id: Long,
	val menuItemId: Long,
	val itemName: String,
	val quantity: Int,
	val priceAtPurchase: Double,
	val servingType: ServingType,
	val customizationNotes: String?,
	val lineTotal: Double
)

data class OrderData(
	val id: Long,
	val orderNumber: String,
	val userId: Long,
	val status: OrderStatus,
	val totalAmount: Double,
	val createdAt: String,
	val items: List<OrderItemData>
)
