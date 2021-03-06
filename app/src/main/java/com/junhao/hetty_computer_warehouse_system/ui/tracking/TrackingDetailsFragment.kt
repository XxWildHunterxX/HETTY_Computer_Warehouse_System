package com.junhao.hetty_computer_warehouse_system.ui.tracking

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.TrackingItemDetailsAdapter
import com.junhao.hetty_computer_warehouse_system.data.TrackingItemDetails
import kotlinx.android.synthetic.main.fragment_tracking_details.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import java.net.URL
import com.junhao.hetty_computer_warehouse_system.data.TrackingItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.maps.model.Marker
import com.junhao.hetty_computer_warehouse_system.data.Product
import com.junhao.hetty_computer_warehouse_system.data.WarehouseInventory


class TrackingDetailsFragment : Fragment(), OnMapReadyCallback {

    val database = FirebaseDatabase.getInstance()

    private val refWarehouse = database.getReference("Warehouse")
    private lateinit var eventListener : ValueEventListener
    var trackingItemDetailsList : ArrayList<TrackingItemDetails> ? = null

    private var originLatitude: Double = 0.0
    private var originLongitude: Double = 0.0
    private var originLocation = LatLng(0.0, 0.0)


    private var destinationLatitude: Double = 0.0
    private var destinationLongitude: Double = 0.0
    private var destinationLocation = LatLng(destinationLatitude, destinationLongitude)

    //google map
    lateinit var googleMap: GoogleMap
    private var markerOrigin: Marker? = null
    private var markerDestination: Marker? =null

    private var LatLongB = LatLngBounds.Builder()
    private var options = PolylineOptions()

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val pERMISSION_ID = 42


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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val btnLatestLocation = view.findViewById<Button>(R.id.btnLatestLocation)
        val btnReachDestination = view.findViewById<Button>(R.id.btnReachDestination)


        btnLatestLocation.setOnClickListener{


            getLastLocation()

        }

        btnReachDestination.setOnClickListener{

            reachDestination()
        }



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

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)

        eventListener = refWarehouse?.child(savedWarehouse!!).child("WarehouseInventory").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot1: DataSnapshot) {
                trackingItemDetailsList!!.clear()

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

                            /* RETRIEVE NESTED VALUE OF WAREHOUSE TRACKING DETAILS */
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

                            /* RETRIEVE DESTINATION WAREHOUSE FOR GOOGLE MAP BEGIN */
                            val trackingItemDestination = c.child("warehouseInvReq").value.toString()

                            refWarehouse?.addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot2: DataSnapshot) {
                                    if(snapshot2!!.exists()){
                                        for (w in snapshot2.children){

                                            if(trackingItemDestination == w.key.toString()){

                                                destinationLatitude = w.child("latitude").value as Double
                                                destinationLongitude = w.child("longitude").value as Double


                                            }

                                        }

                                        // IF ORIGIN AND DESTINATION OBTAINED, RUN GOOGLE MAP
                                        if(originLatitude != 0.0 && originLongitude != 0.0 && destinationLatitude != 0.0 && destinationLongitude != 0.0){


                                            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

                                            mapFragment!!.getMapAsync(this@TrackingDetailsFragment)




                                        }else{

                                            mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
                                            mapFragment!!.requireView().visibility = View.INVISIBLE

                                            val mapLayout = view.findViewById(R.id.mapLayout) as FrameLayout

                                            mapLayout.setBackgroundResource(R.drawable.ic_empty_image)
                                        }

                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }



                            })




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
    // Get current location
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {

                        googleMap.clear()
                        markerOrigin?.remove()
                        markerDestination?.remove()
                        googleMap!!.addPolyline(options).remove()
                        originLatitude = location.latitude
                        originLongitude = location.longitude
                        originLocation = LatLng(originLatitude, originLongitude)

                        UpdateCurrentLocationToTrackDetails(originLocation)

                        //addPolyLine(originLocation, destinationLocation)
                        for(i in 1..50){
                            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                            mapFragment!!.getMapAsync(this@TrackingDetailsFragment)// googleMap!!.addPolyline(options).remove()
                            googleMap.clear()
                            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                            mapFragment!!.getMapAsync(this@TrackingDetailsFragment)// googleMap!!.addPolyline(options).remove()
                        }


                    }
                }
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()


                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }

    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    // If current location could not be located, use last location
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            originLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)



        }
    }


    // function to check if GPS is on
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // Check if location permissions are
    // granted to the application
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // Request permissions if not granted before
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            pERMISSION_ID
        )
    }

    // What must happen when permission is granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == pERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    private fun UpdateCurrentLocationToTrackDetails(originLocation: LatLng) {

        val getTrackingNumber = requireActivity().findViewById<TextView>(R.id.tvTrackingNumber)
        val info: String = getTrackingNumber.text.toString()

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)

        var trackDetailsID : String = ""

        refWarehouse.child(savedWarehouse!!).child("WarehouseInventory").child(info).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot4: DataSnapshot) {

                refWarehouse.child(savedWarehouse!!).child("WarehouseInventory").child(info).child("warehouseInvStatus").setValue("In Transit")

                refWarehouse?.child(savedWarehouse!!).child("WarehouseInventory").child(info).child("warehouseTrackDetail").orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener{
                    @SuppressLint("SimpleDateFormat")
                    override fun onDataChange(snapshot3: DataSnapshot) {
                        for(childSnapshot in snapshot3.children){
                            trackDetailsID  = childSnapshot.key.toString()

                            trackDetailsID = (Integer.parseInt(trackDetailsID) + 1).toString()

                        }

                        val formatter = SimpleDateFormat("dd-MM-yyyy")
                        val formatterTime = SimpleDateFormat("HH:mm")
                        val now = Date(System.currentTimeMillis())
                        val trackDetailsDate = formatter.format(now)
                        val trackDetailsTime= formatterTime.format(now)


                        val trackDetailsDesc = "In Transit"


                        val trackingItemDetailsList = TrackingItemDetails(trackDetailsDate, trackDetailsDesc, trackDetailsTime,
                            originLocation.latitude, originLocation.longitude,trackDetailsID)


                        refWarehouse?.child(savedWarehouse!!).child("WarehouseInventory").child(info).child("warehouseTrackDetail").child(
                            trackDetailsID!!
                        ).setValue(trackingItemDetailsList)


                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })





    }

    private fun reachDestination(){

        val getTrackingNumber = requireActivity().findViewById<TextView>(R.id.tvTrackingNumber)
        val info: String = getTrackingNumber.text.toString()

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)

        var trackDetailsID : String = ""

        refWarehouse.child(savedWarehouse!!).child("WarehouseInventory").child(info).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot5: DataSnapshot) {
                refWarehouse.child(savedWarehouse!!).child("WarehouseInventory").child(info).child("warehouseInvStatus").setValue("Delivered")

                val trackingWarehouseReq = snapshot5.getValue(TrackingItem::class.java)?.warehouseInvReq
                val trackingWarehouseBarcode = snapshot5.getValue(WarehouseInventory::class.java)?.warehouseInvProdBarcode

                refWarehouse.child(trackingWarehouseReq!!).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot111: DataSnapshot) {
                        val trackingWarehouseReqLatitude = snapshot111.child("latitude").getValue(Double::class.java)!!
                        val trackingWarehouseReqLongitude = snapshot111.child("longitude").getValue(Double::class.java)!!


                        refWarehouse?.child(savedWarehouse!!).child("WarehouseInventory").child(info).child("warehouseTrackDetail").orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener{
                            @SuppressLint("SimpleDateFormat")
                            override fun onDataChange(snapshot6: DataSnapshot) {
                                for(childSnapshot in snapshot6.children){
                                    trackDetailsID  = childSnapshot.key.toString()

                                    trackDetailsID = (Integer.parseInt(trackDetailsID) + 1).toString()

                                }

                                val formatter = SimpleDateFormat("dd-MM-yyyy")
                                val formatterTime = SimpleDateFormat("HH:mm")
                                val now = Date(System.currentTimeMillis())
                                val trackDetailsDate = formatter.format(now)
                                val trackDetailsTime= formatterTime.format(now)


                                val trackDetailsDesc = "Delivered"


                                val trackingItemDetailsList = TrackingItemDetails(trackDetailsDate, trackDetailsDesc, trackDetailsTime,trackingWarehouseReqLatitude!!.toDouble(),trackingWarehouseReqLongitude!!.toDouble(),trackDetailsID)


                                refWarehouse?.child(savedWarehouse!!).child("WarehouseInventory").child(info).child("warehouseTrackDetail").child(
                                    trackDetailsID!!
                                ).setValue(trackingItemDetailsList)


                                googleMap.clear()
                                markerOrigin?.remove()
                                markerDestination?.remove()

                                originLatitude = trackingWarehouseReqLatitude.toDouble()
                                originLongitude = trackingWarehouseReqLongitude!!.toDouble()
                                originLocation = LatLng(originLatitude, originLongitude )
                                googleMap!!.addPolyline(options).remove()




                                for(i in 1..50){
                                    mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                                    mapFragment!!.getMapAsync(this@TrackingDetailsFragment)// googleMap!!.addPolyline(options).remove()
                                    googleMap.clear()
                                    mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                                    mapFragment!!.getMapAsync(this@TrackingDetailsFragment)// googleMap!!.addPolyline(options).remove()
                                }

                                //INSERT PRODUCT TO THE DESTINATION WAREHOUSE

                                refWarehouse?.child(savedWarehouse!!).child("product").addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot1: DataSnapshot) {
                                        for (c in snapshot1.children) {
                                            val productBarCode =
                                                c.child("productBarcode").value

                                            if (productBarCode == trackingWarehouseBarcode) {

                                                val productName = c.child("productName").value.toString()
                                                var productRack : String? = null
                                                val productType = c.child("productType").value.toString()
                                                val productPrice = c.child("productPrice").value.toString()
                                                val tvProductQuantity : TextView = requireView().findViewById(R.id.tvTrackingQty)
                                                val productQuantity = tvProductQuantity.text.toString()
                                                val productAlertQty = "0"
                                                val productAlertChk = "false"
                                                val productImg = c.child("productImg").value.toString()

                                               // var nextLetter = "A-"
                                                var nextDigit = 0
                                                var rackNumber : String? = null
                                                var found = 0
                                                var count = 0
                                                    refWarehouse.child(trackingWarehouseReq).child("product").addValueEventListener(object : ValueEventListener{
                                                        override fun onDataChange(snapshot9: DataSnapshot) {
                                                            if(snapshot9.exists()){
                                                                loop@for(i in 1 until 100){
                                                                loop2@for (z in snapshot9.children){

                                                                    val existRack = z.child("productRack").getValue(String::class.java)

                                                                    if(count ==0){
                                                                        nextDigit += 1
                                                                        if(nextDigit < 10){

                                                                            if("A-0$nextDigit" != existRack && "B-0$nextDigit" != existRack && "C-0$nextDigit" != existRack){
                                                                                rackNumber = "A-0$nextDigit"
                                                                                found = 1
                                                                                break@loop2
                                                                            }
                                                                        }else{
                                                                            if("A-$nextDigit" != existRack && "B-$nextDigit" != existRack && "C-$nextDigit" != existRack){
                                                                                rackNumber = "A-$nextDigit"
                                                                                found = 1
                                                                                break@loop2
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                    if(found == 1){
                                                                        productRack = rackNumber

                                                                        val productList = Product(productName, trackingWarehouseBarcode, productRack,productType,productPrice,productQuantity,productAlertQty,productAlertChk,productImg)

                                                                        refWarehouse.child(trackingWarehouseReq).addListenerForSingleValueEvent(object : ValueEventListener{
                                                                            override fun onDataChange(snapshot: DataSnapshot) {

                                                                                refWarehouse?.child(trackingWarehouseReq).child("product").child(productName).setValue(productList)

                                                                            }

                                                                            override fun onCancelled(error: DatabaseError) {
                                                                                TODO("Not yet implemented")
                                                                            }

                                                                        })
                                                                        count=1
                                                                        found = 0
                                                                        break@loop
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

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })





                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }



    override fun onMapReady(p0: GoogleMap?) {


        if (p0 != null) {
            googleMap = p0
        }
        googleMap.clear()

        //ORIGIN LOCATION
        originLocation = LatLng(originLatitude, originLongitude)

        //DESTINATION LOCATION
        destinationLocation = LatLng(destinationLatitude, destinationLongitude)



        placeMarkerOnMap(originLocation, destinationLocation)



            addPolyLine(originLocation, destinationLocation)


    }
    private fun addPolyLine(originLocation: LatLng, destinationLocation: LatLng){

       // googleMap!!.clear()

        //LatLongB = LatLngBounds.Builder()

        // Declare polyline object and set up color and width
        //options = PolylineOptions()
       // options.color(Color.rgb(252, 3, 57))
        //options.width(5f)

       // polylineFinal?.remove()

        // build URL to call API
        //val url = getURL(originLocation, destinationLocation)
        val url = getURL(originLocation, destinationLocation)


        googleMap!!.addPolyline(options).remove()

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

                googleMap!!.addPolyline(options).remove()

                LatLongB = LatLngBounds.Builder()

                options = PolylineOptions()
                options.color(Color.rgb(252, 3, 57))
                options.width(5f)

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

    private fun placeMarkerOnMap(originLocation: LatLng, destinationLocation: LatLng) {


        markerOrigin?.remove()

        markerDestination?.remove()

        val markerOptionsOrigin = MarkerOptions().position(originLocation).icon(
            bitmapDescriptorFromVector(requireContext(),R.drawable.ic_truck_red_top_view)
        )
        markerOrigin = googleMap.addMarker(markerOptionsOrigin)!!

        val markerOptionsDestination = MarkerOptions().position(destinationLocation).icon(
            bitmapDescriptorFromVector(requireContext(),R.drawable.ic_custom_red_marker)
        )

        markerDestination = googleMap.addMarker(markerOptionsDestination)!!
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

    override fun onStop() {
        super.onStop()
        Log.d("TAG","onStopShow")

        refWarehouse.removeEventListener(eventListener)

    }








}


