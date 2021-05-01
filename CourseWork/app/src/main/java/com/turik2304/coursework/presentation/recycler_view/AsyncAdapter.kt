package com.turik2304.coursework.presentation.recycler_view

import androidx.recyclerview.widget.AsyncListDiffer
import com.turik2304.coursework.presentation.recycler_view.base.BaseAdapter
import com.turik2304.coursework.presentation.recycler_view.base.HolderFactory
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

class AsyncAdapter<T : ViewTyped>(holderFactory: HolderFactory, diffCallback: DiffCallback<T>) :
    BaseAdapter<T>(holderFactory) {

    private val localItems = AsyncListDiffer(this, diffCallback)

    override var items: AsyncListDiffer<T>
        get() = localItems
        set(newItems) {
            localItems.submitList(newItems.currentList)
        }


}