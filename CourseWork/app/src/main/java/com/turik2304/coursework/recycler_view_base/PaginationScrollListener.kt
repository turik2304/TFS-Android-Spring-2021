package com.turik2304.coursework.recycler_view_base

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener(var layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        if (!isLoading() && !isLastPage() && firstVisibleItemPosition <= 5) {
            loadMoreItems()
        }
    }

    abstract fun loadMoreItems()


}