package com.junhao.hetty_computer_warehouse_system.ui.item

import android.content.ContentValues.TAG
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.ProductItemAdapter
import com.junhao.hetty_computer_warehouse_system.data.Product
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import androidx.navigation.Navigation.findNavController
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_show_product.*
import java.util.*
import kotlin.collections.ArrayList


class ShowProduct : Fragment() {

    val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Warehouse").child("warehouse1").child("product")
    var ProductItemList: ArrayList<Product>? = null
    var tempArrayList: ArrayList<Product>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_show_product, container, false)

        ProductItemList = arrayListOf<Product>()
        tempArrayList = arrayListOf<Product>()

        (activity as HomePage?)?.showFloatingActionButton()

        myRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                ProductItemList!!.clear()
                tempArrayList!!.clear()
                if (snapshot!!.exists()) {

                    for (c in snapshot.children) {

                        if (c.exists()) {
                            Log.d(TAG, "Value is: ${c.value}")
                            val productItem = c.getValue(Product::class.java)
                            ProductItemList?.add(productItem!!)
                        }

                    }
                    tempArrayList!!.addAll(ProductItemList!!)


                    val adapter = ProductItemAdapter(context!!, tempArrayList!!)

                    val recyclerView: RecyclerView = view.findViewById(R.id.recycleViewProduct)

                    recyclerView?.adapter = adapter
                    adapter.setOnItemClickListener(object : ProductItemAdapter.onItemClickListener {

                        override fun onItemClick(
                            productName: String,
                            productPrice: String,
                            productType: String,
                            productRack: String,
                            productQty: String
                        ) {
                            val bundle = bundleOf(
                                Pair("productName", productName),
                                Pair("productPrice", productPrice),
                                Pair("productType", productType),
                                Pair("productRack", productRack),
                                Pair("productQty", productQty)
                            )

                            findNavController(view).navigate(
                                R.id.action_nav_items_to_updateProduct,
                                bundle
                            )

                            Toast.makeText(
                                activity,
                                "You Clicked on item no, $productName",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                        override fun onDeleteClick(productName: String) {
                            val queryRef :Query = myRef.orderByChild("productName").equalTo(productName)

                            queryRef.addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (exist in snapshot.children){
                                        exist.ref.setValue(null)
                                    }

                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
                                }


                            })

                        }

                        override fun onLocationClick(productName: String, productRack: String) {
                            Toast.makeText(activity, "This is Location Button $productName,$productRack", Toast.LENGTH_SHORT).show()
                        }


                    })

                    recyclerView.setHasFixedSize(true)


                }

            }
        })



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    recycleViewProduct.adapter!!.notifyDataSetChanged()
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
        recycleViewProduct.adapter!!.notifyDataSetChanged()

    }


}