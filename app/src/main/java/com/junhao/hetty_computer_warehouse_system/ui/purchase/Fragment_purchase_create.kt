package com.junhao.hetty_computer_warehouse_system.ui.purchase


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.Navigation

import com.google.firebase.database.FirebaseDatabase

import com.junhao.hetty_computer_warehouse_system.R

import com.junhao.hetty_computer_warehouse_system.data.Purchase
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_add_staff.view.*
import kotlinx.android.synthetic.main.fragment_purchase_create.*
import kotlinx.android.synthetic.main.fragment_purchase_create.tf_productName
import kotlinx.android.synthetic.main.fragment_purchase_create.tf_purchasePrice
import kotlinx.android.synthetic.main.fragment_purchase_create.tf_purchaseQuantity
import kotlinx.android.synthetic.main.fragment_purchase_create.tf_supplierAddress
import kotlinx.android.synthetic.main.fragment_purchase_create.tf_supplierContact
import kotlinx.android.synthetic.main.fragment_purchase_create.tf_supplierName
import kotlinx.android.synthetic.main.fragment_purchase_create.view.*
import kotlinx.android.synthetic.main.fragment_purchase_create_success.*
import java.text.DateFormat
import java.util.*

class Fragment_purchase_create : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_purchase_create, container, false)
        (activity as HomePage?)?.hideFloatingActionButton()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Warehouse").child("warehouse1").child("Purchase")


        view.btn_Submit.setOnClickListener {
            val purProductName = tf_productName.text.toString()
            val purQty = tf_purchaseQuantity.text.toString()
            val purPrice = tf_purchasePrice.text.toString()
            val supplierName = tf_supplierName.text.toString()
            val supplierAddress = tf_supplierAddress.text.toString()
            val supplierContact = tf_supplierContact.text.toString()
            val requestDate = DateFormat.getDateTimeInstance().format(Date())


            if (purProductName.isEmpty()) {
                view.tf_productName.error = "Product Name Required!"
                return@setOnClickListener
            } else if (purQty.isEmpty()) {
                view.tf_purchaseQuantity.error = "Purchase Quantity Required!"
                return@setOnClickListener
            } else if ((purQty.toInt()) <= 0) {
                view.tf_purchaseQuantity.error = "Purchase Quantity should not be negative or zero!"
                return@setOnClickListener
            } else if (supplierName.isEmpty()) {
                view.tf_supplierName.error = "Supplier Name Required!"
                return@setOnClickListener
            } else if (supplierAddress.isEmpty()) {
                view.tf_supplierAddress.error = "Supplier Address Required!"
                return@setOnClickListener
            } else if (supplierContact.isEmpty()) {
                view.tf_supplierContact.error = "Supplier Contact Required!"
                return@setOnClickListener
            }else {
                var randomNum = ((1000..9000).random()).toString()
                var purchaseID :String = "P$randomNum"
                var dup = 0
                while(dup >= 1)
                    myRef.child(purchaseID).get().addOnSuccessListener {
                        if (it.exists()) {
                            randomNum = ((1000..9000).random()).toString()
                            purchaseID=("P$randomNum")
                            dup = 1
                        }
                        else {
                            dup =0
                        }
                    }

                val purchase = Purchase(
                    purchaseID,
                    purProductName,
                    purQty,
                    purPrice,
                    supplierName,
                    supplierAddress,
                    supplierContact,
                    requestDate,
                    "",
                    "Pending"
                )
                myRef.child(purchase.purchaseID!!).setValue(purchase)
                Toast.makeText(activity, "Purchase Create Successfully", Toast.LENGTH_SHORT).show()

                val bundle = bundleOf(
                    Pair("purchaseID", purchaseID)
                )

                Navigation.findNavController(view).navigate(
                    R.id.nav_purchase_create_success,
                    bundle
                )

            }
        }
        return view

    }

}