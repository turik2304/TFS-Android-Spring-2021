package com.turik2304.coursework.fragments.bottom_navigation_fragments

import android.widget.ImageView
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.network.models.data.StatusEnum

object SetStatusUtil {

    fun TextView.setColoredTextStatus(status: StatusEnum) {
        val resources = this.resources
        val context = this.context
        when (status) {
            StatusEnum.ACTIVE -> {
                this.setTextColor(
                    resources.getColor(
                        R.color.green_status_online,
                        context?.theme
                    )
                )
            }
            StatusEnum.IDLE -> {
                this.setTextColor(
                    resources.getColor(
                        R.color.yellow_status_idle,
                        context?.theme
                    )
                )
            }
            else -> {
                this.setTextColor(
                    resources.getColor(
                        R.color.red_status_offline,
                        context?.theme
                    )
                )
            }
        }
    }

    fun ImageView.setColoredImageStatus(status: StatusEnum) {
        when (status) {
            StatusEnum.ACTIVE -> {
                this.setImageResource(R.drawable.ic_status_online)
            }
            StatusEnum.IDLE -> {
                this.setImageResource(R.drawable.ic_status_idle)
            }
            else -> {
                this.setImageResource(R.drawable.ic_status_offline)
            }
        }
    }
}