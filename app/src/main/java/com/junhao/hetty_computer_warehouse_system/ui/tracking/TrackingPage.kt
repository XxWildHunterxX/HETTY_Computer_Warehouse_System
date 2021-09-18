package com.junhao.hetty_computer_warehouse_system.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.TrackingAdapter


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
        adapter.addFragment(TrackingDeliveredFragment(),"Delivered")


        trackingviewpager!!.adapter = adapter
        trackingtab!!.setupWithViewPager(trackingviewpager)


        // WHEN HOME PAGE TRACKING CARD VIEW IS CLICKED, IDENTIFY SELECTED TAB
        val homeSelectedTab = arguments?.getString("trackingTab")

        if(homeSelectedTab=="Pending"){

            trackingtab?.setScrollPosition(1,0f,true)
            trackingviewpager?.currentItem = 1

        }else if(homeSelectedTab=="Prepared"){

            trackingtab?.setScrollPosition(2,0f,true)
            trackingviewpager?.currentItem = 2

        }else if(homeSelectedTab=="In Transit"){

            trackingtab?.setScrollPosition(3,0f,true)
            trackingviewpager?.currentItem = 3
        }else if(homeSelectedTab=="Delivered"){

            trackingtab?.setScrollPosition(4,0f,true)
            trackingviewpager?.currentItem = 4
        }





        // Inflate the layout for this fragment
        return view
    }


}


