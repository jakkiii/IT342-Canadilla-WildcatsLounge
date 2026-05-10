package edu.cit.canadilla.wildcatslounge.mobile.feature.cart.data

import edu.cit.canadilla.wildcatslounge.mobile.core.model.AddCartItemRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.model.UpdateCartItemRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.network.RetrofitClient

class CartRepository {
    suspend fun getCart(userId: Long) = RetrofitClient.cartApiService.getCart(userId)

    suspend fun addCartItem(userId: Long, payload: AddCartItemRequest) =
        RetrofitClient.cartApiService.addCartItem(userId, payload)

    suspend fun updateCartItem(userId: Long, cartItemId: Long, payload: UpdateCartItemRequest) =
        RetrofitClient.cartApiService.updateCartItem(userId, cartItemId, payload)

    suspend fun removeCartItem(userId: Long, cartItemId: Long) =
        RetrofitClient.cartApiService.removeCartItem(userId, cartItemId)
}

