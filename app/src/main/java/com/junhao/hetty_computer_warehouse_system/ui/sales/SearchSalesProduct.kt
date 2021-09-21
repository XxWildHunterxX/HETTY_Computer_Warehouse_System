package com.junhao.hetty_computer_warehouse_system.ui.sales

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.SalesOrderProductAdapter
import com.junhao.hetty_computer_warehouse_system.data.Product
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_search_sales_product.*
import kotlinx.android.synthetic.main.fragment_search_sales_product.view.*
import java.util.*
import kotlin.collections.ArrayList


class SearchSalesProduct : Fragment() {

    val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private var getSOStatus : String? = null
    var ProductItemList: ArrayList<Product>? = null
    var tempArrayList: ArrayList<Product>? = null
    private lateinit var eventListener: ValueEventListener
    private var found: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_sales_product, container, false)

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
        getSOStatus = sharedPreferencesSalesOrder.getString("getSOStatus", null)

        val getSOID = sharedPreferencesSalesOrder.getString("getSOID", null)
        val getSOBarcode = sharedPreferencesSalesOrder.getString("getSOBarcode", null)
        val getSOProductName = sharedPreferencesSalesOrder.getString("getSOProductName", null)
        val getSOCustomerName = sharedPreferencesSalesOrder.getString("getSOCustomerName", null)
        val getSOCustomerPhone = sharedPreferencesSalesOrder.getString("getSOCustomerPhone", null)
        val getSOQuantity = sharedPreferencesSalesOrder.getString("getSOQuantity", null)
        val getSODate = sharedPreferencesSalesOrder.getString("getSODate", null)
        val getSOPrice = sharedPreferencesSalesOrder.getString("getSOPrice", null)
        val getSOPaymentType = sharedPreferencesSalesOrder.getString("getSOPaymentType", null)

        sharedPreferencesSalesOrder.edit().remove("getSOStatus").apply()

        myRef = database.getReference("Warehouse").child(savedWarehouse!!)

        ProductItemList = arrayListOf<Product>()
        tempArrayList = arrayListOf<Product>()

        eventListener = myRef?.child("product").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }


            override fun onDataChange(snapshot: DataSnapshot) {
                ProductItemList!!.clear()
                tempArrayList!!.clear()

                recycleViewSalesOrderProduct.visibility = View.VISIBLE;

                for (c in snapshot.children) {

                    if (c.exists()) {
                        Log.d(ContentValues.TAG, "Value is: ${c.value}")
                        val productItem = c.getValue(Product::class.java)
                        ProductItemList?.add(productItem!!)
                    }

                }
                tempArrayList!!.addAll(ProductItemList!!)


                val adapter = SalesOrderProductAdapter(context!!, tempArrayList!!)

                val recycleViewSalesOrderProduct: RecyclerView = view.findViewById(R.id.recycleViewSalesOrderProduct)

                recycleViewSalesOrderProduct?.adapter = adapter
                adapter.setOnItemClickListener(object : SalesOrderProductAdapter.onItemClickListener {

                    override fun onItemClick(
                        productName: String,
                        productPrice: String,
                        productQty: String
                    ) {

                        if(getSOStatus == "Update"){

                                val bundle = bundleOf(
                                    Pair("productName", productName),
                                    Pair("salesOrderPrice", productPrice),
                                    Pair("productQty", productQty),
                                    Pair("salesOrderDate", getSODate),
                                    Pair("salesOrderQty", getSOQuantity),
                                    Pair("salesOrderPaymentType", getSOPaymentType),
                                    Pair("getSOCustomerName", getSOCustomerName),
                                    Pair("getSOCustomerPhone", getSOCustomerPhone),
                                    Pair("salesOrderID", getSOID)
                                )
                                sharedPreferencesSalesOrder.edit().putString("getSOStatus","Update").apply()

                                Navigation.findNavController(view).navigate(
                                    R.id.nav_updateSalesOrder,
                                    bundle
                                )

                        }else{
                            val bundle = bundleOf(
                                Pair("productName", productName),
                                Pair("productPrice", productPrice),
                                Pair("productQty", productQty)
                            )
                            sharedPreferencesSalesOrder.edit().putString("getSOStatus","Add").apply()

                            Navigation.findNavController(view).navigate(
                                R.id.fragment_addsales,
                                bundle
                            )
                        }


                    }

                })

                recycleViewSalesOrderProduct.setHasFixedSize(true)

            }
        })

        view.imgSalesOrderScanner.setOnClickListener {
            val scanner = IntentIntegrator.forSupportFragment(this)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()

        }

        return view
    }
    override fun onStart() {
        super.onStart()

        searchView_SalesOrder.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                tempArrayList?.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    search(searchText)
                } else {

                    tempArrayList!!.clear()
                    tempArrayList?.addAll(ProductItemList!!)
                    recycleViewSalesOrderProduct.adapter!!.notifyDataSetChanged()
                }

                return true
            }

        })


    }
    fun search(str: String) {

        ProductItemList!!.forEach {
            if (it.productName!!.lowercase(Locale.getDefault())
                    .contains(str) || it.productType!!.lowercase(Locale.getDefault())
                    .contains(str) || it.productPrice!!.lowercase(Locale.getDefault())
                    .contains(str) || it.productRack!!.lowercase(Locale.getDefault())
                    .contains(str) || it.productQuantity!!.lowercase(Locale.getDefault())
                    .contains(str)
            ) {
                tempArrayList?.add(it)

            }
        }
        recycleViewSalesOrderProduct.adapter!!.notifyDataSetChanged()

    }
    override fun onStop() {
        super.onStop()
        Log.d("TAG","onStopShow")

        myRef.child("product").removeEventListener(eventListener)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    //tfProductBarcode.setText(result.contents)
                    eventListener =
                        myRef?.child("product").addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {

                                for (c in snapshot.children) {
                                    val barcode =
                                        c.child("productBarcode").getValue(String::class.java)
                                    if (barcode == result.contents) {

                                        if(getSOStatus == "Update"){

                                            val sharedPreferencesSalesOrder: SharedPreferences = requireActivity().getSharedPreferences(
                                                "sharedPrefsSalesOrder",
                                                Context.MODE_PRIVATE
                                            )
                                            val getSOID = sharedPreferencesSalesOrder.getString("getSOID", null)
                                            val getSOCustomerName = sharedPreferencesSalesOrder.getString("getSOCustomerName", null)
                                            val getSOCustomerPhone = sharedPreferencesSalesOrder.getString("getSOCustomerPhone", null)
                                            val getSOQuantity = sharedPreferencesSalesOrder.getString("getSOQuantity", null)
                                            val getSODate = sharedPreferencesSalesOrder.getString("getSODate", null)
                                            val getSOPaymentType = sharedPreferencesSalesOrder.getString("getSOPaymentType", null)

                                            val bundle = bundleOf(
                                                Pair("productName", c.child("productName").getValue(String::class.java)),
                                                Pair("salesOrderPrice", c.child("productPrice").getValue(String::class.java)),
                                                Pair("productQty", c.child("productQuantity").getValue(String::class.java)),
                                                Pair("salesOrderDate", getSODate),
                                                Pair("salesOrderQty", getSOQuantity),
                                                Pair("salesOrderPaymentType", getSOPaymentType),
                                                Pair("getSOCustomerName", getSOCustomerName),
                                                Pair("getSOCustomerPhone", getSOCustomerPhone),
                                                Pair("salesOrderID", getSOID)
                                            )
                                            sharedPreferencesSalesOrder.edit().putString("getSOStatus","Update").apply()

                                            found = true

                                            Navigation.findNavController(view!!).navigate(
                                                R.id.nav_updateSalesOrder,
                                                bundle
                                            )


                                        }else{
                                            val bundle = bundleOf(
                                                Pair("productName", c.child("productName").getValue(String::class.java)),
                                                Pair("productPrice", c.child("productPrice").getValue(String::class.java)),
                                                Pair("productQty", c.child("productQuantity").getValue(String::class.java))
                                            )

                                            found = true

                                            Navigation.findNavController(view!!).navigate(
                                                R.id.fragment_addsales,
                                                bundle
                                            )
                                        }
                                        break
                                    } else {
                                        found = false
                                    }

                                }
                                if(!found){
                                    Toast.makeText(activity, "Product Not Existed!", Toast.LENGTH_LONG)
                                        .show()
                                }


                            }
                        })


                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }

        }

    }


}