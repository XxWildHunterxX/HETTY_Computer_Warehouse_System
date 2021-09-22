package com.junhao.hetty_computer_warehouse_system.ui.purchase

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.google.firebase.database.FirebaseDatabase
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.Purchase
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_purchase_create.*
import kotlinx.android.synthetic.main.fragment_purchase_create.view.*
import kotlinx.android.synthetic.main.fragment_purchase_update.*
import kotlinx.android.synthetic.main.fragment_purchase_update.view.*
import kotlinx.android.synthetic.main.fragment_purchase_update.view.btn_save
import kotlinx.android.synthetic.main.fragment_purchase_view_details.*
import java.text.DateFormat
import java.util.*


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
         val tvcostPerUnit : EditText = view.findViewById(R.id.update_tf_costPerUnit)
         val tvtotalCost : TextView = view.findViewById(R.id.update_tf_totalCost)
        val tvsupplierName : EditText = view.findViewById(R.id.update_tf_supplierName)
        val tvsupplierAddress: EditText = view.findViewById(R.id.update_tf_supplierAddress)
        val tvsupplierContact : EditText = view.findViewById(R.id.update_tf_supplierContact)
        val tvrequestDate: TextView = view.findViewById(R.id.update_tf_requestDate)
         val tvStatus:TextView = view.findViewById(R.id.update_tf_status)
         val formatPhone = Regex("^((01)[0-46-9]-)*[0-9]{7,8}\$")
         val formatTel = Regex("^((0)[0-46-9]-)*[0-9]{7,8}\$")


        myRef.child(purchaseID!!).get().addOnSuccessListener {
            if (it.exists()) {
                tvpurchaseID.text = it.child("purchaseID").value.toString()
                tvpurProductName.setText(it.child("purProductName").value.toString())
                tvpurQty.setText(it.child("purQty").value.toString())
                tvcostPerUnit.setText(it.child("costPerUnit").value.toString())
                tvtotalCost.text = it.child("totalCost").value.toString()
                tvsupplierName.setText(it.child("supplierName").value.toString())
                tvsupplierAddress.setText(it.child("supplierAddress").value.toString())
                tvsupplierContact.setText(it.child("supplierContact").value.toString())
                tvrequestDate.text = it.child("requestDate").value.toString()
                tvStatus.text = it.child("status").value.toString()
            }
            else {
                Toast.makeText(activity,"Record Not Founded", Toast.LENGTH_LONG).show()
            }
        }

         view.update_tf_costPerUnit.addTextChangedListener(object:TextWatcher{
             override fun afterTextChanged(p0: Editable?) {
             }

             override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
             }

             override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                 var qty = update_tf_purchaseQuantity.text
                 if (p0.toString() == ""||qty.toString()=="") {
                     view.update_tf_totalCost.text ="0"
                 } else {
                     var calculateTotalPrice = (p0.toString().toDouble()) * (qty.toString().toDouble())
                     view.update_tf_totalCost.text = "$calculateTotalPrice"
                 }
             }

         })
         view.update_tf_purchaseQuantity.addTextChangedListener(object:TextWatcher{
             override fun afterTextChanged(p0: Editable?) {
             }

             override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
             }

             override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                 var costUnit = update_tf_costPerUnit.text
                 if (p0.toString() == ""||costUnit.toString()=="") {
                     view.update_tf_totalCost.text ="0"
                 } else {
                     var calculateTotalPrice = (p0.toString().toDouble()) * (costUnit.toString().toDouble())
                     view.update_tf_totalCost.text = "$calculateTotalPrice"
                 }
             }

         })

        view.btn_save.setOnClickListener(){
            AlertDialog.Builder(requireContext()).also{
                it.setTitle(getString(R.string.update_confirmation))
                it.setPositiveButton(getString(R.string.yes),
                    DialogInterface.OnClickListener{ dialog, id ->
                        myRef.child(purchaseID!!).setValue(null).addOnCompleteListener{
                            if(it.isSuccessful){
                                val purProductName = update_tf_productName.text.toString()
                                val purQty = update_tf_purchaseQuantity.text.toString()
                                val costPerUnit = update_tf_costPerUnit.text.toString()
                                val totalCost = update_tf_totalCost.text.toString()
                                val supplierName = update_tf_supplierName.text.toString()
                                val supplierAddress = update_tf_supplierAddress.text.toString()
                                val supplierContact = update_tf_supplierContact.text.toString()
                                val requestDate = DateFormat.getDateTimeInstance().format(Date())
                                if (purProductName.isEmpty()) {
                                    view.update_tf_productName.error = "Product Name Required!"
                                } else if (purQty.isEmpty()) {
                                    view.update_tf_purchaseQuantity.error = "Purchase Quantity Required!"
                                } else if ((purQty.toInt()) <= 0) {
                                    view.update_tf_purchaseQuantity.error = "Purchase Quantity should not be negative or zero!"
                                } else if (costPerUnit.isEmpty()) {
                                    view.tf_costPerUnit.error = "Cost Per Unit Required!"
                                }else if (supplierName.isEmpty()) {
                                    view.update_tf_supplierName.error = "Supplier Name Required!"
                                } else if (supplierAddress.isEmpty()) {
                                    view.update_tf_supplierAddress.error = "Supplier Address Required!"
                                } else if (supplierContact.isEmpty()) {
                                    view.update_tf_supplierContact.error = "Supplier Contact Required!"
                                }else if (!formatPhone.containsMatchIn(supplierContact)&&!formatTel.containsMatchIn(supplierContact)) {
                                    view.tf_supplierContact.error = "Supplier Contact Wrong Format!"
                                }

                                val purchase = Purchase(
                                    purchaseID,
                                    purProductName,
                                    purQty,
                                    costPerUnit,
                                    totalCost,
                                    supplierName,
                                    supplierAddress,
                                    supplierContact,
                                    requestDate,
                                    "",
                                    "",
                                    "",
                                    "",
                                    "Pending"
                                )
                                myRef.child(purchase.purchaseID!!).setValue(purchase)
                                Toast.makeText(activity, "Purchase Updated Successfully", Toast.LENGTH_SHORT).show()
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
    return view
    }

 }
