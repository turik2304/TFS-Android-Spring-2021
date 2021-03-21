package com.turik2304.coursework.recycler_view_base

import androidx.recyclerview.widget.AsyncListDiffer

class AsyncAdapter<T : ViewTyped>(holderFactory: HolderFactory, diffCallback: BaseDiffCallback<T>) :
    BaseAdapter<T>(holderFactory) {

    private val localItems = AsyncListDiffer(this, diffCallback)

    override var items: AsyncListDiffer<T>
        get() = localItems
        set(newItems) {
            localItems.submitList(newItems.currentList)
        }


}