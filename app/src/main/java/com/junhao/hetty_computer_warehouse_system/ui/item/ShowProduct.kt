package com.junhao.hetty_computer_warehouse_system.ui.item

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.ProductItemAdapter
import com.junhao.hetty_computer_warehouse_system.adapter.TrackingItemAdapter
import com.junhao.hetty_computer_warehouse_system.data.Product
import com.junhao.hetty_computer_warehouse_system.data.TrackingItem
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage





class ShowProduct : Fragment() {

    val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Warehouse").child("warehouse1").child("product")
    var ProductItemList: ArrayList<Product>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_show_product, container, false)

        ProductItemList = arrayListOf<Product>()

        /* (activity as HomePage?)?.showFloatingActionButton() */
        (activity as HomePage?)?.showFloatingActionButton()

        myRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("TAG", "ondatachange")
                if (snapshot!!.exists()) {
                    Log.d("TAG", "child")

                    for (c in snapshot.children) {
                        Log.d("TAG", "child1")

                        if (c.exists()) {
                            Log.d(TAG, "Value is: ${c.value}")
                            val productItem = c.getValue(Product::class.java)
                            ProductItemList?.add(productItem!!)
                        }

                    }

                    val adapter = ProductItemAdapter(context!!, ProductItemList!!)

                    val recyclerView: RecyclerView = view.findViewById(R.id.recycleViewProduct)

                    recyclerView?.adapter = adapter
                    recyclerView.setHasFixedSize(true)

                }

            }
        })



        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}