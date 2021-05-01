package com.turik2304.coursework.presentation.recycler_view.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

abstract class HolderFactory : (ViewGroup, Int) -> BaseViewHolder<ViewTyped> {

    abstract fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>?

    final override fun invoke(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> {
        val view: View = viewGroup.inflate(viewType)
        return checkNotNull(createViewHolder(view, viewType)) {
            "uknown viewType" + viewGroup.resources.getResourceName(viewType)
        } as BaseViewHolder<ViewTyped>
    }

}

fun <T : View> View.inflate(
    @LayoutRes
    layout: Int,
    root: ViewGroup? = this as? ViewGroup,
    attachToRoot: Boolean = false,
): T {
    return LayoutInflater.from(context).inflate(layout, root, attachToRoot) as T
}
