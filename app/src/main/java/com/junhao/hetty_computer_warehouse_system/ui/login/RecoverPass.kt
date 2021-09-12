package com.junhao.hetty_computer_warehouse_system.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.junhao.hetty_computer_warehouse_system.R
import kotlinx.android.synthetic.main.activity_recover_pass.*

class RecoverPass : AppCompatActivity() {

    lateinit var prograessbar:ProgressBar
    lateinit var auth : FirebaseAuth
    lateinit var forgotID : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_pass)

        forgotID = findViewById(R.id.tfForgotID)
        prograessbar = findViewById(R.id.pbForgotPassword)
        auth = FirebaseAuth.getInstance()


        btnSendVCode.setOnClickListener {
            resetPassword()
        }

    }

    private fun resetPassword(){

        if(forgotID.text.isNotEmpty()){

            val database = FirebaseDatabase.getInstance()
            val myRef=database.getReference("Staff")

            myRef.child(forgotID.text.toString().trim()).get().addOnSuccessListener {

                val getEmail = it.child("email").value.toString()
                prograessbar.visibility = View.VISIBLE
                auth.sendPasswordResetEmail(getEmail).addOnCompleteListener {

                    if(it.isSuccessful){
                        Toast.makeText(this,"Check Your Email to Reset Your Password!",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,"Invalid Staff ID",Toast.LENGTH_LONG).show()

                    }
                    prograessbar.visibility = View.GONE
                }

            }


        }else{
            forgotID.error="Staff ID Required"
            forgotID.requestFocus()
            return
        }





    }

}