package com.junhao.hetty_computer_warehouse_system.ui.login

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock.sleep
import android.renderscript.Sampler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.activity_login_page.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginPage : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var progressbar: ProgressBar
    var flag: Boolean = false
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("Staff")

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sleep(1000)
        setContentView(R.layout.activity_login_page)

        progressbar = findViewById(R.id.pbLogin)
        auth = FirebaseAuth.getInstance()

        checkLoggedInState()

        btnLogin.setOnClickListener() {
            loginUser()
        }

        val forgotPassword: TextView = findViewById(R.id.tvForgotPassword)

        forgotPassword.setOnClickListener {

            val intent = Intent(this, RecoverPass::class.java)

            startActivity(intent)

        }

        val tfPassword: EditText = findViewById(R.id.tfPassword)

        tfPassword.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= tfPassword.right - tfPassword.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {

                    if (!flag) {
                        tfPassword.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        tfPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_visibility_off,
                            0
                        )
                        flag = true
                    } else {
                        tfPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        tfPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_visibility,
                            0
                        )
                        flag = false
                    }
                    return@OnTouchListener true
                }
            }

            false
        })

    }

    override fun onStart() {
        super.onStart()
        progressbar.visibility = View.GONE
    }

    private fun checkLoggedInState() {

        val user = auth.currentUser
        progressbar.visibility = View.VISIBLE
        if (user != null) {

            if (user.isEmailVerified) {

                try {

                    val sharedPreferences : SharedPreferences = this.getSharedPreferences("sharedPrefs",
                        Context.MODE_PRIVATE)
                    val savedStaffName = sharedPreferences.getString("getStaffName",null)

                    Toast.makeText(
                        applicationContext, "Welcome ${savedStaffName.toString()}", Toast.LENGTH_LONG).show()

                    val shareIntent = Intent(this, HomePage::class.java)
                    startActivity(shareIntent)

                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "Action no found", Toast.LENGTH_LONG).show()
                }
                // set the shared preferences here
            } else {
                Toast.makeText(this, "Please Verify Your Email Address", Toast.LENGTH_LONG).show()
                progressbar.visibility = View.GONE
            }

        }

    }

    private fun loginUser() {

        val staffID = tfLoginStaffID.text.toString().trim()
        val password = tfPassword.text.toString().trim()

        if (staffID.isNotEmpty() && password.isNotEmpty()) {

            try {

                myRef.child(staffID).get().addOnSuccessListener {
                    val getEmail = it.child("email").value.toString()
                    //Toast.makeText(applicationContext,getEmail, Toast.LENGTH_LONG).show()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            auth.signInWithEmailAndPassword(getEmail, password).await()
                            withContext(Dispatchers.Main) {


                                val staffID = it.child("id").getValue(String::class.java)
                                val staffName = it.child("name").getValue(String::class.java)
                                val staffEmail = it.child("email").getValue(String::class.java)
                                val staffImg = it.child("staffImg").getValue(String::class.java)


                                if (staffID == tfLoginStaffID.text.toString().trim()) {

                                    val getWarehouse =
                                        it.child("warehouse").getValue(String::class.java)

                                    val sharedPreferences =
                                        getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

                                    val editor = sharedPreferences.edit()

                                    editor.apply {
                                        putString("getWarehouse", getWarehouse.toString())
                                        putString("getStaffName",staffName.toString())
                                        putString("getStaffID",staffID.toString())
                                        putString("getStaffEmail",staffEmail.toString())
                                        putString("getStaffImg",staffImg.toString())
                                    }.apply()


                                    checkLoggedInState()

                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Invalid Staff ID or Password",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }


                            }


                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG)
                                    .show()
                                progressbar.visibility = View.GONE
                            }
                        }

                    }

                }

            } catch (e: Exception) {

                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()

            }

        } else {
            when {
                staffID.isEmpty() && password.isEmpty() -> {
                    Toast.makeText(applicationContext, "Staff ID Required", Toast.LENGTH_LONG)
                        .show()
                    tfLoginStaffID.requestFocus()
                    Toast.makeText(applicationContext, "Password Required", Toast.LENGTH_LONG)
                        .show()
                    tfPassword.requestFocus()
                }
                password.isEmpty() -> {
                    Toast.makeText(applicationContext, "Password Required", Toast.LENGTH_LONG)
                        .show()
                    tfPassword.requestFocus()
                }
                else -> {
                    Toast.makeText(applicationContext, "Staff ID Required", Toast.LENGTH_LONG)
                        .show()
                    tfLoginStaffID.requestFocus()
                }
            }

        }

    }


}