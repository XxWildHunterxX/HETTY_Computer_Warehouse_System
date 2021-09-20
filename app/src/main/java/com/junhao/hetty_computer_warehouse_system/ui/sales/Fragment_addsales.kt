package com.junhao.hetty_computer_warehouse_system.ui.sales

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.renderscript.Sampler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.SalesOrder
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_addsales.*
import kotlinx.android.synthetic.main.fragment_addsales.view.*
import java.text.SimpleDateFormat
import java.util.*

class Fragment_addsales : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Warehouse")
    private var found: Boolean = false
    private lateinit var showSalesDate: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_addsales, container, false)

        val formatPhone = Regex("^((01)[0-46-9]-)*[0-9]{7,8}\$")
        val productName = arguments?.getString("productName")
        val productPrice = arguments?.getString("productPrice")
        val productQty = arguments?.getString("productQty")


        view.etSalesProductName.setText(productName)
        view.etSalesPrice.setText("RM $productPrice")
        view.tvNote.text = "Total Quantity : $productQty"
view.etSalesQuantity.setText("1")

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

        myRef.child(savedWarehouse!!).child("product").child(productName.toString()).get().addOnSuccessListener {

            view.etSalesProductBarcode.setText(it.child("productBarcode").value.toString())

        }

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

            if (etCustomerName.isEmpty()) {
                view.etSalesCustomerName.error = "Customer Name Required"
                view.etSalesCustomerName.requestFocus()
                return@setOnClickListener
            }else if(!formatPhone.containsMatchIn(etCustomerPhone)){
                view.etSalesCustomerPhone.error = "Customer Phone Wrong Format! Example: 012-3456789"
                view.etSalesCustomerPhone.requestFocus()
                return@setOnClickListener
            }else if(etCustomerPhone.isEmpty()){
                view.etSalesCustomerPhone.error = "Customer Phone Required"
                view.etSalesCustomerPhone.requestFocus()
                return@setOnClickListener
            } else if (etSalesQty.isEmpty() || etSalesQty.toInt() < 1) {
                view.etSalesQuantity.error = "Sales Quantity Required"
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
                            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show()
                        }

                    }


                }


            }

        }

        showSalesDate = view.findViewById(R.id.etSalesDate)
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(myCalendar)
        }

        showSalesDate.setOnClickListener {

            DatePickerDialog(
                requireContext(),
                datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()


        }

        view.etSalesQuantity.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Toast.makeText(activity, "Testing $s", Toast.LENGTH_LONG).show()

                if(s.toString() == ""){
                    view.etSalesPrice.setText("RM 0")
                }else{
                    val calculateTotalPrice = (s.toString().toDouble()) * (productPrice.toString().toDouble())

                    view.etSalesPrice.setText("RM ${calculateTotalPrice.toString()}")
                }



            }

            override fun afterTextChanged(s: Editable?) {

            }

        })



        return view
    }
    private fun updateLable(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        showSalesDate.text = sdf.format(myCalendar.time)
    }


}