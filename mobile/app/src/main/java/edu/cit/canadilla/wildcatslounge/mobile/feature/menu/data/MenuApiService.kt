package edu.cit.canadilla.wildcatslounge.mobile.feature.menu.data

import edu.cit.canadilla.wildcatslounge.mobile.core.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.core.model.MenuItemData
import retrofit2.http.GET

interface MenuApiService {
    @GET("auth/menu")
    suspend fun getMenuItems(): ApiResponse<List<MenuItemData>>
}

