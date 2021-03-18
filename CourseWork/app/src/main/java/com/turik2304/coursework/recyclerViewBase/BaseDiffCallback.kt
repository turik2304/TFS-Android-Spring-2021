package com.turik2304.coursework.recyclerViewBase

import androidx.recyclerview.widget.DiffUtil

open class BaseDiffCallback<T : ViewTyped> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return (oldItem.viewType == newItem.viewType)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.uid == newItem.uid
    }


}