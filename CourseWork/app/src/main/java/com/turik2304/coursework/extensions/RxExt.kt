package com.turik2304.coursework.extensions

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.SerialDisposable

fun Disposable.addTo(disposables: CompositeDisposable) {
    disposables.add(this)
}

fun Disposable.setTo(disposables: SerialDisposable) {
    disposables.set(this)
}