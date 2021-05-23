package com.turik2304.coursework.presentation.recycler_view

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.turik2304.coursework.presentation.recycler_view.base.BaseAdapter
import com.turik2304.coursework.presentation.recycler_view.base.HolderFactory
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

class AsyncAdapter<T : ViewTyped>(
    holderFactory: HolderFactory,
    diffCallback: DiffUtil.ItemCallback<T>
) :
    BaseAdapter<T>(holderFactory) {

    private val localItems = AsyncListDiffer(this, diffCallback)

    override var items: List<T>
        get() = localItems.currentList
        set(newItems) {
            localItems.submitList(newItems)
        }

    fun setItemsWithCommitCallback(
        newItems: List<T>,
        runnable: Runnable
    ) {
        localItems.submitList(newItems) {
            runnable.run()
        }
    }
}