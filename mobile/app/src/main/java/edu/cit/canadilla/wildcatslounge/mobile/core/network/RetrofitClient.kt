package edu.cit.canadilla.wildcatslounge.mobile.core.network

import edu.cit.canadilla.wildcatslounge.mobile.BuildConfig
import edu.cit.canadilla.wildcatslounge.mobile.feature.auth.data.AuthApiService
import edu.cit.canadilla.wildcatslounge.mobile.feature.cart.data.CartApiService
import edu.cit.canadilla.wildcatslounge.mobile.feature.events.data.EventApiService
import edu.cit.canadilla.wildcatslounge.mobile.feature.lounge.data.LoungeApiService
import edu.cit.canadilla.wildcatslounge.mobile.feature.menu.data.MenuApiService
import edu.cit.canadilla.wildcatslounge.mobile.feature.order.data.OrderApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
	private val authInterceptor = Interceptor { chain ->
		val requestBuilder = chain.request().newBuilder()
		AuthTokenProvider.getBearerToken()?.let { token ->
			requestBuilder.header("Authorization", "Bearer $token")
		}
		chain.proceed(requestBuilder.build())
	}

	private val loggingInterceptor = HttpLoggingInterceptor().apply {
		level = if (BuildConfig.DEBUG) {
			HttpLoggingInterceptor.Level.BODY
		} else {
			HttpLoggingInterceptor.Level.NONE
		}
	}

	private val okHttpClient = OkHttpClient.Builder()
		.addInterceptor(authInterceptor)
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
	val eventApiService: EventApiService = retrofit.create(EventApiService::class.java)
	val loungeApiService: LoungeApiService = retrofit.create(LoungeApiService::class.java)
}
