package com.junhao.hetty_computer_warehouse_system.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.activity_home_page2.view.*
import android.content.SharedPreferences




class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)


        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)



            val PREFS_NAME = "MyPrefsFile"

            val settings: SharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, 0)

            if (settings.getBoolean("my_first_time", true)) {
                //the app is being launched for first time, do something
                Log.d("Comments", "First time")

                // first time task

                // record the fact that the app has been started at least once
                settings.edit().putBoolean("my_first_time", false).commit()
            }else{
                (activity as HomePage?)?.showFloatingActionButton()
            }




        /* val textView: TextView = binding.textHome */
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
         /*   textView.text = it */
        })
        return view
    }

}