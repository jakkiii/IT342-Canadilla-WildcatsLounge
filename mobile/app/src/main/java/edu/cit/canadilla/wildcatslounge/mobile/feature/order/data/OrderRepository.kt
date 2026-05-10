package edu.cit.canadilla.wildcatslounge.mobile.feature.order.data

import edu.cit.canadilla.wildcatslounge.mobile.core.network.RetrofitClient

class OrderRepository {
    suspend fun getOrders(userId: Long) = RetrofitClient.orderApiService.getOrders(userId)

    suspend fun checkoutCart(userId: Long) = RetrofitClient.orderApiService.checkoutCart(userId)
}

