package com.junhao.hetty_computer_warehouse_system.ui.login

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.Staff
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import kotlinx.android.synthetic.main.fragment_add_staff.*
import kotlinx.android.synthetic.main.fragment_add_staff.view.*
import java.text.SimpleDateFormat
import java.util.*

class Fragment_addStaff : Fragment() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Warehouse").child("warehouse3").child("Staff")
    private var imageURI: Uri = Uri.EMPTY
    private var found: Boolean = false
    private lateinit var findDateDOB: TextView
    private lateinit var showDateDOB: TextView
    private lateinit var findDateJoin: TextView
    private lateinit var showDateJoin: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_staff, container, false)
        val formatPhone = Regex("^((01)[0-46-9]-)*[0-9]{7,8}\$")
        val formatEmail = Regex("^[A-Za-z]+[A-Za-z0-9.]+@[A-Za-z0-9.-]+.[A-Za-z]{2,4}\$")

        (activity as HomePage?)?.hideFloatingActionButton()

        view.btnStaffImage.setOnClickListener {
            chooseImage()
        }

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
            } else if (!formatPhone.containsMatchIn(staffPhoneNum)) {
                view.tfStaffPhoneNum.error = "Staff Phone Number Wrong Format! Example: 012-3456789"
                return@setOnClickListener
            } else if (staffEmail.isEmpty()) {
                view.tfStaffEmail.error = "Staff Email Required!"
                return@setOnClickListener
            } else if (!formatEmail.containsMatchIn(staffEmail)) {
                view.tfStaffEmail.error = "Staff Email Wrong Format! Example: johnsmith@example.com"
                return@setOnClickListener
            } else if (staffPosition.isEmpty()) {
                view.tfStaffPosition.error = "Staff Position Required!"
                return@setOnClickListener
            } else if (staffJoinedDate.isEmpty()) {
                view.tfStaffJoinedDate.error = "Staff Joined Date Required!"
                return@setOnClickListener
            } else {
                myRef.get().addOnSuccessListener {
                    val eventListener: ValueEventListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (ds in dataSnapshot.children) {
                                val phoneNo = ds.child("phoneNum").getValue(String::class.java)
                                val email = ds.child("email").getValue(String::class.java)
                                Log.d("TAG", phoneNo!!)
                                if (phoneNo == staffPhoneNum) {
                                    found = true
                                    view.tfStaffPhoneNum.error = "Staff Phone Number Existed!"
                                    break
                                } else if (email == staffEmail) {
                                    found = true
                                    view.tfStaffEmail.error = "Staff Email Existed!"
                                    break
                                } else {
                                    found = false
                                }
                            }
                            if (!found) {

                                val progressDialog = ProgressDialog(activity)
                                progressDialog.setMessage("Uploading File ...")
                                progressDialog.setCancelable(false)
                                progressDialog.show()

                                val formatter = SimpleDateFormat("yyyy-MM-dd_hh_mm_ss", Locale.getDefault())
                                val now = Date()
                                val fileName = formatter.format(now)

                                val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

                                storageReference.putFile(imageURI).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->

                                        btnStaffImage.setImageURI(null)
                                        Toast.makeText(activity, "Staff Added Successfully", Toast.LENGTH_LONG).show()
                                        imageURI = Uri.EMPTY
                                        btnStaffImage.setImageResource(R.drawable.ic_default_product_select_img)
                                        if (progressDialog.isShowing) progressDialog.dismiss()

                                        taskSnapshot.storage.downloadUrl.addOnCompleteListener { task ->
                                            val getImgValue = task.result.toString()
                                            Log.d("TAG", task.result.toString())

                                            val staff = Staff(
                                                "S1003",
                                                staffName,
                                                staffGender,
                                                staffDOB,
                                                staffAddress,
                                                staffPhoneNum,
                                                staffEmail,
                                                staffPosition,
                                                staffJoinedDate,
                                                getImgValue
                                            )

                                            myRef.child(staff.id!!).setValue(staff)

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


                                    }).addOnFailureListener {
                                    if (progressDialog.isShowing) progressDialog.dismiss()
                                    Toast.makeText(activity, "Please add staff image", Toast.LENGTH_LONG).show()
                                }

                            }

                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    }
                    myRef.addListenerForSingleValueEvent(eventListener)


                }.addOnFailureListener {

                    Toast.makeText(activity, "Cannot Find", Toast.LENGTH_LONG).show()
                }
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
        findDateDOB.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePicker1,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        findDateJoin.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePicker2,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

// Inflate the layout for this fragment
        return view
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageURI = data?.data!!
            btnStaffImage.setImageURI(imageURI)

        }
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