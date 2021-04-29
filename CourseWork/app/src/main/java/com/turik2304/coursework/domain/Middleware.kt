package com.turik2304.coursework.domain

import io.reactivex.rxjava3.core.Observable

interface Middleware<A, S> {
    fun bind(actions: Observable<A>, state: Observable<S>): Observable<A>

}