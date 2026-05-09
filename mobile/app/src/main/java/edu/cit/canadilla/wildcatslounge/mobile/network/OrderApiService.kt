package edu.cit.canadilla.wildcatslounge.mobile.network

import edu.cit.canadilla.wildcatslounge.mobile.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.model.OrderData
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApiService {
    @GET("orders/{userId}")
    suspend fun getOrders(@Path("userId") userId: Long): ApiResponse<List<OrderData>>

    @POST("orders/{userId}/checkout")
    suspend fun checkoutCart(@Path("userId") userId: Long): ApiResponse<OrderData>
}
