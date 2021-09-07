package com.junhao.hetty_computer_warehouse_system.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.junhao.hetty_computer_warehouse_system.data.TrackingItem

class TrackingAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

    private val fragmentList : MutableList<Fragment> = ArrayList()
    private val titleList : MutableList<String> = ArrayList()


    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment:Fragment,title:String){

        fragmentList.add(fragment)
        titleList.add(title)

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }

}