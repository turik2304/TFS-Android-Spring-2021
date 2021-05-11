package com.turik2304.coursework.presentation.recycler_view

import androidx.recyclerview.widget.RecyclerView
import com.turik2304.coursework.presentation.recycler_view.base.BaseAdapter
import com.turik2304.coursework.presentation.recycler_view.base.Recycler
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import io.reactivex.rxjava3.core.Observable

internal class RecyclerImpl<T : ViewTyped>(
    override val recyclerView: RecyclerView,
    override val adapter: BaseAdapter<T>
) : Recycler<T> {

    override fun setItems(items: List<T>) {
        adapter.items = items
    }

    override fun <R : ViewTyped> clickedItem(vararg viewType: Int): Observable<R> {
        return adapter.holderFactory.clickPosition(*viewType)
            .map { adapter.items[it] as R }
    }

    override fun <R : ViewTyped> clickedViewId(viewType: Int, viewId: Int): Observable<R> {
        return adapter.holderFactory.clickPosition(viewType, viewId)
            .map { adapter.items[it] as R }
    }

    override fun repeatOnErrorClick(): Observable<*> {
        return adapter.holderFactory.repeatOnErrorClicks()
    }
}