package com.junhao.hetty_computer_warehouse_system.ui.item

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
import com.junhao.hetty_computer_warehouse_system.adapter.WarehouseAdapter
import com.junhao.hetty_computer_warehouse_system.data.Product
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_search_warehouse_product.*
import kotlinx.android.synthetic.main.fragment_search_warehouse_product.view.*
import java.util.*
import kotlin.collections.ArrayList


class SearchWarehouseProduct : Fragment() {

    private lateinit var myRef: DatabaseReference
    val database = FirebaseDatabase.getInstance()
    var ProductItemList: ArrayList<Product>? = null
    var tempArrayList: ArrayList<Product>? = null
    private lateinit var eventListener: ValueEventListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_warehouse_product, container, false)
        (activity as HomePage?)?.hideFloatingActionButton()
        // val sharedPreferences : SharedPreferences = requireActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        val sharedPreferencesWarehouse: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefsWarehouse",
            Context.MODE_PRIVATE
        )

        val getSelectedWarehouse =
            sharedPreferencesWarehouse.getString("getSelectedWarehouse", null)

        //val savedWarehouse = sharedPreferences.getString("getWarehouse",null)

        myRef = database.getReference("Warehouse").child(getSelectedWarehouse!!)


        ProductItemList = arrayListOf<Product>()
        tempArrayList = arrayListOf<Product>()

        eventListener = myRef?.child("product").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }


            override fun onDataChange(snapshot: DataSnapshot) {
                ProductItemList!!.clear()
                tempArrayList!!.clear()

                for (c in snapshot.children) {

                    if (c.exists()) {
                        Log.d(ContentValues.TAG, "Value is: ${c.value}")
                        val productItem = c.getValue(Product::class.java)
                        ProductItemList?.add(productItem!!)
                    }

                }
                tempArrayList!!.addAll(ProductItemList!!)


                val adapter = WarehouseAdapter(context!!, tempArrayList!!)

                val recyclerView: RecyclerView = view.findViewById(R.id.recycleViewWarehouseProduct)

                recyclerView?.adapter = adapter
                adapter.setOnItemClickListener(object : WarehouseAdapter.onItemClickListener {

                    override fun onItemClick(
                        productName: String,
                        productType: String,
                        productBarcode: String,
                        productQty: String
                    ) {
                        val bundle = bundleOf(
                            Pair("productWarehouseName", productName),
                            Pair("productWarehouseType", productType),
                            Pair("productWarehouseBarcode", productBarcode),
                            Pair("productWarehouseQty", productQty),
                            Pair("getSelectWarehouse", getSelectedWarehouse)
                        )

                        Navigation.findNavController(view).navigate(
                            R.id.action_nav_searchWarehouseProduct_to_nav_sentWarehouseProduct,
                            bundle
                        )

                        Toast.makeText(
                            activity,
                            "You Clicked on item no, $productName",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                })

                recyclerView.setHasFixedSize(true)


            }
        })

        view.imgWarehouseScanner.setOnClickListener {
            val scanner = IntentIntegrator.forSupportFragment(this)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()

        }


        return view
    }

    override fun onStart() {
        super.onStart()

        searchView_warehouseProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    recycleViewWarehouseProduct.adapter!!.notifyDataSetChanged()
                }

                return true
            }

        })


    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG", "onStopShow")

        myRef.child("product").removeEventListener(eventListener)
    }

    fun search(str: String) {

        ProductItemList!!.forEach {
            if (it.productName!!.lowercase(Locale.getDefault())
                    .contains(str) || it.productType!!.lowercase(Locale.getDefault())
                    .contains(str) || it.productBarcode!!.lowercase(Locale.getDefault())
                    .contains(str) || it.productQuantity!!.lowercase(Locale.getDefault())
                    .contains(str)
            ) {
                tempArrayList?.add(it)

            }
        }
        recycleViewWarehouseProduct.adapter!!.notifyDataSetChanged()

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
                    Toast.makeText(activity, "Scanned: " + result.contents, Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }

        }

    }


}