package com.hsdroid.openinappassignment.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class Helper {
    companion object {

        fun showToast(context: Context?, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        fun setViewsVisibility(visibility: Int, vararg views: View) {
            views.forEach { it.visibility = visibility }
        }

        fun createGradientDrawable(startColor: Int, endColor: Int): GradientDrawable {
            return GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(startColor, endColor)
            )
        }

        @SuppressLint("SimpleDateFormat")
        fun convertTimeStampToReadableTime(timeStamp: String?): String {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val formatter = SimpleDateFormat("dd MMM yyyy")
            return formatter.format(parser.parse(timeStamp!!)!!)
        }

        fun getGreetingMessage(): String {
            val calendar = Calendar.getInstance()
            return when (calendar.get(Calendar.HOUR_OF_DAY)) {
                in 0..4 -> "Good Night"
                in 5..11 -> "Good Morning"
                in 12..15 -> "Good Afternoon"
                in 16..20 -> "Good Evening"
                else -> "Good Night"
            }
        }
    }
}