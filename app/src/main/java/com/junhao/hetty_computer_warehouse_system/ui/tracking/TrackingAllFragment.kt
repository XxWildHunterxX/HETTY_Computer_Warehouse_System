package com.junhao.hetty_computer_warehouse_system.ui.tracking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.junhao.hetty_computer_warehouse_system.R


class TrackingAllFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        //val binding : FragmentTrackingFirstBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tracking_first,container,false)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracking_all, container, false)
    }

    //edit text here

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



}