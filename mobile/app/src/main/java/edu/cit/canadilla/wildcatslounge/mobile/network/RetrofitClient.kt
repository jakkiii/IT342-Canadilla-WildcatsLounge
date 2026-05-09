package edu.cit.canadilla.wildcatslounge.mobile.network

import edu.cit.canadilla.wildcatslounge.mobile.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
	private val loggingInterceptor = HttpLoggingInterceptor().apply {
		level = HttpLoggingInterceptor.Level.BODY
	}

	private val okHttpClient = OkHttpClient.Builder()
		.addInterceptor(loggingInterceptor)
		.build()

	private val retrofit: Retrofit = Retrofit.Builder()
		.baseUrl(BuildConfig.API_BASE_URL)
		.addConverterFactory(GsonConverterFactory.create())
		.client(okHttpClient)
		.build()

	val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
	val menuApiService: MenuApiService = retrofit.create(MenuApiService::class.java)
	val cartApiService: CartApiService = retrofit.create(CartApiService::class.java)
	val orderApiService: OrderApiService = retrofit.create(OrderApiService::class.java)
}
