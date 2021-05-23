package com.turik2304.coursework.presentation.recycler_view.clicks

import com.turik2304.coursework.presentation.base.Action
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

interface ClickMapper<T : Action> {
    fun bind(actionConsumer: Consumer<T>): Disposable
}