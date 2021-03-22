package com.turik2304.coursework.recycler_view_base.diff_utils

import android.annotation.SuppressLint
import com.turik2304.coursework.recycler_view_base.BaseDiffCallback
import com.turik2304.coursework.recycler_view_base.ViewTyped

class DiffCallbackStreamUI : BaseDiffCallback<ViewTyped>() {

    override fun areItemsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
        return oldItem.uid == newItem.uid
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {

        return oldItem == newItem
    }

}