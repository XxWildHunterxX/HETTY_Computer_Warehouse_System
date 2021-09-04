package com.junhao.hetty_computer_warehouse_system.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.database.FirebaseDatabase
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.Staff

class Fragment_addStaff : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_staff, container, false)

        view.findViewById<Button>(R.id.btnAddStaff).setOnClickListener{
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Staff")

            val staff = Staff("S001","Jun Hao","Male")

            myRef.child(staff.id).setValue(staff)
        }


        // Inflate the layout for this fragment
        return view
    }

}