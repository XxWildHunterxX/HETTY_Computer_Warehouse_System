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
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
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
    private val mySalesRef = database.getReference("Warehouse")
    private val getRef = database.getReference("Warehouse")
    private lateinit var showSalesDate: TextView
    var oldSalesID :String = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_addsales, container, false)

        val formatName = Regex("^[a-zA-Z ]+\$")
        val formatPhone = Regex("^((01)[0-46-9]-)*[0-9]{7,8}\$")
        val productName = arguments?.getString("productName")
        val productPrice = arguments?.getString("productPrice")
        val productQty = arguments?.getString("productQty")

        view.etSalesProductName.setText(productName)
        view.etSalesPrice.setText("RM $productPrice")
        view.tvNote.text = "Total Quantity : $productQty"
        view.etSalesQuantity.setText("1")

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)

        myRef.child(savedWarehouse!!).child("product")
        (activity as HomePage?)?.hideFloatingActionButton()

        myRef.child(savedWarehouse!!).child("product").child(productName.toString()).get()
            .addOnSuccessListener {

                view.etSalesProductBarcode.setText(it.child("productBarcode").value.toString())

            }

        mySalesRef.child(savedWarehouse!!).child("salesOrder").orderByKey().limitToLast(1).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(childSnapshot in snapshot.children){
                    oldSalesID = childSnapshot.key!!.drop(2)
                    oldSalesID = (Integer.parseInt(oldSalesID) + 1).toString()

                    view.etSalesOrderID.setText("SO$oldSalesID")
                    Toast.makeText(
                        activity,
                        oldSalesID,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

        if (view.etSalesOrderID.text.toString() == "") {

            view.etSalesOrderID.setText("SO1001")

        }


        view.btnSaveSales.setOnClickListener {

            val etSalesOrderID = view.etSalesOrderID.text.toString().trim()
            val etProductBarcode = view.etSalesProductBarcode.text.toString().trim()
            val etProductName = view.etSalesProductName.text.toString().trim()
            val etCustomerName = view.etSalesCustomerName.text.toString().trim()
            val etCustomerPhone = view.etSalesCustomerPhone.text.toString().trim()
            val etSalesQty = view.etSalesQuantity.text.toString().trim()
            val etSalesDate = view.etSalesDate.text.toString().trim()
            val etSalesPrice = view.etSalesPrice.text.toString().trim()
            val spinnerSalesType = view.spinnerSalesType.selectedItem.toString().trim()

            if (etCustomerName.isEmpty()) {
                view.etSalesCustomerName.error = "Customer Name Required"
                view.etSalesCustomerName.requestFocus()
                return@setOnClickListener
            } else if (!formatName.containsMatchIn(etCustomerName)) {
                view.etSalesCustomerName.error = "Customer Name only can Alphabet"
                view.etSalesCustomerName.requestFocus()
                return@setOnClickListener
            } else if (!formatPhone.containsMatchIn(etCustomerPhone)) {
                view.etSalesCustomerPhone.error =
                    "Customer Phone Wrong Format! Example: 012-3456789"
                view.etSalesCustomerPhone.requestFocus()
                return@setOnClickListener
            } else if (etCustomerPhone.isEmpty()) {
                view.etSalesCustomerPhone.error = "Customer Phone Required"
                view.etSalesCustomerPhone.requestFocus()
                return@setOnClickListener
            } else if (etSalesQty.isEmpty() || etSalesQty.toInt() < 1) {
                view.etSalesQuantity.error = "Sales Quantity Required"
                view.etSalesQuantity.requestFocus()
                return@setOnClickListener
            } else if (etSalesQty.toInt() > productQty!!.toInt()) {
                view.etSalesQuantity.error = "Cannot More Than $productQty"
                view.etSalesQuantity.requestFocus()
                return@setOnClickListener
            } else if (etSalesDate.isEmpty()) {
                Toast.makeText(activity, "Sales Date Required", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {

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
                mySalesRef.child(savedWarehouse!!).child("salesOrder").child(etSalesOrderID).setValue(salesOrder)
                Toast.makeText(activity, "Successfully Added!", Toast.LENGTH_LONG).show()

                val calculateQuantity = productQty.toInt() - etSalesQty.toInt()

                val query : Query = getRef.child(savedWarehouse!!).child("product").orderByChild("productName").equalTo(etProductName)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            childSnapshot.ref.child("productQuantity").setValue(calculateQuantity.toString())
                            // might be able to do .ref. instead of .getRef().
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Database Error", error.toString())
                    }
                })

                requireActivity().onBackPressed();

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

        view.etSalesQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString() == "") {
                    view.etSalesPrice.setText("RM 0")
                } else {
                    val calculateTotalPrice =
                        (s.toString().toDouble()) * (productPrice.toString().toDouble())

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