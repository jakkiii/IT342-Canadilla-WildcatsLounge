package edu.cit.canadilla.wildcatslounge.mobile.feature.menu.data

import edu.cit.canadilla.wildcatslounge.mobile.core.network.RetrofitClient
import edu.cit.canadilla.wildcatslounge.mobile.core.network.safeApiCall

class MenuRepository {
    suspend fun getMenuItems() = safeApiCall {
        RetrofitClient.menuApiService.getMenuItems()
    }
}

