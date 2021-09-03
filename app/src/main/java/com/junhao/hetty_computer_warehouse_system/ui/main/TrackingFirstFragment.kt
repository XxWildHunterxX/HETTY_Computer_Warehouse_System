package com.junhao.hetty_computer_warehouse_system.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.databinding.FragmentTrackingFirstBinding
import android.widget.ArrayAdapter








class TrackingFirstFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentTrackingFirstBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tracking_first,container,false)


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracking_first, container, false)
    }


}