package com.turik2304.coursework.presentation.recycler_view

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

class DiffCallback<T : ViewTyped> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return (oldItem.uid == newItem.uid)
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }


}