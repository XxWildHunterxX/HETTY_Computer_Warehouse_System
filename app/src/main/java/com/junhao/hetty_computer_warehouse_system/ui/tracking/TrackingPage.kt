package com.junhao.hetty_computer_warehouse_system.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.TrackingAdapter
import com.junhao.hetty_computer_warehouse_system.databinding.ActivityTrackingPageBinding


class TrackingPage : Fragment() {

    var trackingtab: TabLayout? = null
    var trackingviewpager: ViewPager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        val view = inflater.inflate(R.layout.activity_tracking_page, container, false)

        trackingtab = view.findViewById(R.id.tabs)
        trackingviewpager = view.findViewById(R.id.view_pager)

        val adapter = TrackingAdapter(childFragmentManager)
        adapter.addFragment(TrackingAllFragment(),"All")
        adapter.addFragment(TrackingPendingFragment(),"Pending")
        adapter.addFragment(TrackingPreparedFragment(),"Prepared")
        adapter.addFragment(TrackingInTransitFragment(),"In Transit")

        trackingviewpager!!.adapter = adapter
        trackingtab!!.setupWithViewPager(trackingviewpager)

        // Inflate the layout for this fragment
        return view
    }


}


