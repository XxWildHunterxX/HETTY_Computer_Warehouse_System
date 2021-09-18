package com.junhao.hetty_computer_warehouse_system.ui.login

import android.app.Activity
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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.Staff
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_add_staff.view.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_staff.*
import java.text.SimpleDateFormat
import java.util.*

class Fragment_userProfile : Fragment() {
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef : DatabaseReference
    private var imageURI: Uri = Uri.EMPTY

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)
        val formatPhone = Regex("^((01)[0-46-9]-)*[0-9]{7,8}\$")
        val formatEmail = Regex("^[A-Za-z]+[A-Za-z0-9.]+@[A-Za-z0-9.-]+.[A-Za-z]{2,4}\$")
        val formatDate =
            Regex("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$")

        (activity as HomePage?)?.hideFloatingActionButton()

        val sharedPreferences : SharedPreferences = requireActivity().getSharedPreferences("sharedPrefs",
            Context.MODE_PRIVATE)

        val savedStaffID = sharedPreferences.getString("getStaffID",null)

        myRef = database.getReference("Staff")

        view.spinnerGender.isEnabled = false

        myRef.child(savedStaffID.toString()).get().addOnSuccessListener {
            view.tvStaffName.setText(it.child("name").value.toString())
            view.tvStaffID.setText(it.child("id").value.toString())
            if (it.child("gender").value.toString() == "Male") {
                view.spinnerGender.setSelection(0)
            } else {
                view.spinnerGender.setSelection(1)
            }

            view.tvStaffDOB.setText(it.child("dateOfBirth").value.toString())
            view.tvStaffAddress.setText(it.child("address").value.toString())
            view.tvStaffPhoneNo.setText(it.child("phoneNum").value.toString())
            view.tvStaffEmail.setText(it.child("email").value.toString())
            view.tvStaffPosition.setText(it.child("position").value.toString())
            view.tvStaffJoinedDate.setText(it.child("joinDate").value.toString())

            var imageUri: String? = null
            imageUri = it.child("staffImg").value.toString()
            Picasso.get().load(imageUri).into(imageview_account_profile);

        }.addOnFailureListener {
            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show()
        }

        view.btnEditProfile.setOnClickListener() {
            if (view.btnEditProfile.text == "Edit Profile") {
                view.tvStaffName.isEnabled = true
                view.tvStaffDOB.isEnabled = true
                view.tvStaffAddress.isEnabled = true
                view.tvStaffPhoneNo.isEnabled = true
                view.tvStaffEmail.isEnabled = true
                view.tvStaffPosition.isEnabled = true
                view.tvStaffJoinedDate.isEnabled = true
                view.spinnerGender.isEnabled = true
                imageview_account_profile.setOnClickListener {
                    selectImage()
                }
                view.btnEditProfile.text = "Update"

            } else {
                val staffName = view.tvStaffName.text.toString().trim()
                val staffGender = view.spinnerGender.selectedItem.toString().trim()
                val staffDOB = view.tvStaffDOB.text.toString().trim()
                val staffAddress = view.tvStaffAddress.text.toString().trim()
                val staffPhoneNum = view.tvStaffPhoneNo.text.toString().trim()
                val staffEmail = view.tvStaffEmail.text.toString().trim()
                val staffPosition = view.tvStaffPosition.text.toString().trim()
                val staffJoinedDate = view.tvStaffJoinedDate.text.toString().trim()

                if (staffName.isEmpty()) {
                    view.tvStaffName.error = "Staff Name Required!"
                    return@setOnClickListener
                } else if (staffDOB.isEmpty()) {
                    view.tvStaffDOB.error = "Staff Date Of Birth Required!"
                    return@setOnClickListener
                } else if (!formatDate.containsMatchIn(staffDOB)) {
                    view.tvStaffDOB.error =
                        "Staff Date of Birth Wrong Format! Example: DD/MM/YYYY"
                    return@setOnClickListener
                } else if (staffAddress.isEmpty()) {
                    view.tvStaffAddress.error = "Staff Address Required!"
                    return@setOnClickListener
                } else if (staffPhoneNum.isEmpty()) {
                    view.tvStaffPhoneNo.error = "Staff Phone Number Required!"
                    return@setOnClickListener
                } else if (!formatPhone.containsMatchIn(staffPhoneNum)) {
                    view.tvStaffPhoneNo.error =
                        "Staff Phone Number Wrong Format! Example: 012-3456789"
                    return@setOnClickListener
                } else if (staffEmail.isEmpty()) {
                    view.tvStaffEmail.error = "Staff Email Required!"
                    return@setOnClickListener
                } else if (!formatEmail.containsMatchIn(staffEmail)) {
                    view.tvStaffEmail.error =
                        "Staff Email Wrong Format! Example: johnsmith@example.com"
                    return@setOnClickListener
                } else if (staffPosition.isEmpty()) {
                    view.tvStaffPosition.error = "Staff Position Required!"
                    return@setOnClickListener
                } else if (staffJoinedDate.isEmpty()) {
                    view.tvStaffJoinedDate.error = "Staff Joined Date Required!"
                    return@setOnClickListener
                } else if (!formatDate.containsMatchIn(staffJoinedDate)) {
                    view.tvStaffJoinedDate.error =
                        "Staff Joined Date Wrong Format! Example: DD/MM/YYYY"
                } else {
                    val progressDialog = ProgressDialog(activity)
                    progressDialog.setMessage("Uploading File ...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()

                    val formatter = SimpleDateFormat("yyyy-MM-dd_hh_mm_ss", Locale.getDefault())
                    val now = Date()
                    val fileName = formatter.format(now)

                    val storageReference =
                        FirebaseStorage.getInstance().getReference("images/$fileName")

                    storageReference.putFile(imageURI)
                        .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->

                            imageview_account_profile.setImageURI(null)
                            imageURI = Uri.EMPTY
                            if (progressDialog.isShowing) progressDialog.dismiss()

                            taskSnapshot.storage.downloadUrl.addOnCompleteListener { task ->
                                val getImgValue = task.result.toString()
                                Log.d("TAG", task.result.toString())

                                val staff = mapOf<String, String>(
                                    "id" to savedStaffID.toString(),
                                    "name" to staffName,
                                    "gender" to staffGender,
                                    "dateOfBirth" to staffDOB,
                                    "address" to staffAddress,
                                    "phoneNum" to staffPhoneNum,
                                    "email" to staffEmail,
                                    "position" to staffPosition,
                                    "joinDate" to staffJoinedDate,
                                    "staffImg" to getImgValue
                                )

                                myRef.child(savedStaffID.toString()).updateChildren(staff).addOnSuccessListener {
                                    Navigation.findNavController(view)
                                        .navigate(R.id.action_nav_profile_self)
                                }

                                Toast.makeText(
                                    activity,
                                    "Update with image change",
                                    Toast.LENGTH_LONG
                                ).show()

                            }


                        }).addOnFailureListener {
                            if (progressDialog.isShowing) progressDialog.dismiss()

                            val staff = mapOf<String, String>(
                                "id" to savedStaffID.toString(),
                                "name" to staffName,
                                "gender" to staffGender,
                                "dateOfBirth" to staffDOB,
                                "address" to staffAddress,
                                "phoneNum" to staffPhoneNum,
                                "email" to staffEmail,
                                "position" to staffPosition,
                                "joinDate" to staffJoinedDate
                            )

                            myRef.child(savedStaffID.toString()).updateChildren(staff).addOnSuccessListener {
                                Navigation.findNavController(view)
                                    .navigate(R.id.action_nav_profile_self)
                            }




                            Toast.makeText(
                                activity,
                                "Update without image change",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
        }







        return view
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageURI = data?.data!!
            imageview_account_profile.setImageURI(imageURI)

        }
    }

}