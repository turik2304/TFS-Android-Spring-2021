package com.turik2304.coursework.fragments.bottom_navigation_fragments

import android.widget.ImageView
import android.widget.TextView
import com.turik2304.coursework.R

object SetStatusUtil {

    fun setColoredTextStatus(textViewStatus: TextView) {
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

    fun setColoredImageStatus(status: String, imageView: ImageView) {
        when (status) {
            "active" -> {
                imageView.setImageResource(R.drawable.ic_status_online)
            }
            "idle" -> {
                imageView.setImageResource(R.drawable.ic_status_idle)
            }
            else -> {
                imageView.setImageResource(R.drawable.ic_status_offline)
            }
        }
    }
}