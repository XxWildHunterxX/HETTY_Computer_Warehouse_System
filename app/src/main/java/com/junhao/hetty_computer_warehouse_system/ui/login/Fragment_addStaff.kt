package com.junhao.hetty_computer_warehouse_system.ui.login

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
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
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class Fragment_addStaff : Fragment() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Staff")
    private var imageURI: Uri = Uri.EMPTY
    private var found: Boolean = false
    private lateinit var findDateDOB: TextView
    private lateinit var showDateDOB: TextView
    private lateinit var findDateJoin: TextView
    private lateinit var showDateJoin: TextView
    lateinit var auth: FirebaseAuth
    var oldStaffID :String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()

        val view = inflater.inflate(R.layout.fragment_add_staff, container, false)
        val formatPhone = Regex("^((01)[0-46-9]-)*[0-9]{7,8}\$")
        val formatEmail = Regex("^[A-Za-z]+[A-Za-z0-9._]+@[A-Za-z0-9.-]+.[A-Za-z]{2,4}\$")

        (activity as HomePage?)?.hideFloatingActionButton()


        myRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(childSnapshot in snapshot.children){
                    oldStaffID = childSnapshot.key!!.drop(1)
                    oldStaffID = (Integer.parseInt(oldStaffID) + 1).toString()

                    view.tfStaffID.setText("S$oldStaffID")
                    Toast.makeText(
                        activity,
                        oldStaffID,
                        Toast.LENGTH_LONG
                    ).show()
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


if(view.tfStaffID.text.toString() == ""){

    view.tfStaffID.setText("S1001")

}

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

            val staffID = view.tfStaffID.text.toString().trim()
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

                                val formatter =
                                    SimpleDateFormat("yyyy-MM-dd_hh_mm_ss", Locale.getDefault())
                                val now = Date()
                                val fileName = formatter.format(now)

                                val storageReference =
                                    FirebaseStorage.getInstance().getReference("images/$fileName")

                                storageReference.putFile(imageURI)
                                    .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->

                                        btnStaffImage.setImageURI(null)

                                        btnStaffImage.setImageResource(R.drawable.ic_default_product_select_img)
                                        if (progressDialog.isShowing) progressDialog.dismiss()

                                        taskSnapshot.storage.downloadUrl.addOnCompleteListener { task ->
                                            val getImgValue = task.result.toString()
                                            Log.d("TAG", task.result.toString())

                                            val sharedPreferences :SharedPreferences = activity!!.getSharedPreferences("sharedPrefs",
                                                Context.MODE_PRIVATE)

                                            val savedWarehouse = sharedPreferences.getString("getWarehouse",null)

                                            Toast.makeText(
                                                activity,
                                                savedWarehouse,
                                                Toast.LENGTH_LONG
                                            ).show()

                                            val staff = Staff(
                                                staffID,
                                                staffName,
                                                staffGender,
                                                staffDOB,
                                                staffAddress,
                                                staffPhoneNum,
                                                staffEmail,
                                                staffPosition,
                                                staffJoinedDate,
                                                getImgValue,
                                                savedWarehouse
                                            )

                                            myRef.child(staff.id!!).setValue(staff)
                                            registerUser()
                                            //updateProfile()

                                            imageURI = Uri.EMPTY
                                            view.tfStaffName.text.clear()
                                            view.rgpGender.clearCheck()
                                            view.tfStaffDOB.text.clear()
                                            view.tfStaffAddress.text.clear()
                                            view.tfStaffPhoneNum.text.clear()
                                            view.tfStaffEmail.text.clear()
                                            view.tfStaffPosition.text.clear()
                                            view.tfStaffJoinedDate.text.clear()
                                            view.tfStaffName.requestFocus()
                                            val getNewStaffID :String = (Integer.parseInt(staffID.drop(1)) + 1).toString()

                                            view.tfStaffID.setText("S$getNewStaffID")

                                        }


                                    }).addOnFailureListener {
                                    if (progressDialog.isShowing) progressDialog.dismiss()
                                    Toast.makeText(
                                        activity,
                                        "Please add staff image",
                                        Toast.LENGTH_LONG
                                    ).show()
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

    private fun registerUser() {

        val email = tfStaffEmail.text.toString()
        val defaultPassword = tfStaffPhoneNum.text.toString()

        if (email.isNotEmpty() && defaultPassword.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {

                try {

                    auth.createUserWithEmailAndPassword(email, defaultPassword).addOnCompleteListener { p0 ->
                        if (p0.isSuccessful) {
                            auth.currentUser!!.sendEmailVerification().addOnCompleteListener {

                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        activity,
                                        "Staff Added Successfully,Please Verify Your Email",
                                        Toast.LENGTH_LONG
                                    ).show()

                                } else {
                                    Toast.makeText(
                                        activity,
                                        it.exception!!.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }
                        }
                    }

                    withContext(Dispatchers.Main) {

                    }

                } catch (e: Exception) {

                    withContext(Dispatchers.Main) {

                        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()

                    }


                }


            }


        }

    }

/*
    private fun updateProfile() {

        auth.currentUser?.let { user ->

            val staffID = tfStaffID.text.toString()

            val profileUpdates =
                UserProfileChangeRequest.Builder().setDisplayName(staffID).build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            activity,
                            "Successfully updated user profile",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    }

                }

            }

        }


    }
*/


}