package edu.cit.canadilla.wildcatslounge.mobile.feature.order.data

import edu.cit.canadilla.wildcatslounge.mobile.core.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.core.model.OrderData
import retrofit2.http.GET
import retrofit2.http.POST

interface OrderApiService {
	@GET("auth/orders/my")
	suspend fun getOrders(): ApiResponse<List<OrderData>>

	@POST("auth/orders")
	suspend fun placeOrder(): ApiResponse<OrderData>
}
