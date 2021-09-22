package com.junhao.hetty_computer_warehouse_system.ui.purchase

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import com.google.firebase.database.FirebaseDatabase
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_purchase_create_success.view.*


class Fragment_purchase_create_success : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_purchase_create_success, container, false)
        (activity as HomePage?)?.hideFloatingActionButton()

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Warehouse").child(savedWarehouse!!).child("Purchase")
        val purchaseID = arguments?.getString("purchaseID")

        val tvpurchaseID : TextView = view.findViewById(R.id.tf_purchaseID_success)
        val tvpurProductName : TextView = view.findViewById(R.id.tf_productName_success)
        val tvpurQty: TextView = view.findViewById(R.id.tf_purchaseQuantity_success)
        val tvcostPerUnit : TextView = view.findViewById(R.id.tf_costPerUnit_success)
        val tvtotalCost : TextView = view.findViewById(R.id.tf_totalCost_success)
        val tvsupplierName : TextView = view.findViewById(R.id.tf_supplierName_success)
        val tvsupplierAddress: TextView = view.findViewById(R.id.tf_supplierAddress_success)
        val tvsupplierContact : TextView = view.findViewById(R.id.tf_supplierContact_success)
        val tvrequestDate: TextView = view.findViewById(R.id.tf_requestDate_success)

        myRef.child(purchaseID!!).get().addOnSuccessListener {
            if (it.exists()) {
                tvpurchaseID.text = it.child("purchaseID").value.toString()
                tvpurProductName.text = it.child("purProductName").value.toString()
                tvpurQty.text = it.child("purQty").value.toString()
                tvcostPerUnit.text =it.child("costPerUnit").value.toString()
                tvtotalCost.text = it.child("totalCost").value.toString()
                tvsupplierName.text = it.child("supplierName").value.toString()
                tvsupplierAddress.text = it.child("supplierAddress").value.toString()
                tvsupplierContact.text = it.child("supplierContact").value.toString()
                tvrequestDate.text = it.child("requestDate").value.toString()
            }

        }

        view.btn_Done.setOnClickListener{
            Navigation.findNavController(view).navigate(
                R.id.nav_home
            )
        }
        return view
    }


}