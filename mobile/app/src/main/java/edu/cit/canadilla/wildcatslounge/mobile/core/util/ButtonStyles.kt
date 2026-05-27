package edu.cit.canadilla.wildcatslounge.mobile.core.util

import android.widget.Button
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import edu.cit.canadilla.wildcatslounge.mobile.R

fun Chip.applyChoiceChipStyle() {
	backgroundTintList = null
	chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.chip_choice_bg)
	chipStrokeColor = ContextCompat.getColorStateList(context, R.color.chip_choice_stroke)
	chipStrokeWidth = context.resources.getDimension(R.dimen.chip_stroke_width)
	setTextColor(ContextCompat.getColorStateList(context, R.color.chip_choice_text))
	isCheckedIconVisible = false
	checkedIcon = null
	chipIcon = null
	isChipIconVisible = false
	chipCornerRadius = context.resources.getDimension(R.dimen.radius_chip)
}

fun Button.applyPrimaryStyle() {
	backgroundTintList = null
	setBackgroundResource(R.drawable.bg_primary_button)
	setTextColor(ContextCompat.getColor(context, R.color.white))
}

fun Button.applyOutlinedStyle() {
	backgroundTintList = null
	setBackgroundResource(R.drawable.bg_filter_tab_unselected)
	setTextColor(ContextCompat.getColor(context, R.color.wildcats_blue))
}
