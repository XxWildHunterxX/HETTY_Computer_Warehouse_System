package com.junhao.hetty_computer_warehouse_system.ui.sales

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.navigation.Navigation
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.integration.android.IntentIntegrator
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_update_product.view.*
import kotlinx.android.synthetic.main.fragment_update_sales_order.view.*


class UpdateSalesOrder : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private lateinit var productRef: DatabaseReference
    private lateinit var paymentType: List<String>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update_sales_order, container, false)

        (activity as HomePage?)?.hideFloatingActionButton()

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

        if(getSOStatus == "Update"){

            val getSOProductName = sharedPreferencesSalesOrder.getString("getSOProductName", null)
            val getSOBarcode = sharedPreferencesSalesOrder.getString("getSOBarcode", null)
            val getSOPrice = sharedPreferencesSalesOrder.getString("getSOPrice", null)
            view.etSOProductName.setText(getSOProductName.toString())
            //view.etSOCustomerPhone.setText(snapshot.child("salesCustomerPhone").value.toString())
            //view.etSOCustomerName.setText(snapshot.child("salesCustomerName").value.toString())

        }else{
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
                        requireActivity().getSharedPreferences("sharedPrefsSalesOrder", Context.MODE_PRIVATE)

                    val editor = sharedPreferences.edit()

                    editor.apply {
                        putString("getSOID", view.etSOID.text.toString())
                        putString("getSOBarcode",view.etSOProductBarcode.text.toString())
                        putString("getSOProductName",view.etSOProductName.text.toString())
                        putString("getSOCustomerName",view.etSOCustomerName.text.toString())
                        putString("getSOCustomerPhone",view.etSOCustomerPhone.text.toString())
                        putString("getSOQuantity",view.etSOQuantity.text.toString())
                        putString("getSODate",view.etSODate.text.toString())
                        putString("getSOPrice",view.etSOPrice.text.toString())
                        putString("getSOPaymentType",view.spinnerSOType.selectedItem.toString())
                        putString("getSOStatus","Update")
                    }.apply()

                    Navigation.findNavController(view).navigate(
                        R.id.nav_searchSalesProduct
                    )



                    return@OnTouchListener true
                }
            }
            false
        })



        return view
    }


}