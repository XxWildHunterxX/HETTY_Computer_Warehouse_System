package com.junhao.hetty_computer_warehouse_system.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
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
import androidx.navigation.Navigation
import com.junhao.hetty_computer_warehouse_system.ui.login.Fragment_addStaff


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


        /* val textView: TextView = binding.textHome */
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
         /*   textView.text = it */
        })


        return view
    }

    override fun onStart() {
        (activity as HomePage?)?.showFloatingActionButton()
        Log.i("Lifecycle", "onStartFragment")

        super.onStart()

    }

}