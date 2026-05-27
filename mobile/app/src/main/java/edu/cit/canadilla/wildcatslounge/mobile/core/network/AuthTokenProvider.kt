package edu.cit.canadilla.wildcatslounge.mobile.core.network

object AuthTokenProvider {
	@Volatile
	private var tokenSupplier: (() -> String?)? = null

	fun install(supplier: () -> String?) {
		tokenSupplier = supplier
	}

	fun getBearerToken(): String? = tokenSupplier?.invoke()
}
