package com.junhao.hetty_computer_warehouse_system.ui.sales

import android.app.AlertDialog
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
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.ProductItemAdapter
import com.junhao.hetty_computer_warehouse_system.data.Product
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_show_product.*


class SearchSalesProduct : Fragment() {

    val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    var ProductItemList: ArrayList<Product>? = null
    var tempArrayList: ArrayList<Product>? = null
    private lateinit var eventListener: ValueEventListener
    private lateinit var optionsMenu: Menu


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

                recycleViewProduct.visibility = View.VISIBLE;

                for (c in snapshot.children) {

                    if (c.exists()) {
                        Log.d(ContentValues.TAG, "Value is: ${c.value}")
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

                        Navigation.findNavController(view).navigate(
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

                        val dialog: AlertDialog =
                            AlertDialog.Builder(activity).setTitle("Delete")
                                .setMessage("Are You Sure Want to Delete?")
                                .setPositiveButton("Yes", null)
                                .setNegativeButton("No", null).show()

                        val positiveButton: Button =
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)

                        positiveButton.setOnClickListener(object : View.OnClickListener {
                            override fun onClick(v: View?) {

                                val queryRef: Query =
                                    myRef.child("product").orderByChild("productName")
                                        .equalTo(productName)

                                queryRef.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (exist in snapshot.children) {
                                            exist.ref.setValue(null)
                                        }

                                        Toast.makeText(
                                            activity,
                                            "Delete Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                })
                                dialog.dismiss()
                            }
                        })


                    }

                    override fun onLocationClick(productName: String, productRack: String) {
                        Toast.makeText(
                            activity,
                            "This is Location Button $productName,$productRack",
                            Toast.LENGTH_SHORT
                        ).show()

                        val bundle = bundleOf(
                            Pair("rackProductName", productName),
                            Pair("productRack", productRack)
                        )
                        Navigation.findNavController(view).navigate(
                            R.id.nav_rackLocation,
                            bundle
                        )


                    }


                })

                recyclerView.setHasFixedSize(true)

            }
        })

        return view
    }


}