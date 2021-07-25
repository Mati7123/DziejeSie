package com.example.dziejesie.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class Fragments(activity: FragmentActivity, private val fragments: List<Class<out Fragment>>) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment =
        fragments[position].newInstance()
}