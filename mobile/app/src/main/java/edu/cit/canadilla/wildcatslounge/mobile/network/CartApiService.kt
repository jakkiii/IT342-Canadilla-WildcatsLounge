package edu.cit.canadilla.wildcatslounge.mobile.network

import edu.cit.canadilla.wildcatslounge.mobile.model.AddCartItemRequest
import edu.cit.canadilla.wildcatslounge.mobile.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.model.CartData
import edu.cit.canadilla.wildcatslounge.mobile.model.UpdateCartItemRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApiService {
    @GET("carts/{userId}")
    suspend fun getCart(@Path("userId") userId: Long): ApiResponse<CartData>

    @POST("carts/{userId}/items")
    suspend fun addCartItem(
        @Path("userId") userId: Long,
        @Body payload: AddCartItemRequest
    ): ApiResponse<CartData>

    @PUT("carts/{userId}/items/{cartItemId}")
    suspend fun updateCartItem(
        @Path("userId") userId: Long,
        @Path("cartItemId") cartItemId: Long,
        @Body payload: UpdateCartItemRequest
    ): ApiResponse<CartData>

    @DELETE("carts/{userId}/items/{cartItemId}")
    suspend fun removeCartItem(
        @Path("userId") userId: Long,
        @Path("cartItemId") cartItemId: Long
    ): ApiResponse<CartData>
}
