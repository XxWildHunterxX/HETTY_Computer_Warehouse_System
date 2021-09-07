package com.junhao.hetty_computer_warehouse_system.ui.item

import android.content.ContentValues.TAG
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.product_item.view.*


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

                if (snapshot!!.exists()) {

                    for (c in snapshot.children) {

                        if (c.exists()) {
                            Log.d(TAG, "Value is: ${c.value}")
                            val productItem = c.getValue(Product::class.java)
                            ProductItemList?.add(productItem!!)
                        }

                    }


                    val adapter = ProductItemAdapter(context!!, ProductItemList!!)

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
                                Pair("productPrice",productPrice),
                                Pair("productType",productType),
                                Pair("productRack", productRack),
                                Pair("productQty",productQty)
                            )

                            findNavController(view).navigate(R.id.action_nav_items_to_updateProduct,bundle)
                            /*
                              val fragment: Fragment = Fragment_Add_Item()
                              val fragmentManager = activity!!.supportFragmentManager
                              val fragmentTransaction = fragmentManager.beginTransaction()
                              fragmentTransaction.replace(R.id.nav_host_fragment_content_home_page2, fragment)
                              fragmentTransaction.addToBackStack(null)
                              fragmentTransaction.commit()
  */
                            Toast.makeText(
                                activity,
                                "You Clicked on item no, $productName",
                                Toast.LENGTH_SHORT
                            ).show()

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

}