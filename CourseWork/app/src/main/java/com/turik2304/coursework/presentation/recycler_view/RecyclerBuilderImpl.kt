package com.turik2304.coursework.presentation.recycler_view

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.turik2304.coursework.presentation.recycler_view.base.*

class RecyclerBuilderImpl<T : ViewTyped>(
    override val adapter: BaseAdapter<T>
) : RecyclerBuilder<T> {

    constructor(
        holderFactory: HolderFactory,
        diffCallback: DiffUtil.ItemCallback<T>? = null
    ) : this(diffCallback?.run { AsyncAdapter(holderFactory, this) } ?: Adapter<T>(holderFactory))

    override val itemDecoration: MutableList<RecyclerView.ItemDecoration> = mutableListOf()
    override val layoutManager: RecyclerView.LayoutManager? = null
    override var hasFixedSize: Boolean = true

    override fun build(recyclerView: RecyclerView): Recycler<T> {
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            layoutManager ?: recyclerView.layoutManager ?: LinearLayoutManager(recyclerView.context)

        itemDecoration.forEach(recyclerView::addItemDecoration)
        recyclerView.setHasFixedSize(hasFixedSize)
        return RecyclerImpl(recyclerView, adapter)
    }
}