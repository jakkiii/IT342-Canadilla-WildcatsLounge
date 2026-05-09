package edu.cit.canadilla.wildcatslounge.mobile.repository

import edu.cit.canadilla.wildcatslounge.mobile.network.RetrofitClient

class OrderRepository {
    suspend fun getOrders(userId: Long) = RetrofitClient.orderApiService.getOrders(userId)

    suspend fun checkoutCart(userId: Long) = RetrofitClient.orderApiService.checkoutCart(userId)
}
