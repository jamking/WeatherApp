package com.gmail.kenzhang0.ui

import androidx.lifecycle.ViewModel
import com.f2prateek.rx.preferences2.Preference
import com.gmail.kenzhang0.common.SchedulerProvider
import com.gmail.kenzhang0.vo.Query
import org.koin.core.KoinComponent
import org.koin.core.inject

class RecentSearchViewModel(val schedulerProvider: SchedulerProvider) : ViewModel(), KoinComponent {
    private val queriesPreference: Preference<List<Query>> by inject()
    var queryList: List<Query>

    init {
        queryList = queriesPreference.get()
    }
}