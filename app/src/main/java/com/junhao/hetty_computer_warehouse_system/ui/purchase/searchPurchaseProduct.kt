package com.junhao.hetty_computer_warehouse_system.ui.purchase

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.PurchaseProductAdapter
import com.junhao.hetty_computer_warehouse_system.data.Product
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_search_purchase_product.*
import kotlinx.android.synthetic.main.fragment_search_purchase_product.view.*
import java.util.*
import kotlin.collections.ArrayList


class searchPurchaseProduct : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_search_purchase_product, container, false)

        (activity as HomePage?)?.hideFloatingActionButton()

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )
        ProductItemList = arrayListOf<Product>()
        tempArrayList = arrayListOf<Product>()
        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)

        myRef = database.getReference("Warehouse").child(savedWarehouse!!)

        eventListener = myRef?.child("product").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }


            override fun onDataChange(snapshot: DataSnapshot) {
                ProductItemList!!.clear()
                tempArrayList!!.clear()

                recycleViewPurchaseProduct.visibility = View.VISIBLE;

                for (c in snapshot.children) {

                    if (c.exists()) {
                        Log.d(ContentValues.TAG, "Value is: ${c.value}")
                        val productItem = c.getValue(Product::class.java)
                        ProductItemList?.add(productItem!!)
                    }

                }
                tempArrayList!!.addAll(ProductItemList!!)


                val adapter = PurchaseProductAdapter(context!!, tempArrayList!!)

                val recycleViewPurchaseProduct: RecyclerView = view.findViewById(R.id.recycleViewPurchaseProduct)

                recycleViewPurchaseProduct?.adapter = adapter
                adapter.setOnItemClickListener(object : PurchaseProductAdapter.onItemClickListener {

                    override fun onItemClick(
                        productName: String
                    ) {
                            val bundle = bundleOf(
                                Pair("productName", productName)
                            )

                            Navigation.findNavController(view).navigate(
                                R.id.nav_purchase_create,
                                bundle
                            )

                    }

                })

                recycleViewPurchaseProduct.setHasFixedSize(true)

            }
        })

        view.imgPurchaseScanner.setOnClickListener {
            val scanner = IntentIntegrator.forSupportFragment(this)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()

        }


        return view
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
        recycleViewPurchaseProduct.adapter!!.notifyDataSetChanged()

    }
    override fun onStart() {
        super.onStart()

        searchView_Purchase.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    recycleViewPurchaseProduct.adapter!!.notifyDataSetChanged()
                }

                return true
            }

        })


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

                                            val bundle = bundleOf(
                                                Pair("productName", c.child("productName").getValue(String::class.java))
                                            )

                                            found = true

                                            Navigation.findNavController(view!!).navigate(
                                                R.id.nav_purchase_create,
                                                bundle
                                            )

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