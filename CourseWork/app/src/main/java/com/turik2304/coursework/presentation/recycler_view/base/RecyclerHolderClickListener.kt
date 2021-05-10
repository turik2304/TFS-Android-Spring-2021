package com.turik2304.coursework.presentation.recycler_view.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.functions.Consumer

interface RecyclerHolderClickListener {
    fun accept(viewHolder: BaseViewHolder<*>, onClick: () -> Unit = {})
    fun acceptLong(viewHolder: BaseViewHolder<*>, onClick: () -> Unit = {})
    fun accept(view: View, viewHolder: BaseViewHolder<*>, onClick: () -> Unit = {})
    fun acceptLong(view: View, viewHolder: BaseViewHolder<*>, onClick: () -> Unit = {})
}

data class ItemClick(val viewType: Int, val position: Int, val view: View)

class RecyclerItemClicksObservable : Observable<ItemClick>(), RecyclerHolderClickListener {

    private val source: PublishRelay<ItemClick> = PublishRelay.create()

    override fun accept(viewHolder: BaseViewHolder<*>, onClick: () -> Unit) {
        viewHolder.itemView.run { setOnClickListener(Listener(source, viewHolder, this, onClick)) }
    }

    override fun acceptLong(viewHolder: BaseViewHolder<*>, onClick: () -> Unit) =
        viewHolder.itemView.run {
            setOnLongClickListener(
                LongListener(
                    source,
                    viewHolder,
                    this,
                    onClick
                )
            )
        }

    override fun accept(view: View, viewHolder: BaseViewHolder<*>, onClick: () -> Unit) {
        view.setOnClickListener(Listener(source, viewHolder, view, onClick))
    }

    override fun acceptLong(view: View, viewHolder: BaseViewHolder<*>, onClick: () -> Unit) {
        view.setOnLongClickListener(LongListener(source, viewHolder, view, onClick))
    }

    override fun subscribeActual(observer: Observer<in ItemClick>) {
        source.subscribe(observer)
    }

    class Listener(
        private val source: Consumer<ItemClick>,
        private val viewHolder: BaseViewHolder<*>,
        private val clickedView: View,
        private val onClick: () -> Unit
    ) : View.OnClickListener {

        override fun onClick(v: View?) {
            if (viewHolder.adapterPosition != RecyclerView.NO_POSITION) {
                onClick()
                source.accept(
                    ItemClick(
                        viewHolder.itemViewType,
                        viewHolder.adapterPosition,
                        clickedView
                    )
                )
            }
        }
    }

    class LongListener(
        private val source: Consumer<ItemClick>,
        private val viewHolder: BaseViewHolder<*>,
        private val clickedView: View,
        private val onClick: () -> Unit
    ) : View.OnLongClickListener {

        override fun onLongClick(v: View?): Boolean {
            return if (viewHolder.adapterPosition != RecyclerView.NO_POSITION) {
                onClick()
                source.accept(
                    ItemClick(
                        viewHolder.itemViewType,
                        viewHolder.adapterPosition,
                        clickedView
                    )
                )
                true
            } else false
        }
    }

}