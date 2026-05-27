package edu.cit.canadilla.wildcatslounge.mobile.feature.cart.data

import edu.cit.canadilla.wildcatslounge.mobile.core.model.AddCartItemRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.core.model.CartData
import edu.cit.canadilla.wildcatslounge.mobile.core.model.UpdateCartItemQuantityRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApiService {
	@GET("auth/cart")
	suspend fun getCart(): ApiResponse<CartData>

	@POST("auth/cart/items")
	suspend fun addCartItem(@Body payload: AddCartItemRequest): ApiResponse<CartData>

	@PUT("auth/cart/items/{cartItemId}")
	suspend fun updateCartItem(
		@Path("cartItemId") cartItemId: Long,
		@Body payload: UpdateCartItemQuantityRequest
	): ApiResponse<CartData>

	@DELETE("auth/cart/items/{cartItemId}")
	suspend fun removeCartItem(@Path("cartItemId") cartItemId: Long): ApiResponse<CartData>
}
