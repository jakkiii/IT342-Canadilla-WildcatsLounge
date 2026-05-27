package edu.cit.canadilla.wildcatslounge.mobile.feature.cart.data

import edu.cit.canadilla.wildcatslounge.mobile.core.model.AddCartItemRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.model.CartData
import edu.cit.canadilla.wildcatslounge.mobile.core.model.UpdateCartItemQuantityRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.network.RetrofitClient
import edu.cit.canadilla.wildcatslounge.mobile.core.network.safeApiCall

class CartRepository {
	suspend fun getCart() = safeApiCall {
		normalizeCart(RetrofitClient.cartApiService.getCart())
	}

	suspend fun addCartItem(payload: AddCartItemRequest) = safeApiCall {
		normalizeCart(RetrofitClient.cartApiService.addCartItem(payload))
	}

	suspend fun updateCartItem(cartItemId: Long, quantity: Int) = safeApiCall {
		normalizeCart(
			RetrofitClient.cartApiService.updateCartItem(
				cartItemId,
				UpdateCartItemQuantityRequest(quantity)
			)
		)
	}

	suspend fun removeCartItem(cartItemId: Long) = safeApiCall {
		normalizeCart(RetrofitClient.cartApiService.removeCartItem(cartItemId))
	}

	private fun normalizeCart(response: edu.cit.canadilla.wildcatslounge.mobile.core.model.ApiResponse<CartData>): edu.cit.canadilla.wildcatslounge.mobile.core.model.ApiResponse<CartData> {
		if (!response.success || response.data == null) return response
		val raw = response.data
		return response.copy(
			data = raw.copy(id = raw.resolvedId())
		)
	}
}
