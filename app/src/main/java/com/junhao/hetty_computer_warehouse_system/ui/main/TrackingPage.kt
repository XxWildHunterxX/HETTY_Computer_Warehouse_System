package com.junhao.hetty_computer_warehouse_system.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.TrackingTabPageAdapter


class TrackingPage : AppCompatActivity() {

    var trackingtab: TabLayout? = null
    var trackingviewpager: ViewPager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_page)

        trackingtab = findViewById<TabLayout>(R.id.tabs)
        trackingviewpager = findViewById<ViewPager>(R.id.view_pager)



        val adapter =
            TrackingTabPageAdapter(
                this,
                supportFragmentManager,
                trackingtab!!.tabCount
            )

        trackingviewpager!!.adapter = adapter

        trackingviewpager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(trackingtab))

        trackingtab!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                trackingviewpager!!.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })


    }
}


