package com.junhao.hetty_computer_warehouse_system.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import com.junhao.hetty_computer_warehouse_system.R




class TrackingFirstFragment : Fragment() {


    // Array of strings...
    var simpleList: ListView? = null
    var countryList = arrayOf("India", "China", "australia", "Portugle", "America", "NewZealand")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val binding: TrackingFirstFragment = DataBindingUtil.inflate(inflater , R.layout.fragment_tracking_first, container, false)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracking_first, container, false)
    }


}