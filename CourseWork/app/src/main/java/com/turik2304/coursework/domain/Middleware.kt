package com.turik2304.coursework.domain

import com.turik2304.coursework.data.repository.Repository
import io.reactivex.rxjava3.core.Observable

interface Middleware<A, S> {
    val repository: Repository
    fun bind(actions: Observable<A>, state: Observable<S>): Observable<A>
}