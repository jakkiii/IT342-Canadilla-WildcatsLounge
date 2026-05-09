package edu.cit.canadilla.wildcatslounge.mobile.network

import edu.cit.canadilla.wildcatslounge.mobile.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.model.MenuItemData
import retrofit2.http.GET
import retrofit2.http.Query

interface MenuApiService {
    @GET("menu-items")
    suspend fun getMenuItems(@Query("category") category: String? = null): ApiResponse<List<MenuItemData>>
}
