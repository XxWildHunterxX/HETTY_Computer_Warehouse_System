package com.junhao.hetty_computer_warehouse_system.ui.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.junhao.hetty_computer_warehouse_system.R


class SentWarehouseItem : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =inflater.inflate(R.layout.fragment_sent_warehouse_item, container, false)







        return view
    }


}