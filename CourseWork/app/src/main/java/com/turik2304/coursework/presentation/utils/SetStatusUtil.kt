package com.turik2304.coursework.presentation.utils

import android.widget.ImageView
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.data.network.models.data.StatusEnum

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
                this.text = resources.getString(R.string.status_active)
            }
            StatusEnum.IDLE -> {
                this.setTextColor(
                    resources.getColor(
                        R.color.yellow_status_idle,
                        context?.theme
                    )
                )
                this.text = resources.getString(R.string.status_idle)
            }
            else -> {
                this.setTextColor(
                    resources.getColor(
                        R.color.red_status_offline,
                        context?.theme
                    )
                )
                this.text = resources.getString(R.string.status_offline)
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