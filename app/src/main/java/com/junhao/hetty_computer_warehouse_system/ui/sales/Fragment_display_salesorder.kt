package com.junhao.hetty_computer_warehouse_system.ui.sales

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.SalesOrderItemAdapter
import com.junhao.hetty_computer_warehouse_system.data.SalesOrder
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_display_salesorder.*
import kotlinx.android.synthetic.main.fragment_show_product.*
import java.util.*
import kotlin.collections.ArrayList

class Fragment_display_salesorder : Fragment() {

    private lateinit var optionsMenu : Menu
    val database = FirebaseDatabase.getInstance()
    private lateinit var myRef : DatabaseReference
    private lateinit var eventListener : ValueEventListener
    var salesOrderItemList: ArrayList<SalesOrder>? = null
    var tempArrayList: ArrayList<SalesOrder>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_display_salesorder, container, false)

        setHasOptionsMenu(true)
        (activity as HomePage?)?.hideFloatingActionButton()

        val sharedPreferences : SharedPreferences = requireActivity().getSharedPreferences("sharedPrefs",
            Context.MODE_PRIVATE)

        val savedWarehouse = sharedPreferences.getString("getWarehouse",null)

        myRef = database.getReference("Warehouse").child(savedWarehouse!!)

        salesOrderItemList = arrayListOf<SalesOrder>()
        tempArrayList = arrayListOf<SalesOrder>()

        eventListener = myRef?.child("salesOrder").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }


            override fun onDataChange(snapshot: DataSnapshot) {
                salesOrderItemList!!.clear()
                tempArrayList!!.clear()

                if (snapshot!!.exists()) {

                    for (c in snapshot.children) {

                        if (c.exists()) {
                            Log.d(ContentValues.TAG, "Value is: ${c.value}")
                            val productItem = c.getValue(SalesOrder::class.java)
                            salesOrderItemList?.add(productItem!!)
                        }

                    }
                    tempArrayList!!.addAll(salesOrderItemList!!)

                    val adapter = SalesOrderItemAdapter(context!!, tempArrayList!!)

                    val recyclerView: RecyclerView = view.findViewById(R.id.recycleViewSalesOrder)

                    recyclerView?.adapter = adapter
                    adapter.setOnItemClickListener(object : SalesOrderItemAdapter.onItemClickListener {

                        override fun onItemClick(
                            salesOrderID: String,
                            salesOrderBarCode: String,
                            salesOrderDate: String,
                            salesOrderPrice: String,
                            salesOrderQty: String,
                            salesOrderPaymentType: String
                        ) {

                            val bundle = bundleOf(
                                Pair("salesOrderID", salesOrderID),
                                Pair("salesOrderBarCode", salesOrderBarCode),
                                Pair("salesOrderDate", salesOrderDate),
                                Pair("salesOrderPrice", salesOrderPrice),
                                Pair("salesOrderQty", salesOrderQty),
                                Pair("salesOrderPaymentType", salesOrderPaymentType),
                                Pair("salesOrderView","View")
                            )

                            Navigation.findNavController(view).navigate(
                                R.id.nav_updateSalesOrder,
                                bundle
                            )


                        }


                        override fun onDeleteClick(salesOrderID: String) {

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
                                        myRef.child("salesOrder").orderByChild("salesOrderID").equalTo(salesOrderID)

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

                        override fun onEditClick(
                            salesOrderID: String,
                            salesOrderBarCode: String,
                            salesOrderDate: String,
                            salesOrderPrice: String,
                            salesOrderQty: String,
                            salesOrderPaymentType: String
                        ) {
                            val bundle = bundleOf(
                                Pair("salesOrderID", salesOrderID),
                                Pair("salesOrderBarCode", salesOrderBarCode),
                                Pair("salesOrderDate", salesOrderDate),
                                Pair("salesOrderPrice", salesOrderPrice),
                                Pair("salesOrderQty", salesOrderQty),
                                Pair("salesOrderPaymentType", salesOrderPaymentType),
                                Pair("salesOrderView","Update")
                            )

                            Navigation.findNavController(view).navigate(
                                R.id.nav_updateSalesOrder,
                                bundle
                            )
                        }


                    })

                    recyclerView.setHasFixedSize(true)


                }else{

                    Toast.makeText(activity, "Not Found", Toast.LENGTH_SHORT)
                        .show()

                }

            }
        })

        return view
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        optionsMenu = menu
        optionsMenu.findItem(R.id.action_add).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onStop() {
        super.onStop()

        myRef.child("salesOrder").removeEventListener(eventListener)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.action_add ->{
                Navigation.findNavController(requireView()).navigate(R.id.nav_searchSalesProduct)

                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

    fun search(str: String) {

        salesOrderItemList!!.forEach {
            if (it.salesOrderID!!.lowercase(Locale.getDefault())
                    .contains(str) || it.salesProductBarcode!!.lowercase(Locale.getDefault())
                    .contains(str) || it.salesPrice!!.lowercase(Locale.getDefault())
                    .contains(str) || it.salesQuantity!!.lowercase(Locale.getDefault())
                    .contains(str) || it.salesDate!!.lowercase(Locale.getDefault())
                    .contains(str) || it.salesPaymentType!!.lowercase(Locale.getDefault())
                    .contains(str)
            ) {
                tempArrayList?.add(it)

            }
        }
        recycleViewSalesOrder.adapter!!.notifyDataSetChanged()

    }
    override fun onStart() {
        super.onStart()

        sv_SalesOrder.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    tempArrayList?.addAll(salesOrderItemList!!)
                    recycleViewSalesOrder.adapter!!.notifyDataSetChanged()
                }

                return true
            }

        })


    }

}