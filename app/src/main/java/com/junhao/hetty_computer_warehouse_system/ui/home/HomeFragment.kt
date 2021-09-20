package com.junhao.hetty_computer_warehouse_system.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.junhao.hetty_computer_warehouse_system.R
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    val database = FirebaseDatabase.getInstance()

    private val refWarehouse = database.getReference("Warehouse")
    private lateinit var eventListener : ValueEventListener

    private lateinit var homeViewModel: HomeViewModel

        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

            val view = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)


        /* val textView: TextView = binding.textHome */
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
         /*   textView.text = it */
        })


        return view
    }

    override fun onStart() {
        (activity as HomePage?)?.showFloatingActionButton()
        Log.i("Lifecycle", "onStartFragment")

        super.onStart()

        //SET NAVIGATION FOR TRACKING CARD VIEW WHEN SELECTED
        val cvPending = view?.findViewById<CardView>(R.id.to_be_pending_cardview)
        val cvPrepared = view?.findViewById<CardView>(R.id.to_be_prepared_cardview)
        val cvTransit = view?.findViewById<CardView>(R.id.to_be_shipped_cardview)
        val cvDelivered = view?.findViewById<CardView>(R.id.to_be_delivered_cardview)

        cvPending?.setOnClickListener{

            val bundle = bundleOf(
                Pair("trackingTab", "Pending")
            )

            Navigation.findNavController(requireView()).navigate(
                R.id.nav_warehouseTracking,
                bundle
            )


        }

        cvPrepared?.setOnClickListener{

            val bundle = bundleOf(
                Pair("trackingTab", "Prepared")
            )

            Navigation.findNavController(requireView()).navigate(
                R.id.nav_warehouseTracking,
                bundle
            )
        }

        cvTransit?.setOnClickListener{

            val bundle = bundleOf(
                Pair("trackingTab", "In Transit")
            )

            Navigation.findNavController(requireView()).navigate(
                R.id.nav_warehouseTracking,
                bundle
            )
        }

        cvDelivered?.setOnClickListener{

            val bundle = bundleOf(
                Pair("trackingTab", "Delivered")
            )

            Navigation.findNavController(requireView()).navigate(
                R.id.nav_warehouseTracking,
                bundle
            )
        }


        val tvToBePendingQty = view?.findViewById<TextView>(R.id.tv_to_be_pending_qty)
        var countPending = 0
        var countPrepared = 0
        var countShipped = 0
        var countDelivered = 0
        var countAll = 0
        var countTotalQty = 0
        var countUnitQty = 0

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)

        eventListener = refWarehouse?.child(savedWarehouse!!).child("product").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot!!.exists()){
                    for (c in snapshot.children){
                        val productQty = c.child("productQuantity").getValue(String::class.java)
                        countTotalQty += productQty!!.toInt()
                        countUnitQty += 1
                        tv_total_qty?.text = countTotalQty.toString()
                        tv_no_of_product_qty?.text = countUnitQty.toString()

                        val barCode = c.child("productBarcode").getValue(String::class.java)
                        refWarehouse.child(savedWarehouse!!).child("WarehouseInventory").addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot2: DataSnapshot) {
                                if(snapshot2!!.exists()) {
                                    for (w in snapshot2.children){
                                        val warehouseBarCode = w.child("warehouseInvProdBarcode").getValue(String::class.java)
                                        val warehouseInvStatus = w.child("warehouseInvStatus").getValue(String::class.java)
                                        if(warehouseBarCode == barCode){
                                            if(warehouseInvStatus == "Pending"){

                                                countPending += 1

                                                tvToBePendingQty?.text = countPending.toString()
                                            }else if(warehouseInvStatus == "Prepared"){

                                                countPrepared += 1
                                                tvToBePreparedQty?.text = countPrepared.toString()

                                            }else if(warehouseInvStatus == "In Transit"){

                                                countShipped += 1
                                                tvToBeShippedQty?.text = countShipped.toString()
                                            }else if(warehouseInvStatus == "Delivered"){

                                                countDelivered += 1
                                                tvToBeDeliveredQty?.text = countDelivered.toString()
                                            }

                                            countAll += 1

                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }
            }
        })





    }
}