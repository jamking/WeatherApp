package com.gmail.kenzhang0.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent

/**
 * Base class of state viewmodel
 * State viewmodel is a combination of ViewModel/LiveData/RxJava
 */
abstract class BaseStateViewModel : ViewModel(), KoinComponent {
    lateinit var error: Throwable
    val state = MutableLiveData<Status>()

    internal fun error(e: Throwable) {
        error = e
        state.value = Status.ERROR
    }

    internal fun complete() {
        state.value = Status.SUCCESS
    }

    protected fun go() {
        if (state.value != Status.LOADING) {
            state.value = Status.LOADING
            invoke()
        }
    }

    abstract fun invoke()
}