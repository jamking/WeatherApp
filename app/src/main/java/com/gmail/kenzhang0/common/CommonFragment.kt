package com.gmail.kenzhang0.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gmail.kenzhang0.R

abstract class CommonFragment : Fragment() {
    private lateinit var act: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = activity as AppCompatActivity
    }

    fun start(fragment: Fragment, args: Bundle? = null): Fragment {
        args?.let { fragment.arguments = it }
        act.supportFragmentManager.beginTransaction()
            .setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.container, fragment, fragment.javaClass.name)
            .addToBackStack(fragment.javaClass.name)
            .commit()
        return fragment
    }

    protected fun backPreviousPage() {
        act.supportFragmentManager.popBackStack()
    }
}