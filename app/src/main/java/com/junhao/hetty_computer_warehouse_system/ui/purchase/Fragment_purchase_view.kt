package com.junhao.hetty_computer_warehouse_system.ui.purchase

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.renderscript.Sampler
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.PurchaseAdapter
import com.junhao.hetty_computer_warehouse_system.data.Purchase
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_purchase_view.*
import java.util.*
import kotlin.collections.ArrayList


class Fragment_purchase_view : Fragment() {
var database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("Warehouse").child("warehouse1")
    var purchaseArrayList : ArrayList<Purchase> ?= null
    var tempArrayList : ArrayList<Purchase> ?= null
    private lateinit var eventListener:ValueEventListener
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_purchase_view, container, false)
        (activity as HomePage?)?.hideFloatingActionButton()

        purchaseArrayList = arrayListOf<Purchase>()
        tempArrayList = arrayListOf<Purchase>()
            getData()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun getData(){
        eventListener=myRef?.child("Purchase").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                purchaseArrayList!!.clear()
                tempArrayList!!.clear()
                if(snapshot!!.exists()) {
                    for (purchaseSnapshot in snapshot.children) {
                        val purchase = purchaseSnapshot.getValue(Purchase::class.java)
                        purchaseArrayList?.add(purchase!!)

                    }
                    tempArrayList!!.addAll(purchaseArrayList!!)

                    val adapter = PurchaseAdapter(context!!, tempArrayList!!)
                    val recyclerView: RecyclerView = view!!.findViewById(R.id.purchaseRecycleList)
                    recyclerView?.adapter = adapter
                    recyclerView.setHasFixedSize(true)
                    adapter.setOnItemClickListener((object:PurchaseAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val selectedid = purchaseArrayList!![position].purchaseID
                            val bundle = bundleOf(
                                Pair("purchaseID", selectedid)
                            )
                            Navigation.findNavController(view!!).navigate(
                                R.id.nav_purchase_view_details,
                                bundle
                            )
                            Toast.makeText(activity, "You selected purchaseID : $selectedid", Toast.LENGTH_LONG).show()
                        }

                        override fun onDeleteClick(position: Int) {
                            AlertDialog.Builder(requireContext()).also{
                                it.setTitle(getString(R.string.delete_confirmation))
                                it.setPositiveButton(getString(R.string.yes),DialogInterface.OnClickListener{dialog,id ->
                                    deletePurchase(purchaseArrayList!![position].purchaseID!!)
                                    purchaseArrayList!!.removeAt(position)
                                    adapter.notifyItemChanged(position)

                            })
                                it.setNegativeButton("No",DialogInterface.OnClickListener{dialog,id->
                                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                                })
                            }.create().show()

                        }
                        override fun onUpdateClick(position: Int) {
                            val purchaseID = purchaseArrayList!![position].purchaseID!!
                            val bundle = bundleOf(
                                Pair("purchaseID", purchaseID)
                            )

                            Navigation.findNavController(view!!).navigate(
                                R.id.nav_purchase_update,
                                bundle
                            )
                        }
                    }))
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


    override fun onStop() {
        super.onStop()
        myRef.child("Purchase").removeEventListener(eventListener)
    }

    fun deletePurchase(deletePurID :String){
        myRef.child("Purchase").child(deletePurID!!).setValue(null).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(activity, "Purchase $deletePurID deleted successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        tf_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    tempArrayList?.addAll(purchaseArrayList!!)
                    purchaseRecycleList.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })
    }

    fun search(str: String) {
        purchaseArrayList!!.forEach {
            if (it.purchaseID!!.lowercase(Locale.getDefault()).contains(str)
                || it.purProductName!!.lowercase(Locale.getDefault()).contains(str)
                || it.status!!.lowercase(Locale.getDefault()).contains(str)
                || it.supplierName!!.lowercase(Locale.getDefault()).contains(str)
            ) {
                tempArrayList?.add(it)
            }
        }
        purchaseRecycleList.adapter!!.notifyDataSetChanged()

    }

}