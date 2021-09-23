package com.junhao.hetty_computer_warehouse_system.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.NotificationItemAdapter
import com.junhao.hetty_computer_warehouse_system.adapter.ProductItemAdapter
import com.junhao.hetty_computer_warehouse_system.data.TrackingItem
import com.junhao.hetty_computer_warehouse_system.data.TrackingItemDetails
import kotlinx.android.synthetic.main.fragment_add_staff.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*
import kotlin.collections.ArrayList

class ShowNotification : Fragment() {

    val database = FirebaseDatabase.getInstance()

    private val refWarehouse = database.getReference("Warehouse")
    var notificationItemList : ArrayList<TrackingItem> ? = null
    private lateinit var eventListener : ValueEventListener
    private lateinit var eventListener2 : ValueEventListener
    var trackDetailsID: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_notification, container, false)

        (activity as HomePage?)?.hideFloatingActionButton()
        notificationItemList = arrayListOf<TrackingItem>()


        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)

        refWarehouse?.child(savedWarehouse!!).child("product").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                notificationItemList!!.clear()
                if(snapshot!!.exists()){

                    for (c in snapshot.children){
                        val barCode = c.child("productBarcode").getValue(String::class.java)
                      refWarehouse.child(savedWarehouse!!).child("WarehouseInventory").addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot2: DataSnapshot) {
                                if(snapshot2!!.exists()) {

                                    for (w in snapshot2.children){
                                        val warehouseBarCode = w.child("warehouseInvProdBarcode").getValue(String::class.java)
                                        val warehouseInvStatus = w.child("warehouseInvStatus").getValue(String::class.java)
                                        if(warehouseBarCode == barCode && warehouseInvStatus == "Pending"){

                                            val trackingItem = c.getValue(TrackingItem::class.java)
                                            val trackingWarehouseItem = w.getValue(TrackingItem::class.java)

                                            if (trackingItem != null) {
                                                if (trackingWarehouseItem != null) {
                                                    trackingWarehouseItem.productImg = trackingItem.productImg
                                                    trackingWarehouseItem.productName = trackingItem.productName



                                                }
                                            }



                                            notificationItemList?.add(trackingWarehouseItem!!)


                                        }
                                    }



                                    val recyclerView: RecyclerView = view.findViewById(R.id.notificationItemRecycleView)


                                    val adapter = NotificationItemAdapter(context!!, notificationItemList!!)

                                    recyclerView?.adapter = adapter

                                    adapter.setOnItemClickListener(object : NotificationItemAdapter.onItemClickListener{


                                        override fun onAcceptClick(productName: String, warehouseInvQty: String, warehouseInvNumber :String) {
                                            val sharedPreferences2: SharedPreferences = requireActivity().getSharedPreferences(
                                                "sharedPrefs",
                                                Context.MODE_PRIVATE
                                            )

                                            val savedWarehouse2 = sharedPreferences2.getString("getWarehouse", null)

                                            val queryRef: Query = refWarehouse.child(savedWarehouse2!!).child("product").orderByChild("productName").equalTo(productName)

                                            queryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onDataChange(snapshot1: DataSnapshot) {
                                                    for (exist1 in snapshot1.children) {
                                                        val currentQty = exist1.child("productQuantity").getValue(String::class.java)
                                                        val currentBarCode = exist1.child("productBarcode").getValue(String::class.java)
                                                        val calculateQty = currentQty!!.toInt() - warehouseInvQty.toInt()

                                                        refWarehouse.child(savedWarehouse!!).child("WarehouseInventory").addListenerForSingleValueEvent(object : ValueEventListener{
                                                            override fun onDataChange(snapshot2: DataSnapshot) {
                                                                for (exist2 in snapshot2.children){
                                                                    val currentWarehouseBarCode = exist2.child("warehouseInvProdBarcode").getValue(String::class.java)
                                                                    val currentWarehouseInvNumber = exist2.child("warehouseInvNumber").getValue(String::class.java)

                                                                    if(currentBarCode == currentWarehouseBarCode && currentWarehouseInvNumber == warehouseInvNumber) {

                                                                        val updateStatus = mapOf<String,String>(
                                                                            "warehouseInvStatus" to "Prepared"
                                                                        )

                                                                        exist2.ref.updateChildren(updateStatus)

                                                                        val updateQty = mapOf<String,String>(
                                                                            "productQuantity" to calculateQty.toString()
                                                                        )
                                                                        exist1.ref.updateChildren(updateQty)


                                                                        Toast.makeText(activity, "Request Accepted", Toast.LENGTH_LONG)
                                                                            .show()


                                                                        var trackDetailsLatitude :Double = 0.0
                                                                        var trackDetailsLongitude :Double = 0.0

                                                                        exist2.ref.child("warehouseTrackDetail").orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener{
                                                                            @SuppressLint("SimpleDateFormat")
                                                                            @RequiresApi(Build.VERSION_CODES.O)
                                                                            override fun onDataChange(snapshot3: DataSnapshot) {
                                                                                for(childSnapshot in snapshot3.children){
                                                                                    trackDetailsID  = childSnapshot.key!!


                                                                                    if(trackDetailsID == "1"){
                                                                                        trackDetailsLatitude = childSnapshot.child("trackLatitude").getValue(Double::class.java)!!
                                                                                        trackDetailsLongitude = childSnapshot.child("trackLongitude").getValue(Double::class.java)!!

                                                                                        trackDetailsID = (Integer.parseInt(trackDetailsID) + 1).toString()
                                                                                    }

                                                                                }

                                                                                val formatter = SimpleDateFormat("dd-MM-yyyy")
                                                                                val formatterTime = SimpleDateFormat("HH:mm")
                                                                                val now = Date(System.currentTimeMillis() + 28800 * 1000)
                                                                                val trackDetailsDate = formatter.format(now)
                                                                                val trackDetailsTime= formatterTime.format(now)


                                                                                val trackDetailsDesc = "Prepared"


                                                                                val trackingItemDetailsList = TrackingItemDetails(trackDetailsDate, trackDetailsDesc, trackDetailsTime,trackDetailsLatitude,trackDetailsLongitude,"2")


                                                                                exist2.ref.child("warehouseTrackDetail").child(
                                                                                    trackDetailsID!!
                                                                                ).setValue(trackingItemDetailsList)


                                                                            }

                                                                            override fun onCancelled(error: DatabaseError) {

                                                                            }

                                                                        })


                                                                    }



                                                                }
                                                            }

                                                            override fun onCancelled(error: DatabaseError) {
                                                                TODO("Not yet implemented")
                                                            }

                                                        })




                                                    }


                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT)
                                                                .show()
                                                }

                                            })

                                        }

                                        override fun onDeclineClick(warehouseInvNumber: String) {


                                            val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
                                                "sharedPrefs",
                                                Context.MODE_PRIVATE
                                            )

                                            val savedWarehouse = sharedPreferences.getString("getWarehouse", null)

                                            val queryRef: Query = refWarehouse.child(savedWarehouse!!).child("WarehouseInventory").child(warehouseInvNumber)

                                            queryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {

                                                    snapshot.ref.removeValue()

                                                    Toast.makeText(activity, "Request Declined", Toast.LENGTH_SHORT)
                                                        .show()

                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }


                                            })



                                        }
                                    })



                                    recyclerView.setHasFixedSize(true)



                                }


                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }



                        })


                    }




                }

                //
            }


        })

        return view
    }
//    override fun onStop() {
//        super.onStop()
//        Log.d("TAG", "onStopShow")
//
//        refWarehouse.child("product").removeEventListener(eventListener)
//        refWarehouse.child("WarehouseInventory").removeEventListener(eventListener2)
//    }

}