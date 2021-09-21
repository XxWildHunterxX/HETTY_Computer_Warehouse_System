package com.junhao.hetty_computer_warehouse_system.ui.purchase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.Purchase
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_purchase_update.*
import kotlinx.android.synthetic.main.fragment_purchase_update.view.*


class Fragment_purchase_update : Fragment() {

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_purchase_update, container, false)
        (activity as HomePage?)?.hideFloatingActionButton()

         val database = FirebaseDatabase.getInstance()
         val myRef = database.getReference("Warehouse").child("warehouse1").child("Purchase")
         val purchaseID = arguments?.getString("purchaseID")
        val tvpurchaseID : TextView = view.findViewById(R.id.update_tf_purchaseID)
         tvpurchaseID.text = purchaseID
        val tvpurProductName : EditText = view.findViewById(R.id.update_tf_productName)
        val tvpurQty: EditText = view.findViewById(R.id.update_tf_purchaseQuantity)
        val tvsupplierName : EditText = view.findViewById(R.id.update_tf_supplierName)
        val tvsupplierAddress: EditText = view.findViewById(R.id.update_tf_supplierAddress)
        val tvsupplierContact : EditText = view.findViewById(R.id.update_tf_supplierContact)
        val tvrequestDate: TextView = view.findViewById(R.id.update_tf_requestDate)
        val tvreceivedDate: EditText = view.findViewById(R.id.update_tf_receivedDate)
        val tvstatus : Spinner = view.findViewById(R.id.update_spin_status)
        var purPrice :String ?= null
        myRef.child(purchaseID!!).get().addOnSuccessListener {
            if (it.exists()) {
                tvpurchaseID.text = it.child("purchaseID").value.toString()
                tvpurProductName.setText(it.child("purProductName").value.toString())
                tvpurQty.setText(it.child("purQty").value.toString())
                purPrice = it.child("purPrice").value.toString()
                tvsupplierName.setText(it.child("supplierName").value.toString())
                tvsupplierAddress.setText(it.child("supplierAddress").value.toString())
                tvsupplierContact.setText(it.child("supplierContact").value.toString())
                tvrequestDate.text = it.child("requestDate").value.toString()
                tvreceivedDate.setText(it.child("receivedDate").value.toString())
                if(it.child("status").value.toString()=="Pending"){
                    tvstatus.setSelection(0)
                }else if(it.child("status").value.toString()=="Accepted"){
                    tvstatus.setSelection(1)
                }
                else if(it.child("status").value.toString()=="Rejected"){
                    tvstatus.setSelection(2)
                }
                else if(it.child("status").value.toString()=="Delivering"){
                    tvstatus.setSelection(3)
                }
                else if(it.child("status").value.toString()=="Received"){
                    tvstatus.setSelection(4)
                }
            }
            else {
                Toast.makeText(activity,"Record Not Founded", Toast.LENGTH_LONG).show()
            }
        }

        view.btn_save.setOnClickListener(){
            val purProductName = update_tf_productName.text.toString()
            val purQty = update_tf_purchaseQuantity.text.toString()
            val supplierName = update_tf_supplierName.text.toString()
            val supplierAddress = update_tf_supplierAddress.text.toString()
            val supplierContact = update_tf_supplierContact.text.toString()
            val requestDate = update_tf_requestDate.text.toString()
            val receivedDate = update_tf_receivedDate.text.toString()
            var status :String ?=null

            if(tvstatus.selectedItemPosition == 0){
                status = "Pending"
            }
            else if(tvstatus.selectedItemPosition == 1){
                status = "Accepted"
            }
            else if(tvstatus.selectedItemPosition == 2){
                status = "Rejected"
            }
            else if(tvstatus.selectedItemPosition == 3){
                status = "Delivering"
            }
            else if(tvstatus.selectedItemPosition == 4){
                status = "Received"
            }

            if (purProductName.isEmpty()) {
                view.update_tf_productName.error = "Product Name Required!"
                return@setOnClickListener
            } else if (purQty.isEmpty()) {
                view.update_tf_purchaseQuantity.error = "Purchase Quantity Required!"
                return@setOnClickListener
            } else if ((purQty.toInt()) <= 0) {
                view.update_tf_purchaseQuantity.error = "Purchase Quantity should not be negative or zero!"
                return@setOnClickListener
            } else if (supplierName.isEmpty()) {
                view.update_tf_supplierName.error = "Supplier Name Required!"
                return@setOnClickListener
            } else if (supplierAddress.isEmpty()) {
                view.update_tf_supplierAddress.error = "Supplier Address Required!"
                return@setOnClickListener
            } else if (supplierContact.isEmpty()) {
                view.update_tf_supplierContact.error = "Supplier Contact Required!"
                return@setOnClickListener
            }
            else if(status == "Received" && receivedDate.isEmpty()){
                view.update_tf_receivedDate.error = "Received Date Required!"
                return@setOnClickListener
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
                receivedDate,
                status
            )
            myRef.child(purchase.purchaseID!!).setValue(purchase)
            Toast.makeText(activity, "Purchase Updated Successfully", Toast.LENGTH_SHORT).show()

        }
    return view
    }

 }
