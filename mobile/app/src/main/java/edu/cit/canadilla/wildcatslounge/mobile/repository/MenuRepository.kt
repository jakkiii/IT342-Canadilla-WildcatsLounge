package edu.cit.canadilla.wildcatslounge.mobile.repository

import edu.cit.canadilla.wildcatslounge.mobile.network.RetrofitClient

class MenuRepository {
    suspend fun getMenuItems(category: String? = null) = RetrofitClient.menuApiService.getMenuItems(category)
}
