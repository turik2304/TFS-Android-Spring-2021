package com.turik2304.coursework.presentation.recycler_view

import com.turik2304.coursework.presentation.recycler_view.base.BaseAdapter
import com.turik2304.coursework.presentation.recycler_view.base.HolderFactory
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

class Adapter<T : ViewTyped>(holderFactory: HolderFactory) :
    BaseAdapter<T>(holderFactory) {

    private val localItems = mutableListOf<T>()

    override var items: List<T>
        get() = localItems
        set(newItems) {
            localItems.clear()
            localItems.addAll(newItems)
            notifyDataSetChanged()
        }
}