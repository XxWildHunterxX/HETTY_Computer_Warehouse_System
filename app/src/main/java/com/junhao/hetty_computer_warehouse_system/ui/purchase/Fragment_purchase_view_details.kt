package com.junhao.hetty_computer_warehouse_system.ui.purchase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage

class Fragment_purchase_view_details : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_purchase_view_details, container, false)
        (activity as HomePage?)?.hideFloatingActionButton()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Warehouse").child("warehouse1").child("Purchase")
        val purchaseID = arguments?.getString("purchaseID")

        val tvpurchaseID :TextView = view.findViewById(R.id.tf_purchaseID_details)
        val tvpurProductName :TextView = view.findViewById(R.id.tf_productName_details)
        val tvpurQty:TextView = view.findViewById(R.id.tf_purchaseQuantity_details)
        val tvpurPrice :TextView = view.findViewById(R.id.tf_purchasePrice_details)
        val tvsupplierName :TextView = view.findViewById(R.id.tf_supplierName_details)
        val tvsupplierAddress:TextView = view.findViewById(R.id.tf_supplierAddress_details)
        val tvsupplierContact :TextView = view.findViewById(R.id.tf_supplierContact_details)
        val tvrequestDate:TextView = view.findViewById(R.id.tf_requestDate_details)
        val tvreceivedDate:TextView = view.findViewById(R.id.tf_receivedDate_details)
        val tvstatus:TextView = view.findViewById(R.id.tf_status_details)

        myRef.child(purchaseID!!).get().addOnSuccessListener {
            if (it.exists()) {
                tvpurchaseID.text = it.child("purchaseID").value.toString()
                tvpurProductName.text =it.child("purProductName").value.toString()
                tvpurQty.text =it.child("purQty").value.toString()
                tvpurPrice.text =it.child("purPrice").value.toString()
                tvsupplierName.text =it.child("supplierName").value.toString()
                tvsupplierAddress.text =it.child("supplierAddress").value.toString()
                tvsupplierContact.text =it.child("supplierContact").value.toString()
                tvrequestDate.text =it.child("requestDate").value.toString()
                tvreceivedDate.text =it.child("receivedDate").value.toString()
                tvstatus.text =it.child("status").value.toString()

            }
            else {
                Toast.makeText(activity,"Record Not Founded",Toast.LENGTH_LONG).show()
            }
        }
        return view
    }


}