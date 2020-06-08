package com.gmail.kenzhang0

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gmail.kenzhang0.ui.RecentSearchFragment
import com.gmail.kenzhang0.ui.WeatherSearchFragment
import com.gmail.kenzhang0.vo.Query


class MainActivity : AppCompatActivity(), RecentSearchFragment.ListItemClicked {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, WeatherSearchFragment.newInstance(), WeatherSearchFragment.javaClass.name)
                .commitNow()
        }
    }

    override fun sendQuery(query: Query) {
        val frag = supportFragmentManager.findFragmentByTag(WeatherSearchFragment.javaClass.name) as WeatherSearchFragment
        frag.search(query)
    }
}