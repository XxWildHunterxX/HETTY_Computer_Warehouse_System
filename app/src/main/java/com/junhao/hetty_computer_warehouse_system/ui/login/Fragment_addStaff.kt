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
            /*val etStaffName = view.findViewById<EditText>(R.id.tfStaffName)
            //val etStaffGender = view.findViewById<EditText>(R.id.rgpGender)
            val etStaffDOB = view.findViewById<EditText>(R.id.tfStaffDOB)
            val etStaffAddress = view.findViewById<EditText>(R.id.tfStaffAddress)
            val etStaffPhoneNum = view.findViewById<EditText>(R.id.tfStaffPhoneNum)
            val etStaffEmail = view.findViewById<EditText>(R.id.tfStaffEmail)
            val etStaffPosition = view.findViewById<EditText>(R.id.tfStaffPosition)
            val etStaffJoinedDate = view.findViewById<EditText>(R.id.tfStaffJoinedDate)

            val staffName = etStaffName.text.toString()
            //val staffGender = etStaffGender.text.toString()
            val staffDOB = etStaffDOB.text.toString()
            val staffAddress = etStaffAddress.text.toString()
            val staffPhoneNum = etStaffPhoneNum.text.toString()
            val staffEmail = etStaffEmail.text.toString()
            val staffPosition = etStaffPosition.text.toString()
            val staffJoinedDate = etStaffJoinedDate.text.toString()
*/
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Staff")


            /*val staff = Staff("S002", staffName,staffGender, staffDOB
                , staffAddress, staffPhoneNum, staffEmail, staffPosition
                , staffJoinedDate)

             */

            val s = Staff("S001", "JunLi", "10/09/2000", "kepong",
                "0124488522", "yewjunli@gmail.com", "employee", "05/09/2021")

            myRef.child(s.id).setValue(s)
        }


        // Inflate the layout for this fragment
        return view
    }

}