package edu.cit.canadilla.wildcatslounge.mobile.feature.menu.data

import edu.cit.canadilla.wildcatslounge.mobile.core.network.RetrofitClient

class MenuRepository {
    suspend fun getMenuItems(category: String? = null) = RetrofitClient.menuApiService.getMenuItems(category)
}

