package com.junhao.hetty_computer_warehouse_system.ui.tracking

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.TrackingItemDetailsAdapter
import com.junhao.hetty_computer_warehouse_system.data.TrackingItemDetails
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import java.net.URL


class TrackingDetailsFragment : Fragment(), OnMapReadyCallback {

    val database = FirebaseDatabase.getInstance()
    private val refWarehouse = database.getReference("Warehouse").child("warehouse1")
    private lateinit var eventListener1 : ValueEventListener
    var trackingItemDetailsList : ArrayList<TrackingItemDetails> ? = null

    private var originLatitude: Double = 0.0
    private var originLongitude: Double = 0.0
    private var originLocation = LatLng(0.0, 0.0)


    private var destinationLatitude: Double = 3.0567
    private var destinationLongitude: Double = 101.5851
    private var destinationLocation = LatLng(destinationLatitude, destinationLongitude)

    //google map
    lateinit var googleMap: GoogleMap

    companion object {
        var mapFragment : SupportMapFragment?=null
        val TAG: String = MapFragment::class.java.simpleName
        fun newInstance() = MapFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tracking_details, container, false)


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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

                                                originLatitude = tidLat
                                                originLongitude = tidLong

                                            }
                                            trackingItemDetailsList?.add(tid)

                                        }



                                    }
                                }
                            }

                            if(originLatitude != 0.0 && originLongitude != 0.0){


                                //mapFragment = (activity?.supportFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment?)!!

                                //mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!

                                mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

                                mapFragment!!.getMapAsync(this@TrackingDetailsFragment)


                            }else{

                                mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
                                mapFragment!!.requireView().visibility = View.INVISIBLE

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



    }

    override fun onMapReady(p0: GoogleMap?) {

        if (p0 != null) {
            googleMap = p0
        }
        var LatLongB = LatLngBounds.Builder()

            //ORIGIN LOCATION
            originLocation = LatLng(originLatitude, originLongitude)

            googleMap.addMarker(MarkerOptions().position(originLocation).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).icon(
                bitmapDescriptorFromVector(requireActivity(), R.drawable.ic_car)
            ))


            //DESTINATION LOCATION
            destinationLocation = LatLng(destinationLatitude, destinationLongitude)
            googleMap.addMarker(MarkerOptions().position(destinationLocation).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            ))

           //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation,15f))

            //val sydney = LatLng(-34.0, 151.0)
            //val opera = LatLng(-33.9320447,151.1597271)
            //googleMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            //googleMap!!.addMarker(MarkerOptions().position(opera).title("Opera House"))


            // Declare polyline object and set up color and width
            val options = PolylineOptions()
            options.color(Color.rgb(252, 3, 57))
            options.width(5f)

            // build URL to call API
            //val url = getURL(originLocation, destinationLocation)
            val url = getURL(originLocation, destinationLocation)


            async {
                // Connect to URL, download content and convert into string asynchronously
                val result = URL(url).readText()
                uiThread {
                    // When API call is done, create parser and convert into JsonObjec
                    val parser: Parser = Parser()
                    val stringBuilder: StringBuilder = StringBuilder(result)
                    val json: JsonObject = parser!!.parse(stringBuilder) as JsonObject
                    // get to the correct element in JsonObject
                    val routes = json.array<JsonObject>("routes")
                    val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                    val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!)
                    }

                    // Add  points to polyline and bounds

                    options.add(originLocation)
                    LatLongB.include(originLocation)
                    for (point in polypts)  {
                        options.add(point)
                        LatLongB.include(point)
                    }
                    options.add(destinationLocation)
                    LatLongB.include(destinationLocation)
                    // build bounds
                    val bounds = LatLongB.build()
                    // add polyline to the map
                    googleMap!!.addPolyline(options)
                    // show map with route centered
                    googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            }


    }

    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val mode = "mode=driving"
        val key = "key=AIzaSyBCRxPA8q_a1Z7ebk_LcvmH9rTEo6gY7aI"
        val params = "$origin&$dest&$sensor&$mode&$key"

        return "https://maps.googleapis.com/maps/api/directions/json?$params"

    }


    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }








}


