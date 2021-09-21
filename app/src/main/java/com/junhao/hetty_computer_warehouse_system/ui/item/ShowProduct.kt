package com.junhao.hetty_computer_warehouse_system.ui.item

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
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
    private lateinit var myRef :DatabaseReference
    var ProductItemList: ArrayList<Product>? = null
    var tempArrayList: ArrayList<Product>? = null
    private lateinit var eventListener : ValueEventListener
    private lateinit var optionsMenu : Menu

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_show_product, container, false)

        val sharedPreferences : SharedPreferences = requireActivity().getSharedPreferences("sharedPrefs",
            Context.MODE_PRIVATE)

        val savedWarehouse = sharedPreferences.getString("getWarehouse",null)

        myRef = database.getReference("Warehouse").child(savedWarehouse!!)

        ProductItemList = arrayListOf<Product>()
        tempArrayList = arrayListOf<Product>()
        val emptyView : ImageView = view.findViewById(R.id.imgEmptyProduct)
        setHasOptionsMenu(true)
        (activity as HomePage?)?.hideFloatingActionButton()
        //myRef?.child("warehouse1").child("product").addValueEventListener(eventListener)
        eventListener = myRef?.child("product").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }


            override fun onDataChange(snapshot: DataSnapshot) {
               ProductItemList!!.clear()
                tempArrayList!!.clear()

                    if (snapshot!!.exists()) {
                        recycleViewProduct.visibility = View.VISIBLE;
                        emptyView.visibility = View.GONE;

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
                                            myRef.child("product").orderByChild("productName").equalTo(productName)

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
                                findNavController(view).navigate(
                                    R.id.nav_rackLocation,
                                    bundle
                                )


                            }


                        })

                        recyclerView.setHasFixedSize(true)


                    }else{

                        recycleViewProduct.visibility = View.GONE;
                        emptyView.visibility = View.VISIBLE;

                    }

            }
        })
       // myRef?.child("warehouse1").child("product").addListenerForSingleValueEvent(eventListener)


        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        optionsMenu = menu
        optionsMenu.findItem(R.id.action_add).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        
        when(item.itemId){

            R.id.action_add ->{
                findNavController(requireView()).navigate(R.id.nav_add_item)

                return true
            }
            
        }
        
        return super.onOptionsItemSelected(item)
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

    override fun onStop() {
        super.onStop()
        Log.d("TAG","onStopShow")

        myRef.child("product").removeEventListener(eventListener)
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