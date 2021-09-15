package com.junhao.hetty_computer_warehouse_system.ui.tracking

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.TrackingItemDetailsAdapter

import com.junhao.hetty_computer_warehouse_system.data.TrackingItemDetails
import kotlinx.android.synthetic.main.tracking_item_list.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class TrackingDetailsFragment : Fragment() {

    val database = FirebaseDatabase.getInstance()
    private val refWarehouse = database.getReference("Warehouse").child("warehouse1")
    private lateinit var eventListener1 : ValueEventListener
    var trackingItemDetailsList : ArrayList<TrackingItemDetails> ? = null
    var mapLatitude : Double? = null
    var mapLongitude :Double?= null
    private var location : LatLng = LatLng(0.0,0.0)

    //google map
    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tracking_details, container, false)



        /* GET VALUE OF SELECTED ITEM */
        val selectedProductName = arguments?.getString("productName")
        val selectedProductInvNo = arguments?.getString("warehouseInvNumber")

        view.findViewById<TextView>(R.id.tvTrackingNumber).text = selectedProductInvNo


        /* RETRIEVE TRACKING DETAILS FROM FIREBASE BEGIN*/
        trackingItemDetailsList = arrayListOf<TrackingItemDetails>()

        eventListener1 = refWarehouse?.child("WarehouseInventory").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot1: DataSnapshot) {

                if(snapshot1!!.exists()){
                    for (c in snapshot1.children){
                        val warehouseInvNo = c.child("warehouseInvNumber").value

                        if(warehouseInvNo == selectedProductInvNo){


                            val tvTrackingItemStatus:TextView = view.findViewById(R.id.tvTrackingStatus)
                            val tvTrackingItemDestination:TextView = view.findViewById(R.id.tvTrackingTo)
                            val tvTrackingItemQuantity:TextView = view.findViewById(R.id.tvTrackingQty)

                            tvTrackingItemStatus?.text= c.child("warehouseInvStatus").value.toString()
                            tvTrackingItemDestination?.text= c.child("warehouseInvReq").value.toString()
                            tvTrackingItemQuantity?.text= c.child("warehouseInvQty").value.toString()


                            /* RETRIEVE NESTED VALUE OF WAREHOUSE TRACKING DETAILS*/
                            val contentTrackDetails: DataSnapshot? = c?.child("warehouseTrackDetail")
                            val trackDetailsValue = contentTrackDetails?.children

                            if(contentTrackDetails != null){

                                if (trackDetailsValue != null) {
                                    for (v in trackDetailsValue) {



                                            val tid: TrackingItemDetails? = v.getValue(TrackingItemDetails::class.java)
                                            val tidLat: Double? = tid?.trackLatitude
                                            val tidLong:Double? = tid?.trackLongitude

                                            if (tid != null) {

                                                if(tidLat != null && tidLong != null){

                                                    location = LatLng(tidLat!!, tidLong!!)


                                                }
                                                trackingItemDetailsList?.add(tid)

                                            }



                                    }
                                }
                            }

                            if(location != LatLng(0.0,0.0)){

                                mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
                                mapFragment.getMapAsync {
                                    googleMap = it
                                    googleMap.addMarker(MarkerOptions().position(location).icon(
                                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,15f))

                                }


                            }else{

                                mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
                                mapFragment.requireView().visibility = View.INVISIBLE

                                val mapLayout = view.findViewById(R.id.mapLayout) as FrameLayout

                                mapLayout.setBackgroundResource(R.drawable.ic_empty_image)

                            }





                            val recyclerView: RecyclerView = view.findViewById(R.id.trackingItemRecycleViewAllDetails)

                            val adapter = TrackingItemDetailsAdapter(context!!, trackingItemDetailsList!!)
                            recyclerView?.adapter = adapter

                            recyclerView.setHasFixedSize(true)

                            /* ALLOW RECYCLE VALUE PERFORM IN REVERSE ORDER, AND SCROLLBAR AT THE BOTTOM */
                            val linearLayoutManager : LinearLayoutManager = LinearLayoutManager(activity);
                            linearLayoutManager.reverseLayout = true;
                            linearLayoutManager.stackFromEnd = true;
                            recyclerView.layoutManager = linearLayoutManager;

                        }



                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
        /* RETRIEVE TRACKING DETAILS FROM FIREBASE END*/


        return view
    }




}