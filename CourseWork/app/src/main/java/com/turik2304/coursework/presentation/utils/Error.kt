package com.turik2304.coursework.presentation.utils

import android.content.Context
import android.widget.Toast

object Error {

    fun showError(context: Context?, error: Throwable) {
        if (context != null) {
            Toast.makeText(context, "Something wrong. Error: $error", Toast.LENGTH_SHORT).show()
        }
    }

}