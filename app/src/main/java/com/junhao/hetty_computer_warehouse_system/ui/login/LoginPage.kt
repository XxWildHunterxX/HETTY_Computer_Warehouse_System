package com.junhao.hetty_computer_warehouse_system.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.junhao.hetty_computer_warehouse_system.R

class LoginPage : AppCompatActivity() {

    lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

auth = FirebaseAuth.getInstance()


    }

    private fun registerUser(){



    }


}