package com.junhao.hetty_computer_warehouse_system.ui.login

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.*
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    lateinit var auth: FirebaseAuth
lateinit var pd : ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        (activity as HomePage?)?.hideFloatingActionButton()
        auth = FirebaseAuth.getInstance()

        pd = ProgressDialog(activity)


        return view
    }

    private fun showChangePasswordDialog() {

        val viewDialog: View =
            LayoutInflater.from(activity).inflate(R.layout.dialog_update_password, null)

        val currentPassword: EditText = viewDialog.findViewById(R.id.currentPassword)
        val newPassword: EditText = viewDialog.findViewById(R.id.newPassword)

        val btnChangePassword: Button = viewDialog.findViewById(R.id.btnConfirmChangePassword)

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setView(viewDialog)

        val dialog: AlertDialog = builder.create()
        dialog.show()

        btnChangePassword.setOnClickListener {

            val oldPassword = currentPassword.text.toString().trim()
            val newPassword = newPassword.text.toString().trim()

            if (TextUtils.isEmpty(oldPassword)) {
                Toast.makeText(activity, "Enter your current password...", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (newPassword.length < 6) {
                Toast.makeText(
                    activity,
                    "Password length must at least 6 characters...",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            dialog.dismiss()
            updatePassword(oldPassword, newPassword)

        }

    }

    private fun updatePassword(oldPassword: String, newPassword: String) {
        pd.show()
        val user: FirebaseUser = auth.currentUser!!

        val authCredential: AuthCredential =
            EmailAuthProvider.getCredential(user.email.toString(), oldPassword)

        user.reauthenticate(authCredential).addOnSuccessListener {

            user.updatePassword(newPassword).addOnSuccessListener {
                pd.dismiss()
                Toast.makeText(
                    activity,
                    "Password Updated",
                    Toast.LENGTH_SHORT
                ).show()


            }.addOnFailureListener {
                pd.dismiss()
                Toast.makeText(
                    activity,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }.addOnFailureListener {
            pd.dismiss()
            Toast.makeText(
                activity,
                it.message,
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun onStart() {
        super.onStart()

        val cvChangePassword = view?.findViewById<CardView>(R.id.changePasswordCV)
        val cvLogOut = view?.findViewById<CardView>(R.id.logOutCV)

        cvChangePassword?.setOnClickListener {
            pd.setMessage("Changing Password")
            showChangePasswordDialog()
        }
        cvLogOut?.setOnClickListener {
            pd.setMessage("Logging Out")
            pd.show()
            auth.signOut()
            val intent = Intent(activity, LoginPage::class.java)

            startActivity(intent)
        }


    }


}