package com.turik2304.coursework.presentation.recycler_view.base

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T : ViewTyped>(internal var holderFactory: HolderFactory) :
    RecyclerView.Adapter<BaseViewHolder<ViewTyped>>() {

    abstract var items: List<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> =
        holderFactory.invoke(parent, viewType)

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewTyped>, position: Int) {
        holder.bind(items[position])
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ViewTyped>,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isNotEmpty()) {
            holder.bind(items[position], payloads)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int = items.size

}
