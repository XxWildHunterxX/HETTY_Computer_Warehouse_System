package com.junhao.hetty_computer_warehouse_system.ui.sales

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.SalesOrder
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import kotlinx.android.synthetic.main.fragment_add_staff.view.*
import kotlinx.android.synthetic.main.fragment_addsales.*
import kotlinx.android.synthetic.main.fragment_addsales.view.*

class Fragment_addsales : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Warehouse")
    private var found: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_addsales, container, false)

        if(view.etSalesOrderID.text.toString() == ""){

            view.etSalesOrderID.setText("SO1001")

        }



        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)

        myRef.child(savedWarehouse!!).child("product")

        (activity as HomePage?)?.hideFloatingActionButton()

        view.btnSaveSales.setOnClickListener {

            val etSalesOrderID = view.etSalesOrderID.toString().trim()
            val etProductBarcode = view.etSalesProductBarcode.toString().trim()
            val etProductName = view.etSalesProductName.toString().trim()
            val etCustomerName = view.etSalesCustomerName.toString().trim()
            val etCustomerPhone = view.etSalesCustomerPhone.toString().trim()
            val etSalesQty = view.etSalesQuantity.toString().trim()
            val etSalesDate = view.etSalesDate.toString().trim()
            val etSalesPrice = view.etSalesPrice.toString().trim()
            val spinnerSalesType = view.spinnerSalesType.selectedItem.toString().trim()

            if (etProductName.isEmpty()) {
                view.etSalesProductName.error = "Product Name Required"
                view.etSalesProductName.requestFocus()
                return@setOnClickListener
            } else if (etProductBarcode.isEmpty()) {
                view.etSalesProductBarcode.error = "Product Barcode Required"
                view.etSalesProductBarcode.requestFocus()
                return@setOnClickListener
            } else if (etCustomerName.isEmpty()) {
                view.etSalesCustomerName.error = "Customer Name Required"
                view.etSalesCustomerName.requestFocus()
                return@setOnClickListener
            } else if (etSalesQty.isEmpty() || etSalesQty.toInt() < 1) {
                view.etSalesQuantity.error = "Customer Name Required"
                view.etSalesQuantity.requestFocus()
                return@setOnClickListener
            } else if (etSalesDate.isEmpty()) {
                view.etSalesDate.error = "Sales Date Required"
                view.etSalesDate.requestFocus()
                return@setOnClickListener
            } else {
                myRef.get().addOnSuccessListener {
                    val eventListener: ValueEventListener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (snap in snapshot.children) {
                                val barCode =
                                    snap.child("productBarcode").getValue(String::class.java)
                                if (barCode == etProductBarcode) {
                                    found = false
                                    break
                                } else {
                                    found = true
                                }
                            }
                            if (!found) {

                                val salesOrder = SalesOrder(
                                    etSalesOrderID,
                                    etProductBarcode,
                                    etProductName,
                                    etCustomerName,
                                    etCustomerPhone,
                                    etSalesQty,
                                    etSalesDate,
                                    etSalesPrice,
                                    spinnerSalesType
                                )
                                myRef.child(etSalesOrderID).setValue(salesOrder)

                            } else {
                                view.etSalesProductBarcode.error = "Product Barcode Not Existed!"
                                view.etSalesProductBarcode.requestFocus()
                                return
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG)
                        }

                    }


                }


            }

        }
        view.etSalesProductBarcode.setOnClickListener {

            Navigation.findNavController(view).navigate(
                R.id.nav_items
            )

            Toast.makeText(activity, "Clicked Sales Order Barcode", Toast.LENGTH_LONG).show()
        }
        view.etSalesProductBarcode.setOnTouchListener(View.OnTouchListener { v, event ->

            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= view.etSalesProductBarcode.right - view.etSalesProductBarcode.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    val scanner = IntentIntegrator.forSupportFragment(this)
                    scanner.setBeepEnabled(false)
                    scanner.initiateScan()

                    return@OnTouchListener true
                }
            }

            false
        })



        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    etSalesProductBarcode.setText(result.contents)
                    Toast.makeText(activity, "Scanned: " + result.contents, Toast.LENGTH_LONG)
                        .show()

                    val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
                        "sharedPrefs",
                        Context.MODE_PRIVATE
                    )

                    val savedWarehouse = sharedPreferences.getString("getWarehouse", null)


                    val tempDatabase = FirebaseDatabase.getInstance()
                    val tempRef = tempDatabase.getReference("Warehouse")

                    tempRef.child(savedWarehouse!!).child("product")


/*
                            if (!it.exists()) {
                                etSalesProductBarcode.error = "Product Barcode Not Existed"
                                etSalesProductBarcode.requestFocus()
                            } else {
                                val tvQuantity = it.child("productQuantity").value.toString()
                                val salesPrice = etSalesQuantity.text.toString()
                                    .toDouble() * it.child("productPrice").value.toString()
                                    .toDouble()
                                tvNote.text = "Note : $tvQuantity"
                                etSalesPrice.setText(salesPrice.toString())

                            }

 */


                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }

        }

    }


}