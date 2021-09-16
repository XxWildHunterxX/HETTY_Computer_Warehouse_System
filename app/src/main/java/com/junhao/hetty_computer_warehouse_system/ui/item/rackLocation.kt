package com.junhao.hetty_computer_warehouse_system.ui.item

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.junhao.hetty_computer_warehouse_system.R
import kotlinx.android.synthetic.main.fragment_rack_location.view.*
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences





class rackLocation : Fragment() {

    lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_rack_location, container, false)

       // imageView = view.findViewById(R.id.imageView)

        val productName = arguments?.getString("rackProductName")
        val productRackLocation = arguments?.getString("productRack")

        view.tvRackLocation.text= productRackLocation
        view.tvRackProductName.text= productName

        val editor = requireContext().getSharedPreferences("sharedPrefsRackLocation", MODE_PRIVATE).edit()
        editor.putString("getRackLocation", productRackLocation)
        editor.apply()


        return view
    }


}