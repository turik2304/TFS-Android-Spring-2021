package com.turik2304.coursework.presentation.recycler_view.base

import androidx.recyclerview.widget.RecyclerView

interface RecyclerBuilder<T : ViewTyped> {
    val itemDecoration: MutableList<RecyclerView.ItemDecoration>
    val adapter: BaseAdapter<T>
    val layoutManager: RecyclerView.LayoutManager?
    var hasFixedSize: Boolean

    fun build(recyclerView: RecyclerView): Recycler<T>
}