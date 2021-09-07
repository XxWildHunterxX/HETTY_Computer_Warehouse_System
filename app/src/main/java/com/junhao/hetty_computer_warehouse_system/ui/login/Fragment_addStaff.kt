package com.junhao.hetty_computer_warehouse_system.ui.login

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.Staff
import kotlinx.android.synthetic.main.fragment_add_staff.*
import kotlinx.android.synthetic.main.fragment_add_staff.view.*
import java.text.SimpleDateFormat
import java.util.*

class Fragment_addStaff : Fragment() {
    private lateinit var findDateDOB: TextView
    private lateinit var showDateDOB: TextView
    private lateinit var findDateJoin: TextView
    private lateinit var showDateJoin: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_staff, container, false)

        view.btnAddStaff.setOnClickListener {

            fun getGender(): String {
                return when (view.rgpGender.checkedRadioButtonId) {
                    R.id.radMale -> getString(R.string.male)
                    R.id.radFemale -> getString(R.string.female)
                    else -> ""
                }
            }

            val staffName = view.tfStaffName.text.toString().trim()
            val staffGender = getGender()
            val staffDOB = view.tfStaffDOB.text.toString().trim()
            val staffAddress = view.tfStaffAddress.text.toString().trim()
            val staffPhoneNum = view.tfStaffPhoneNum.text.toString().trim()
            val staffEmail = view.tfStaffEmail.text.toString().trim()
            val staffPosition = view.tfStaffPosition.text.toString().trim()
            val staffJoinedDate = view.tfStaffJoinedDate.text.toString().trim()

            if (staffName.isEmpty()) {
                view.tfStaffName.error = "Staff Name Required!"
                return@setOnClickListener
            } else if (staffGender.isEmpty()) {
                Toast.makeText(activity, "Please select gender!", Toast.LENGTH_LONG).show()
            } else if (staffDOB.isEmpty()) {
                view.tfStaffDOB.error = "Staff Date Of Birth Required!"
                return@setOnClickListener
            } else if (staffAddress.isEmpty()) {
                view.tfStaffAddress.error = "Staff Address Required!"
                return@setOnClickListener
            } else if (staffPhoneNum.isEmpty()) {
                view.tfStaffPhoneNum.error = "Staff Phone Number Required!"
                return@setOnClickListener
            } else if (staffEmail.isEmpty()) {
                view.tfStaffEmail.error = "Staff Email Required!"
                return@setOnClickListener
            } else if (staffPosition.isEmpty()) {
                view.tfStaffPosition.error = "Staff Position Required!"
                return@setOnClickListener
            } else if (staffJoinedDate.isEmpty()) {
                view.tfStaffJoinedDate.error = "Staff Joined Date Required!"
                return@setOnClickListener
            } else {


                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("Warehouse").child("warehouse3").child("Staff")


                val staff = Staff(
                    "S1003",
                    staffName,
                    staffGender,
                    staffDOB,
                    staffAddress,
                    staffPhoneNum,
                    staffEmail,
                    staffPosition,
                    staffJoinedDate
                )

                myRef.child(staff.id).setValue(staff)

                val snackBar =
                    Snackbar.make(it, "Staff Register Successfully", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                snackBar.show()

                view.tfStaffName.text.clear()
                view.rgpGender.clearCheck()
                view.tfStaffDOB.text.clear()
                view.tfStaffAddress.text.clear()
                view.tfStaffPhoneNum.text.clear()
                view.tfStaffEmail.text.clear()
                view.tfStaffPosition.text.clear()
                view.tfStaffJoinedDate.text.clear()
                view.tfStaffName.requestFocus()
            }
        }
        findDateDOB = view.findViewById(R.id.findDateDOB)
        showDateDOB = view.findViewById(R.id.tfStaffDOB)
        findDateJoin = view.findViewById(R.id.findDateJoin)
        showDateJoin = view.findViewById(R.id.tfStaffJoinedDate)

        val myCalendar = Calendar.getInstance()

        val datePicker1 = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable1(myCalendar)
        }
        val datePicker2 = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable2(myCalendar)
        }
        findDateDOB.setOnClickListener{
            DatePickerDialog(requireContext(), datePicker1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        findDateJoin.setOnClickListener{
            DatePickerDialog(requireContext(), datePicker2, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

// Inflate the layout for this fragment
        return view
    }


    private fun updateLable1(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        showDateDOB.setText(sdf.format(myCalendar.time))
    }
    private fun updateLable2(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        showDateJoin.setText(sdf.format(myCalendar.time))
    }
}