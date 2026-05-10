package edu.cit.canadilla.wildcatslounge.mobile.core.util

object InputValidators {
	fun isValidEmail(value: String): Boolean =
		value.contains("@") && value.contains(".") && value.length <= 100

	fun isValidStudentId(value: String): Boolean =
		value.matches(Regex("^\\d{2}-\\d{4}-\\d{3}$"))
}
