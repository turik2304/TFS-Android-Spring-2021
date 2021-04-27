package com.turik2304.coursework.extensions

import com.facebook.shimmer.ShimmerFrameLayout

fun ShimmerFrameLayout.stopAndHideShimmer() {
    stopShimmer()
    hideShimmer()
}

