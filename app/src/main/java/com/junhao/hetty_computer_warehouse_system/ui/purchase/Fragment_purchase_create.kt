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
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.google.firebase.database.FirebaseDatabase
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.Purchase
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_purchase_create.*
import kotlinx.android.synthetic.main.fragment_purchase_create.tf_productName
import kotlinx.android.synthetic.main.fragment_purchase_create.tf_purchaseQuantity
import kotlinx.android.synthetic.main.fragment_purchase_create.tf_supplierAddress
import kotlinx.android.synthetic.main.fragment_purchase_create.tf_supplierContact
import kotlinx.android.synthetic.main.fragment_purchase_create.tf_supplierName
import kotlinx.android.synthetic.main.fragment_purchase_create.view.*
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
        val formatPhone = Regex("^((01)[0-46-9]-)*[0-9]{7,8}\$")
        val formatTel = Regex("^((0)[0-46-9]-)*[0-9]{7,8}\$")


        view.tf_costPerUnit.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val qty = tf_purchaseQuantity.text
                if (p0.toString() == ""||qty.toString()=="") {
                    view.tf_totalCost.text ="0"
                } else {
                    val calculateTotalPrice = (p0.toString().toDouble()) * (qty.toString().toDouble())
                    view.tf_totalCost.text = String.format("%.2f",calculateTotalPrice)
                }
            }

        })

        view.tf_purchaseQuantity.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var costUnit = tf_costPerUnit.text
                if (p0.toString() == ""||costUnit.toString()=="") {
                    view.tf_totalCost.text ="0"
                } else {
                    var calculateTotalPrice = (p0.toString().toDouble()) * (costUnit.toString().toDouble())
                    view.tf_totalCost.text = String.format("%.2f",calculateTotalPrice)
                }
            }

        })

        view.btn_Submit.setOnClickListener {
            val purProductName = tf_productName.text.toString()
            val purQty = tf_purchaseQuantity.text.toString()
            val costPerUnit = tf_costPerUnit.text.toString()
            val totalCost = tf_totalCost.text.toString()
            val supplierName = tf_supplierName.text.toString()
            val supplierAddress = tf_supplierAddress.text.toString()
            val supplierContact = tf_supplierContact.text.toString()
            val requestDate = DateFormat.getDateTimeInstance().format(Date())

            if (purProductName.isEmpty()) {
                view.tf_productName.error = "Product Name Required!"
            } else if (purQty.isEmpty()) {
                view.tf_purchaseQuantity.error = "Purchase Quantity Required!"
            } else if ((purQty.toInt()) <= 0) {
                view.tf_purchaseQuantity.error = "Purchase Quantity should not be negative or zero!"
            }
            else if (costPerUnit.isEmpty()) {
                view.tf_costPerUnit.error = "Cost Per Unit Required!"
            }
            else if (supplierName.isEmpty()) {
                view.tf_supplierName.error = "Supplier Name Required!"
            } else if (supplierAddress.isEmpty()) {
                view.tf_supplierAddress.error = "Supplier Address Required!"
            } else if (supplierContact.isEmpty()) {
                view.tf_supplierContact.error = "Supplier Contact Required!"
            }
            else if (!formatPhone.containsMatchIn(supplierContact)&&!formatTel.containsMatchIn(supplierContact)) {
                    view.tf_supplierContact.error = "Supplier Contact Wrong Format!"
            }else {
                AlertDialog.Builder(requireContext()).also{
                    it.setTitle(getString(R.string.create_confirmation))
                    it.setPositiveButton(getString(R.string.yes),
                        DialogInterface.OnClickListener{ dialog, id ->
                            var randomNum = ((1000..9000).random()).toString()
                            var purchaseID = "P$randomNum"
                            myRef.child("$purchaseID").get().addOnSuccessListener {
                                if (it.exists()) {
                                    randomNum = ((1000..9000).random()).toString()
                                    purchaseID = "P$randomNum"
                                    Toast.makeText(activity, "1st $purchaseID", Toast.LENGTH_SHORT).show()
                                }
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
                            Toast.makeText(activity, "Purchase Create Successfully", Toast.LENGTH_SHORT).show()

                            val bundle = bundleOf(
                                Pair("purchaseID", purchaseID)
                            )

                            Navigation.findNavController(view).navigate(
                                R.id.nav_purchase_create_success,
                                bundle
                            )

                        })
                    it.setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener{ dialog, id->
                        Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                    })
                }.create().show()
            }
        }
        return view

    }

}