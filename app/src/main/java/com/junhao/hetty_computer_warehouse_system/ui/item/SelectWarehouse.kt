package com.junhao.hetty_computer_warehouse_system.ui.item

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_select_warehouse.view.*


class SelectWarehouse : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_select_warehouse, container, false)

        (activity as HomePage?)?.hideFloatingActionButton()

        val sharedPreferences : SharedPreferences = requireActivity().getSharedPreferences("sharedPrefs",
            Context.MODE_PRIVATE)
        val savedWarehouse = sharedPreferences.getString("getWarehouse",null)

        if(savedWarehouse =="warehouse1"){
            view.btnUp.text = "Warehouse 2"
            view.btnDown.text = "Warehouse 3"
        }else if(savedWarehouse =="warehouse2"){
            view.btnUp.text = "Warehouse 1"
            view.btnDown.text = "Warehouse 3"
        }else{
            view.btnUp.text = "Warehouse 1"
            view.btnDown.text = "Warehouse 2"
        }

        var getSelectedWarehouse = ""
        view.btnUp.setOnClickListener {
            if(view.btnUp.text == "Warehouse 1"){
                getSelectedWarehouse = "warehouse1"
            }else{
                //2
                getSelectedWarehouse = "warehouse2"
            }

            val sharedPreferencesWarehouse =
                requireActivity().getSharedPreferences("sharedPrefsWarehouse", Context.MODE_PRIVATE)

            val editor = sharedPreferencesWarehouse.edit()

            editor.apply {
                putString("getSelectedWarehouse", getSelectedWarehouse)
            }.apply()

            Navigation.findNavController(view).navigate(
                R.id.nav_searchWarehouseProduct
            )

        }

        view.btnDown.setOnClickListener {
            if(view.btnDown.text == "Warehouse 3"){
                getSelectedWarehouse = "warehouse3"
            }else{
                //2
                getSelectedWarehouse = "warehouse2"
            }

            val sharedPreferencesWarehouse =
                requireActivity().getSharedPreferences("sharedPrefsWarehouse", Context.MODE_PRIVATE)

            val editor = sharedPreferencesWarehouse.edit()

            editor.apply {
                putString("getSelectedWarehouse", getSelectedWarehouse)
            }.apply()


            Navigation.findNavController(view).navigate(
                R.id.nav_searchWarehouseProduct
            )

        }


        return view
    }

}