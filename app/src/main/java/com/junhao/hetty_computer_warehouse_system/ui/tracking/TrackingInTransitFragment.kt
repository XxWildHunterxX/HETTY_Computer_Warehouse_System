package com.junhao.hetty_computer_warehouse_system.ui.tracking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.TrackingItemAdapter
import com.junhao.hetty_computer_warehouse_system.data.TrackingItem
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage


class TrackingInTransitFragment : Fragment() {

    val database = FirebaseDatabase.getInstance()
    private val refWarehouse = database.getReference("Warehouse").child("warehouse1")
    var trackingItemList : ArrayList<TrackingItem> ? = null
    private lateinit var eventListener : ValueEventListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view = inflater.inflate(R.layout.fragment_tracking_intransit, container, false)


        /* (activity as HomePage?)?.showFloatingActionButton() */
        (activity as HomePage?)?.hideFloatingActionButton()


        trackingItemList = arrayListOf<TrackingItem>()



        eventListener = refWarehouse?.child("product").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                trackingItemList!!.clear()
                if(snapshot!!.exists()){

                    for (c in snapshot.children){
                        val barCode = c.child("productBarcode").getValue(String::class.java)

                        refWarehouse.child("WarehouseInventory").addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot2: DataSnapshot) {
                                if(snapshot2!!.exists()) {

                                    for (w in snapshot2.children){
                                        val warehouseBarCode = w.child("warehouseInvProdBarcode").getValue(String::class.java)
                                        val warehouseInvStatus = w.child("warehouseInvStatus").getValue(String::class.java)
                                        if(warehouseBarCode == barCode){

                                            if(warehouseInvStatus == "In Transit"){

                                                val trackingItem = c.getValue(TrackingItem::class.java)
                                                val trackingWarehouseItem = w.getValue(TrackingItem::class.java)

                                                if (trackingItem != null) {
                                                    if (trackingWarehouseItem != null) {
                                                        trackingWarehouseItem.productImg = trackingItem.productImg
                                                        trackingWarehouseItem.productName = trackingItem.productName

                                                    }
                                                }

                                                trackingItemList?.add(trackingWarehouseItem!!)


                                            }

                                        }
                                    }



                                    val recyclerView: RecyclerView = view.findViewById(R.id.trackingItemRecycleViewInTransit)


                                    val adapter = TrackingItemAdapter(context!!, trackingItemList!!)
                                    adapter.setOnItemClickListener(object: TrackingItemAdapter.onItemClickListener{
                                        override fun onItemClick(
                                            productName: String,
                                            warehouseInvNumber:String

                                        ) {
                                            val bundle = bundleOf(Pair("productName",productName),Pair("warehouseInvNumber",warehouseInvNumber))

                                            Navigation.findNavController(view).navigate(R.id.action_nav_warehouseTracking_to_trackingDetailsFragment,bundle)

                                        }


                                    })
                                    recyclerView?.adapter = adapter



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


}