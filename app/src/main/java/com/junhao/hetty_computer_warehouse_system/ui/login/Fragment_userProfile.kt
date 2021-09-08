package com.junhao.hetty_computer_warehouse_system.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.FirebaseDatabase
import com.junhao.hetty_computer_warehouse_system.R

class Fragment_userProfile : Fragment() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Warehouse").child("warehouse3").child("Staff")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)








        return view
    }

}