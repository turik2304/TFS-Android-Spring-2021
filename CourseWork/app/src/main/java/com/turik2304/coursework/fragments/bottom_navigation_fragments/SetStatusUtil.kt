package com.turik2304.coursework.fragments.bottom_navigation_fragments

import android.widget.TextView
import com.turik2304.coursework.R

object SetStatusUtil {

    fun setColoredStatus(textViewStatus: TextView) {
        val resources = textViewStatus.resources
        val context = textViewStatus.context
        when (textViewStatus.text) {
            "active" -> {
                textViewStatus.setTextColor(
                    resources.getColor(
                        R.color.green_status_online,
                        context?.theme
                    )
                )
            }
            "idle" -> {
                textViewStatus.setTextColor(
                    resources.getColor(
                        R.color.yellow_status_idle,
                        context?.theme
                    )
                )
            }
            else -> {
                textViewStatus.setTextColor(
                    resources.getColor(
                        R.color.red_status_offline,
                        context?.theme
                    )
                )
            }
        }
    }
}