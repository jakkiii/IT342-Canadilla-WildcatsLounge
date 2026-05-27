package edu.cit.canadilla.wildcatslounge.mobile.feature.order.data

import edu.cit.canadilla.wildcatslounge.mobile.core.network.RetrofitClient
import edu.cit.canadilla.wildcatslounge.mobile.core.network.safeApiCall

class OrderRepository {
	suspend fun getOrders() = safeApiCall {
		RetrofitClient.orderApiService.getOrders()
	}

	suspend fun placeOrder() = safeApiCall {
		RetrofitClient.orderApiService.placeOrder()
	}
}
