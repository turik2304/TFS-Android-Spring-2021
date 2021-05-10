package com.turik2304.coursework.network

import android.content.Context
import android.os.Bundle

import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.turik2304.coursework.Error
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable

import io.reactivex.rxjava3.subjects.BehaviorSubject


class RxLoader<T>(context: Context, private val observable: Observable<T>) :
    Loader<T>(context) {

    private val cache: BehaviorSubject<T> = BehaviorSubject.create()
    private var subscription: Disposable? = null

    override fun onStartLoading() {
        super.onStartLoading()
        subscription = observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(cache::onNext) { th ->
                Error.showError(context, th)
            }
    }

    override fun onReset() {
        super.onReset()
        subscription?.dispose()
    }

    private class CreateLoaderCallback<T>(
        private val context: Context,
        private val observable: Observable<T>
    ) :
        LoaderManager.LoaderCallbacks<T> {

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<T> {
            return RxLoader(context, observable)
        }

        override fun onLoadFinished(loader: Loader<T>, data: T) {
        }

        override fun onLoaderReset(loader: Loader<T>) {
        }
    }

    companion object {

        fun <T> Single<T>.attachLoader(
            activity: FragmentActivity,
            id: Int
        ): Observable<T> {
            return this.toObservable()
                .compose { observable ->
                    create<T>(
                        activity,
                        id,
                        observable
                    )
                }

        }

        private fun <T> create(
            activity: FragmentActivity,
            id: Int,
            observable: Observable<T>
        ): Observable<T> {
            val loaderManager: LoaderManager = LoaderManager.getInstance(activity)
            val createLoaderCallback = CreateLoaderCallback(activity, observable)
            loaderManager.initLoader(id, null, createLoaderCallback)
            val rxLoader = loaderManager.getLoader<T>(id) as RxLoader<T>
            return rxLoader.cache.hide()
        }
    }

}