package com.junhao.hetty_computer_warehouse_system.ui.purchase

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.google.firebase.database.FirebaseDatabase
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_purchase_view_details.*
import kotlinx.android.synthetic.main.fragment_purchase_view_details.view.*

class Fragment_purchase_view_details : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_purchase_view_details, container, false)
        (activity as HomePage?)?.hideFloatingActionButton()

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Warehouse").child(savedWarehouse!!).child("Purchase")
        val purchaseID = arguments?.getString("purchaseID")
        val udpatebtn : ImageButton = view.findViewById(R.id.btn_update)

        val tvpurchaseID :TextView = view.findViewById(R.id.tf_purchaseID_details)
        val tvpurProductName :TextView = view.findViewById(R.id.tf_productName_details)
        val tvpurQty:TextView = view.findViewById(R.id.tf_purchaseQuantity_details)
        val tvcostPerUnit : TextView = view.findViewById(R.id.tf_costPerUnit_details)
        val tvtotalCost : TextView = view.findViewById(R.id.tf_totalCost_details)
        val tvsupplierName :TextView = view.findViewById(R.id.tf_supplierName_details)
        val tvsupplierAddress:TextView = view.findViewById(R.id.tf_supplierAddress_details)
        val tvsupplierContact :TextView = view.findViewById(R.id.tf_supplierContact_details)
        val tvrequestDate:TextView = view.findViewById(R.id.tf_requestDate_details)
        val tvacceptDate:TextView = view.findViewById(R.id.tf_acceptDate_details)
        val tvdeliverDate:TextView = view.findViewById(R.id.tf_deliverDate_details)
        val tvreceivedDate:TextView = view.findViewById(R.id.tf_receivedDate_details)
        val tvstatus:TextView = view.findViewById(R.id.tf_status_details)
        myRef.child(purchaseID!!).get().addOnSuccessListener {
            if (it.exists()) {
                tvpurchaseID.text = it.child("purchaseID").value.toString()
                tvpurProductName.text =it.child("purProductName").value.toString()
                tvpurQty.text =it.child("purQty").value.toString()
                tvcostPerUnit.text =it.child("costPerUnit").value.toString()
                tvtotalCost.text = it.child("totalCost").value.toString()
                tvsupplierName.text =it.child("supplierName").value.toString()
                tvsupplierAddress.text =it.child("supplierAddress").value.toString()
                tvsupplierContact.text =it.child("supplierContact").value.toString()
                tvrequestDate.text =it.child("requestDate").value.toString()
                tvstatus.text =it.child("status").value.toString()

                if(it.child("status").value.toString() =="Rejected"){
                    tvreceivedDate.isVisible = false
                    tv_receivedDate.isVisible =false
                    tvdeliverDate.isVisible = false
                    tv_deliverDate.isVisible =false
                    tv_acceptDate.text = getString(R.string.request_date)
                    tvacceptDate.text = it.child("rejectDate").value.toString()
                }
                else if(it.child("status").value.toString() =="Accepted"){
                    tvreceivedDate.isVisible = false
                    tv_receivedDate.isVisible =false
                    tvdeliverDate.isVisible = false
                    tv_deliverDate.isVisible =false
                    tvacceptDate.text = it.child("acceptDate").value.toString()
                }
                else if(it.child("status").value.toString() =="Delivering"){
                    tvreceivedDate.isVisible = false
                    tv_receivedDate.isVisible =false
                    tvacceptDate.text = it.child("acceptDate").value.toString()
                    tvdeliverDate.text = it.child("deliverDate").value.toString()
                }
                else if(it.child("status").value.toString() =="Received"){
                    tvacceptDate.text = it.child("acceptDate").value.toString()
                    tvdeliverDate.text = it.child("deliverDate").value.toString()
                    tvreceivedDate.text=it.child("receivedDate").value.toString()
                }
                if(it.child("status").value.toString() =="Pending"){
                    tvreceivedDate.isVisible = false
                    tv_receivedDate.isVisible =false
                    tvdeliverDate.isVisible = false
                    tv_deliverDate.isVisible =false
                    tvacceptDate.isVisible = false
                    tv_acceptDate.isVisible = false
                    udpatebtn.isVisible = true
                }
            }
            else {
                Toast.makeText(activity,"Record Not Founded",Toast.LENGTH_LONG).show()
            }
        }

        view.btn_update.setOnClickListener{
            val bundle = bundleOf(
                Pair("purchaseID", purchaseID)
            )

            Navigation.findNavController(view!!).navigate(
                R.id.nav_purchase_update,
                bundle
            )

        }
        view.btn_delete.setOnClickListener{
            AlertDialog.Builder(requireContext()).also{
                it.setTitle(getString(R.string.delete_confirmation))
                it.setPositiveButton(getString(R.string.yes),
                    DialogInterface.OnClickListener{ dialog, id ->
                    myRef.child(purchaseID!!).setValue(null).addOnCompleteListener{
                        if(it.isSuccessful){
                            Toast.makeText(activity, "Purchase $purchaseID deleted successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                        Navigation.findNavController(view!!).navigate(
                            R.id.nav_purchaseOrders
                        )
                })
                it.setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener{ dialog, id->
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                })
            }.create().show()
        }

        view.btnReceive.setOnClickListener{
            AlertDialog.Builder(requireContext()).also{
                it.setTitle(getString(R.string.delete_confirmation))
                it.setPositiveButton(getString(R.string.yes),
                    DialogInterface.OnClickListener{ dialog, id ->

                    })
                it.setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener{ dialog, id->
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                })
            }.create().show()

        }
        return view
    }
}