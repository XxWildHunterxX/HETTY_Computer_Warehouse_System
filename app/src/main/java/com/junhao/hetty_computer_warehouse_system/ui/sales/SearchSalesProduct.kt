package com.junhao.hetty_computer_warehouse_system.ui.sales

import android.content.ContentValues
import android.content.Context
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
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.SalesOrderProductAdapter
import com.junhao.hetty_computer_warehouse_system.data.Product
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_search_sales_product.*
import java.util.*
import kotlin.collections.ArrayList


class SearchSalesProduct : Fragment() {

    val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    var ProductItemList: ArrayList<Product>? = null
    var tempArrayList: ArrayList<Product>? = null
    private lateinit var eventListener: ValueEventListener


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
                        val bundle = bundleOf(
                            Pair("productName", productName),
                            Pair("productPrice", productPrice),
                            Pair("productQty", productQty)
                        )

                        Navigation.findNavController(view).navigate(
                            R.id.fragment_addsales,
                            bundle
                        )

                        Toast.makeText(
                            activity,
                            "You Clicked on item no, $productName",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                })

                recycleViewSalesOrderProduct.setHasFixedSize(true)

            }
        })

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


}