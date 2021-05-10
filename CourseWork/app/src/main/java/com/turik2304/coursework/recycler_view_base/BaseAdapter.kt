package com.turik2304.coursework.recycler_view_base

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T : ViewTyped>(internal var holderFactory: HolderFactory?) :
    RecyclerView.Adapter<BaseViewHolder<ViewTyped>>() {

    abstract var items: AsyncListDiffer<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> =
        holderFactory?.invoke(parent, viewType) ?: BaseViewHolder(parent)

    override fun getItemViewType(position: Int): Int {
        return items.currentList[position].viewType
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewTyped>, position: Int) {
        holder.bind(items.currentList[position])
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ViewTyped>,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isNotEmpty()) {
            holder.bind(items.currentList[position], payloads)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int = items.currentList.size

}
