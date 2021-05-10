package com.turik2304.coursework.presentation.utils

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.turik2304.coursework.presentation.recycler_view.AsyncAdapter
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.StreamUI
import com.turik2304.coursework.presentation.recycler_view.items.TopicUI
import com.turik2304.coursework.presentation.recycler_view.items.UserUI
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

object Search {

    private lateinit var textObservable: Observable<String>

    fun initSearch(
        editText: EditText?,
        recyclerView: RecyclerView
    ) {
        val asyncAdapter = recyclerView.adapter as AsyncAdapter<ViewTyped>
        val initialList = asyncAdapter.items
        editText?.addTextChangedListener { text ->
            textObservable = Observable.create { emitter ->
                emitter.onNext(text.toString())
            }
            textObservable
                .distinctUntilChanged()
                .debounce(1, TimeUnit.SECONDS)
                .subscribe { inputText ->
                    val filteredViewTypedList = initialList
                        .filter { viewTypedUI ->
                            when (viewTypedUI) {
                                is StreamUI -> viewTypedUI.name.contains(inputText, true)
                                is TopicUI -> viewTypedUI.name.contains(inputText, true)
                                is UserUI -> viewTypedUI.userName.contains(inputText, true)
                                else -> false
                            }
                        }
                    if (inputText.trim().isNotEmpty()) {
                        asyncAdapter.setItemsWithCommitCallback(filteredViewTypedList) {
                            recyclerView.smoothScrollToPosition(0)
                        }
                    } else asyncAdapter.setItemsWithCommitCallback(initialList) {
                        recyclerView.smoothScrollToPosition(0)
                    }
                }

        }

    }

}