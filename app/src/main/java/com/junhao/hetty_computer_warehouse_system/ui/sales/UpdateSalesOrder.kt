package com.junhao.hetty_computer_warehouse_system.ui.sales

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.database.*
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.SalesOrder
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import kotlinx.android.synthetic.main.fragment_update_product.view.*
import kotlinx.android.synthetic.main.fragment_update_sales_order.view.*
import java.text.SimpleDateFormat
import java.util.*


class UpdateSalesOrder : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private lateinit var productRef: DatabaseReference
    private lateinit var paymentType: List<String>
    private val mySalesRef = database.getReference("Warehouse")
    private val getRef = database.getReference("Warehouse")
    private val chkRef = database.getReference("Warehouse")
    private lateinit var checkRef: DatabaseReference
    private lateinit var showSalesDate: TextView
    var totalQty = "0"
    var price = "0.00"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update_sales_order, container, false)

        (activity as HomePage?)?.hideFloatingActionButton()

        val formatName = Regex("^[a-zA-Z ]+\$")
        val formatPhone = Regex("^((01)[0-46-9]-)*[0-9]{7,8}\$")

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)


        val sharedPreferencesSalesOrder: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefsSalesOrder",
            Context.MODE_PRIVATE
        )

        val salesOrderID = arguments?.getString("salesOrderID")
        val salesOrderBarCode = arguments?.getString("salesOrderBarCode")
        val salesOrderDate = arguments?.getString("salesOrderDate")
        val salesOrderPrice = arguments?.getString("salesOrderPrice")
        val salesOrderQty = arguments?.getString("salesOrderQty")
        val salesOrderPaymentType = arguments?.getString("salesOrderPaymentType")
        val salesOrderView = arguments?.getString("salesOrderView")

        view.etSOID.setText(salesOrderID)
        view.etSOProductBarcode.setText(salesOrderBarCode)
        view.etSODate.setText(salesOrderDate)
        view.etSOPrice.setText(salesOrderPrice)
        view.etSOQuantity.setText(salesOrderQty)

        if (salesOrderView == "View") {
            view.btnUpdateEditSO.visibility = View.GONE
            paymentType = listOf(salesOrderPaymentType.toString())
        } else {
            if (salesOrderPaymentType == "Cash") {
                paymentType = listOf(salesOrderPaymentType.toString(), "Debit")
            } else {
                paymentType = listOf(salesOrderPaymentType.toString(), "Cash")
            }
        }
        val spinner = view.findViewById<Spinner>(R.id.spinnerSOType)

        val spinnerArrayAdapter = ArrayAdapter<String>(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item,
            paymentType
        )

        spinner.adapter = spinnerArrayAdapter

        val getSOStatus = sharedPreferencesSalesOrder.getString("getSOStatus", null)

        if (getSOStatus == "Update") {

            productRef = database.getReference("Warehouse")

            val getSOProductName = arguments?.getString("productName")
            val getSOPrice = arguments?.getString("salesOrderPrice")
            val productQty = arguments?.getString("productQty")
            val getSOCustomerName = arguments?.getString("getSOCustomerName")
            val getSOCustomerPhone = arguments?.getString("getSOCustomerPhone")

            val calculatePrice = salesOrderQty!!.toDouble() * getSOPrice!!.toDouble()

            view.etSOProductName.setText(getSOProductName.toString())
            view.etSOPrice.setText("RM ${calculatePrice.toString()}")
            view.etSOCustomerName.setText(getSOCustomerName.toString())
            view.etSOCustomerPhone.setText(getSOCustomerPhone.toString())
            view.tvSONote.text = "Total Quantity : $productQty"
            totalQty = productQty.toString()

            productRef.child(savedWarehouse!!).child("product").child(getSOProductName!!).get()
                .addOnSuccessListener {
                    view.etSOProductBarcode.setText(it.child("productBarcode").value.toString())
                    price = it.child("productPrice").value.toString()
                }
        } else {
            myRef = database.getReference("Warehouse")
            productRef = database.getReference("Warehouse")
            myRef.child(savedWarehouse!!).child("salesOrder").child(salesOrderID.toString()).get()
                .addOnSuccessListener { snapshot ->
                    view.etSOProductName.setText(snapshot.child("salesProductName").value.toString())
                    view.etSOCustomerPhone.setText(snapshot.child("salesCustomerPhone").value.toString())
                    view.etSOCustomerName.setText(snapshot.child("salesCustomerName").value.toString())

                    val etSOProductName = snapshot.child("salesProductName").value.toString()
                    productRef.child(savedWarehouse!!).child("product").child(etSOProductName).get()
                        .addOnSuccessListener {
                            val getQty = it.child("productQuantity").value.toString()
                            view.tvSONote.text = "Total Quantity : $getQty"
                            totalQty = getQty.toString()
                            price = it.child("productPrice").value.toString()
                        }

                }
        }

        if (salesOrderView == "View") {
            view.etSOID.isEnabled = false
            view.etSOProductBarcode.isEnabled = false
            view.etSODate.isEnabled = false
            view.etSOPrice.isEnabled = false
            view.etSOQuantity.isEnabled = false
            view.etSOProductName.isEnabled = false
            view.etSOCustomerName.isEnabled = false
            view.etSOCustomerPhone.isEnabled = false

        } else {
            view.etSOProductBarcode.isEnabled = true
            view.etSOProductName.isEnabled = true
            view.etSOCustomerName.isEnabled = true
            view.etSOCustomerPhone.isEnabled = true
            view.etSOQuantity.isEnabled = true
            view.etSODate.isEnabled = true

        }

        view.etSOProductBarcode.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= view.etSOProductBarcode.right - view.etSOProductBarcode.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    val sharedPreferences =
                        requireActivity().getSharedPreferences(
                            "sharedPrefsSalesOrder",
                            Context.MODE_PRIVATE
                        )

                    val editor = sharedPreferences.edit()

                    editor.apply {
                        putString("getSOID", view.etSOID.text.toString())
                        putString("getSOBarcode", view.etSOProductBarcode.text.toString())
                        putString("getSOProductName", view.etSOProductName.text.toString())
                        putString("getSOCustomerName", view.etSOCustomerName.text.toString())
                        putString("getSOCustomerPhone", view.etSOCustomerPhone.text.toString())
                        putString("getSOQuantity", view.etSOQuantity.text.toString())
                        putString("getSODate", view.etSODate.text.toString())
                        putString("getSOPrice", view.etSOPrice.text.toString())
                        putString("getSOPaymentType", view.spinnerSOType.selectedItem.toString())
                        putString("getSOStatus", "Update")
                    }.apply()

                    Navigation.findNavController(view).navigate(
                        R.id.nav_searchSalesProduct
                    )

                    return@OnTouchListener true
                }
            }
            false
        })

        view.etSOQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() == "") {
                    view.etSOPrice.setText("RM 0")
                } else {
                    var calculateTotalPrice = 0.00

                    calculateTotalPrice =
                        (s.toString().toDouble()) * (price.toDouble())


                    view.etSOPrice.setText("RM ${calculateTotalPrice.toString()}")
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        view.btnUpdateEditSO.setOnClickListener {

            val etSOID = view.etSOID.text.toString().trim()
            val etSOProductBarcode = view.etSOProductBarcode.text.toString().trim()
            val etSOProductName = view.etSOProductName.text.toString().trim()
            val etSOCustomerName = view.etSOCustomerName.text.toString().trim()
            val etSOCustomerPhone = view.etSOCustomerPhone.text.toString().trim()
            val etSOQuantity = view.etSOQuantity.text.toString().trim()
            val etSODate = view.etSODate.text.toString().trim()
            val etSOPrice = view.etSOPrice.text.toString().trim()
            val spinnerSOType = view.spinnerSOType.selectedItem.toString().trim()

            if (etSOCustomerName.isEmpty()) {
                view.etSOCustomerName.error = "Customer Name Required"
                view.etSOCustomerName.requestFocus()
                return@setOnClickListener
            } else if (!formatName.containsMatchIn(etSOCustomerName)) {
                view.etSOCustomerName.error = "Customer Name only can Alphabet"
                view.etSOCustomerName.requestFocus()
                return@setOnClickListener
            } else if (!formatPhone.containsMatchIn(etSOCustomerPhone)) {
                view.etSOCustomerPhone.error =
                    "Customer Phone Wrong Format! Example: 012-3456789"
                view.etSOCustomerPhone.requestFocus()
                return@setOnClickListener
            } else if (etSOCustomerPhone.isEmpty()) {
                view.etSOCustomerPhone.error = "Customer Phone Required"
                view.etSOCustomerPhone.requestFocus()
                return@setOnClickListener
            } else if (etSOQuantity.isEmpty() || etSOQuantity.toInt() < 1) {
                view.etSOQuantity.error = "Sales Quantity Required"
                view.etSOQuantity.requestFocus()
                return@setOnClickListener
            } else if (etSOQuantity.toInt() > totalQty!!.toInt()) {
                view.etSOQuantity.error = "Cannot More Than $totalQty"
                view.etSOQuantity.requestFocus()
                return@setOnClickListener
            } else if (etSODate.isEmpty()) {
                Toast.makeText(activity, "Sales Date Required", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {

                val salesOrder = SalesOrder(
                    etSOID,
                    etSOProductBarcode,
                    etSOProductName,
                    etSOCustomerName,
                    etSOCustomerPhone,
                    etSOQuantity,
                    etSODate,
                    etSOPrice,
                    spinnerSOType
                )
                mySalesRef.child(savedWarehouse!!).child("salesOrder").child(etSOID)
                    .setValue(salesOrder)
                Toast.makeText(activity, "Successfully Added!", Toast.LENGTH_LONG).show()

                if (getSOStatus == "Update") {

                    val getBarcode = sharedPreferencesSalesOrder.getString("getSOBarcode",null)

                    val eventListener: ValueEventListener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            for (ds in snapshot.children) {
                                val barCode =
                                    ds.child("productBarcode").getValue(String::class.java)

                                if (barCode == getBarcode) {
                                    val calculatePrevQty =
                                        ds.child("productQuantity").getValue(String::class.java)!!
                                            .toInt() + salesOrderQty!!.toInt()

                                    val queryPrev: Query =
                                        getRef.child(savedWarehouse!!).child("product")
                                            .orderByChild("productName")
                                            .equalTo(
                                                ds.child("productName").getValue(String::class.java)
                                            )

                                    queryPrev.addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (childSnapshot in snapshot.children) {
                                                childSnapshot.ref.child("productQuantity")
                                                    .setValue(calculatePrevQty.toString())
                                                // might be able to do .ref. instead of .getRef().
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("Database Error", error.toString())
                                        }
                                    })
                                    break
                                }

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    }
                    checkRef =
                        database.getReference("Warehouse").child(savedWarehouse!!).child("product")
                    checkRef.addListenerForSingleValueEvent(eventListener)

                    val calculateQuantity = totalQty.toInt() - etSOQuantity.toInt()

                    val query: Query =
                        getRef.child(savedWarehouse!!).child("product").orderByChild("productName")
                            .equalTo(etSOProductName)

                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (childSnapshot in snapshot.children) {
                                childSnapshot.ref.child("productQuantity")
                                    .setValue(calculateQuantity.toString())
                                // might be able to do .ref. instead of .getRef().
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Database Error", error.toString())
                        }
                    })


                } else {
                    val calculateQuantity =
                        totalQty.toInt() - etSOQuantity.toInt() + salesOrderQty!!.toInt()

                    val query: Query =
                        getRef.child(savedWarehouse!!).child("product").orderByChild("productName")
                            .equalTo(etSOProductName)

                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (childSnapshot in snapshot.children) {
                                childSnapshot.ref.child("productQuantity")
                                    .setValue(calculateQuantity.toString())
                                // might be able to do .ref. instead of .getRef().
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Database Error", error.toString())
                        }
                    })
                }

                Navigation.findNavController(view).navigate(
                    R.id.nav_salesOrder
                )

            }

        }

        showSalesDate = view.findViewById(R.id.etSODate)
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(myCalendar)
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


        return view
    }
    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        showSalesDate.text = sdf.format(myCalendar.time)
    }

}