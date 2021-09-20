package com.junhao.hetty_computer_warehouse_system.ui.item

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import com.google.firebase.database.*
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.TrackingItemDetails
import com.junhao.hetty_computer_warehouse_system.data.WarehouseInventory
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_staff.view.*
import kotlinx.android.synthetic.main.fragment_sent_warehouse_product.view.*
import java.text.SimpleDateFormat
import java.util.*

class SentWarehouseProduct : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private lateinit var warehouseRef: DatabaseReference
    private lateinit var getRef: DatabaseReference
    private lateinit var warehouse: List<String>
    var oldWarehouseInventoryID: String = ""
    lateinit var pd : ProgressDialog


    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sent_warehouse_product, container, false)

        (activity as HomePage?)?.hideFloatingActionButton()

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )
        pd = ProgressDialog(activity)

        val productWarehouseName = arguments?.getString("productWarehouseName")
        val productWarehouseBarcode = arguments?.getString("productWarehouseBarcode")
        val productWarehouseQty = arguments?.getString("productWarehouseQty")
        val getSelectWarehouse = arguments?.getString("getSelectWarehouse")

        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)
        warehouseRef = database.getReference("Warehouse")
        myRef = database.getReference("Warehouse").child(getSelectWarehouse!!).child("product")

        getRef = database.getReference("Warehouse").child(getSelectWarehouse!!)



        if (getSelectWarehouse == "warehouse1") {
            warehouse = listOf(
                "Warehouse 1"
            )
        } else if (getSelectWarehouse == "warehouse2") {
            warehouse = listOf(
                "Warehouse 2"
            )
        } else {
            warehouse = listOf(
                "Warehouse 3"
            )
        }

        val spinner = view.findViewById<Spinner>(R.id.spinnerWarehouse)
        val spinnerArrayAdapter = ArrayAdapter<String>(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item,
            warehouse
        )

        spinner.adapter = spinnerArrayAdapter

        view.etWarehouseProductBarcode.setText(productWarehouseBarcode.toString())
        view.etWarehouseProductName.setText(productWarehouseName.toString())
        view.tvTotalQuantity.text = "Total Quantity : $productWarehouseQty"


        val productImg: ImageView = view.findViewById(R.id.addImgWarehouseProduct)

        myRef.child(productWarehouseName.toString()).get().addOnSuccessListener {
            if (it.exists()) {
                var imageUri: String? = null
                imageUri = it.child("productImg").value.toString()
                Picasso.get().load(imageUri).into(productImg)


            }

        }.addOnFailureListener {

            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show()
        }


        view.btnSaveWarehouse.setOnClickListener {
            pd.show()
            pd.setMessage("Loading...")
            val warehouseQty = view.tfWarehouseProductQuantity.text.toString()

            if (warehouseQty.isEmpty() || warehouseQty.toInt() < 1) {
                view.tfWarehouseProductQuantity.error = "Quantity Product Sent Required"
                view.tfWarehouseProductQuantity.requestFocus()
                pd.dismiss()
                return@setOnClickListener
            } else if (warehouseQty.toInt() > productWarehouseQty!!.toInt()) {
                view.tfWarehouseProductQuantity.error = "Cannot More Than $productWarehouseQty"
                view.tfWarehouseProductQuantity.requestFocus()
                pd.dismiss()
                return@setOnClickListener
            } else {

                val getWarehouseRequest: String
                val getWarehouseInvNum: String

                if (view.spinnerWarehouse.selectedItem.toString() == "Warehouse 1") {
                    getWarehouseRequest = "warehouse1"
                } else if (view.spinnerWarehouse.selectedItem.toString() == "Warehouse 2") {
                    getWarehouseRequest = "warehouse2"
                } else {
                    getWarehouseRequest = "warehouse3"
                }

                warehouseRef.child(getWarehouseRequest).child("WarehouseInventory").orderByKey()
                    .limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            oldWarehouseInventoryID = childSnapshot.key!!.drop(2)
                            oldWarehouseInventoryID = "PL"+(Integer.parseInt(oldWarehouseInventoryID) + 1).toString()

                        }


                        if(oldWarehouseInventoryID == ""){

                            oldWarehouseInventoryID = "PL100001"

                        }

                        val formatter = SimpleDateFormat("dd-MM-yyyy")
                        val formatterTime = SimpleDateFormat("HH:mm")
                        val now = Date(System.currentTimeMillis() + 28800 * 1000)
                        val trackDate = formatter.format(now)
                        val trackDesc = "Pending"
                        val trackNoOrder = "1"

                        val trackTime = formatterTime.format(now)

                        val warehouseInventory = WarehouseInventory(
                            oldWarehouseInventoryID,
                            view.etWarehouseProductBarcode.text.toString(),
                            view.tfWarehouseProductQuantity.text.toString(),
                            savedWarehouse,
                            "Pending"
                        )
                        warehouseRef.child(getWarehouseRequest).child("WarehouseInventory")
                            .child(oldWarehouseInventoryID)
                            .setValue(warehouseInventory)


                        getRef.get().addOnSuccessListener {
                            val getWarehouseLatitude = it.child("latitude").value.toString()
                            val getWarehouseLongitude = it.child("longitude").value.toString()

                            val warehouseTrackDetail = TrackingItemDetails(
                                trackDate,
                                trackDesc,
                                trackTime,
                                getWarehouseLatitude.toDouble(),
                                getWarehouseLongitude.toDouble(),
                                trackNoOrder
                            )
                            warehouseRef.child(getWarehouseRequest).child("WarehouseInventory")
                                .child(oldWarehouseInventoryID).child("warehouseTrackDetail")
                                .child("1")
                                .setValue(warehouseTrackDetail)

                        }
                        Toast.makeText(activity, "Successfully Added!", Toast.LENGTH_LONG).show()
                        pd.dismiss()
                        Navigation.findNavController(view).navigate(R.id.action_nav_sentWarehouseProduct_to_nav_searchWarehouseProduct)


                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })


            }

        }

        return view
    }


}