package com.turik2304.coursework.extensions

import androidx.annotation.Px
import com.turik2304.coursework.MyApp
import kotlin.math.roundToInt

@Px
fun Float.spToPx(): Int {
    val res = MyApp.app.applicationContext.resources
    return (this * res.displayMetrics.scaledDensity).roundToInt()
}

@Px
fun Float.dpToPx(): Float {
    val res = MyApp.app.applicationContext.resources
    return (this * res.displayMetrics.density)
}