package com.gmail.kenzhang0.common

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import retrofit2.HttpException
import retrofit2.Response

abstract class StateViewModel<T>(private val schedulerProvider: SchedulerProvider) :
    BaseStateViewModel(),
    SingleObserver<T> {

    override fun onError(e: Throwable) {
        if (e is CompositeException)
            error(e.exceptions[0])
        else
            error(e)
    }

    override fun onSuccess(t: T) {
        complete()
        onNext(t)
    }

    abstract fun onNext(t: T)

    override operator fun invoke() {
        try {
            observable().observeOn(schedulerProvider.main).subscribeOn(schedulerProvider.io)
                .subscribe(this)
        } catch (e: Exception) {
            onError(e)
        }
    }

    override fun onSubscribe(d: Disposable) {

    }

    abstract fun observable(): Single<T>


    protected fun proceed(response: Response<Void>): Response<Void> {
        if (response.isSuccessful)
            return response
        else {
            throw HttpException(response)
        }
    }
}
