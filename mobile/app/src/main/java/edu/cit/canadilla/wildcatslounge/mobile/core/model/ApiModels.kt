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
	val studentId: String
)

data class RegisterVerifyRequest(
	val email: String,
	val password: String,
	val firstname: String,
	val lastname: String,
	val studentId: String,
	val verificationCode: String
)

data class LoginRequest(
	val identifier: String,
	val password: String
)

enum class ServingType {
	HOT,
	ICED,
	BLENDED,
	NONE
}

data class LoungeStatusData(
	val occupancyLevel: String,
	val displayLabel: String,
	val color: String,
	val lastUpdatedAt: String
)

data class EventData(
	val id: Long,
	val title: String,
	val description: String?,
	val postLink: String?,
	val startDatetime: String,
	val endDatetime: String
)

data class MenuItemData(
	val id: Long,
	val name: String,
	val description: String?,
	val category: String,
	val price: Double,
	val isAvailable: Boolean,
	val imageUrl: String?,
	val allowHot: Boolean = false,
	val allowIced: Boolean = false,
	val allowBlended: Boolean = false,
	val allowAddons: Boolean = false,
	val allowSugarLevel: Boolean = false
)

data class AddCartItemRequest(
	val menuItemId: Long,
	val quantity: Int,
	val servingType: ServingType? = null,
	val customizationNotes: String? = null,
	val sugarLevelPercent: Int? = null,
	val addonIds: List<Long>? = null
)

data class UpdateCartItemQuantityRequest(
	val quantity: Int
)

data class CartItemData(
	val id: Long,
	val menuItemId: Long,
	val itemName: String,
	val unitPrice: Double,
	val quantity: Int,
	val customizationNotes: String?,
	val parentItemId: Long? = null,
	val servingType: String? = null,
	val sugarLevelPercent: Int? = null,
	val lineTotal: Double
)

data class CartData(
	val id: Long = 0,
	val cartId: Long? = null,
	val subtotal: Double,
	val items: List<CartItemData>
) {
	fun resolvedId(): Long = cartId ?: id
}

data class OrderItemData(
	val id: Long,
	val menuItemId: Long? = null,
	val itemName: String,
	val quantity: Int,
	val priceAtPurchase: Double,
	val customizationNotes: String?,
	val parentItemId: Long? = null,
	val servingType: String? = null,
	val sugarLevelPercent: Int? = null
)

data class OrderData(
	val id: Long,
	val orderNumber: String,
	val status: String,
	val totalAmount: Double,
	val createdAt: String,
	val updatedAt: String?,
	val customerName: String?,
	val customerEmail: String? = null,
	val customerStudentId: String? = null,
	val items: List<OrderItemData>
)

data class GroupedCartItem(
	val main: CartItemData,
	val addons: MutableList<CartItemData>
)

data class GroupedOrderItem(
	val main: OrderItemData,
	val addons: MutableList<OrderItemData>
)
