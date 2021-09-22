package com.junhao.hetty_computer_warehouse_system.ui.sales


import android.Manifest.permission
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.Staff
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_add_staff.*
import kotlinx.android.synthetic.main.fragment_add_staff.view.*
import kotlinx.android.synthetic.main.fragment_select_report.*
import kotlinx.android.synthetic.main.fragment_select_report.view.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.os.Environment
import android.Manifest.permission.READ_EXTERNAL_STORAGE

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent

import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.Manifest.permission.READ_EXTERNAL_STORAGE

import androidx.test.core.app.ApplicationProvider.getApplicationContext

import androidx.core.content.ContextCompat

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE


class SelectReport : Fragment() {

    val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private lateinit var eventListener: ValueEventListener
    var spinnerItemList: ArrayList<String>? = null
    var datePatternFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_select_report, container, false)

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedStaffName = sharedPreferences.getString("getStaffName", null)
        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)
        spinnerItemList = arrayListOf<String>()
        myRef = database.getReference("Warehouse").child(savedWarehouse!!)

        (activity as HomePage?)?.hideFloatingActionButton()

        eventListener = myRef?.child("product").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for (c in snapshot.children) {

                    if (c.exists()) {

                        val productItem = c.child("productName").getValue(String::class.java)
                        spinnerItemList?.add(productItem!!)
                    }

                }
                val spinner = view.findViewById<Spinner>(R.id.spinnerReportSalesProduct)
                val spinnerArrayAdapter = ArrayAdapter<String>(
                    requireActivity(),
                    R.layout.support_simple_spinner_dropdown_item,
                    spinnerItemList!!
                )

                spinner.adapter = spinnerArrayAdapter

            }
        })

        view.btnReportSavePrint.setOnClickListener {

            var myPdfDocument: PdfDocument = PdfDocument()
            var paint: Paint = Paint()
            val myPageInfo: PdfDocument.PageInfo =
                PdfDocument.PageInfo.Builder(250, 350, 1).create()
            var forLinePaint: Paint = Paint()

            forLinePaint.color = Color.rgb(0,50,250)

            val myPage: PdfDocument.Page = myPdfDocument.startPage(myPageInfo)

            val spinnerProductName = spinnerReportSalesProduct.selectedItem.toString()
            val canvas: Canvas = myPage.canvas

            paint.textSize = 15.5f
            paint.color = Color.rgb(0, 50, 250)

            canvas.drawText("Hetty Warehouse System", 20F, 20F, paint)
            paint.textSize = 8.5f

            if (savedWarehouse == "warehouse1") {
                canvas.drawText("Warehouse 1", 20F, 40F, paint)
            } else if (savedWarehouse == "warehouse2") {
                canvas.drawText("Warehouse 2", 20F, 40F, paint)
            } else {
                canvas.drawText("Warehouse 3", 20F, 40F, paint)
            }
            canvas.drawText("Generated By : $savedStaffName", 20F, 55F, paint)

            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0F)
            forLinePaint.strokeWidth = 2F

            canvas.drawLine(20F, 65F, 230F, 65F, forLinePaint)

            canvas.drawText("Product Name : $spinnerProductName", 20F, 80F, paint)

            canvas.drawLine(20F, 90F, 230F, 90F, forLinePaint)

            canvas.drawText("Purchase : ", 20F, 105F, paint)

            database.getReference("Warehouse").child(savedWarehouse!!).child("salesOrder").get().addOnSuccessListener {

                val eventListener: ValueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        var totalPrice = 0.00
                        var count = 0
                        for (ds in dataSnapshot.children) {
                            val salesProductName =
                                ds.child("salesProductName").getValue(String::class.java)
                            val salesCustomerName =
                                ds.child("salesCustomerName").getValue(String::class.java)
                            val salesPaymentType =
                                ds.child("salesPaymentType").getValue(String::class.java)
                            val salesQuantity =
                                ds.child("salesQuantity").getValue(String::class.java)
                            val salesPrice =
                                ds.child("salesPrice").getValue(String::class.java)
                            totalPrice += salesPrice!!.split(" ")[1]!!.toDouble()
                            count++
                            if (salesProductName == spinnerProductName) {

                                paint.textAlign = Paint.Align.LEFT
                                canvas.drawText("$salesCustomerName", 20F, 135F, paint)
                                canvas.drawText("$salesPaymentType", 70F, 135F, paint)

                                paint.textAlign = Paint.Align.RIGHT

                                canvas.drawText("$salesQuantity", 120F, 135F, paint)
                                canvas.drawText(
                                    "RM $salesPrice",
                                    170F,
                                    135F,
                                    paint
                                )

                            }

                        }
                        paint.textAlign = Paint.Align.LEFT

                        canvas.drawLine(20F, 210F, 230F, 210F, forLinePaint)
                        canvas.drawText("Total : ", 120F, 225F, paint)

                        paint.textAlign = Paint.Align.RIGHT
                        canvas.drawText("RM $totalPrice", 230F, 225F, paint)

                        canvas.drawText(
                            "Date Generated : ${datePatternFormat.format(Date().time)}",
                            20F,
                            260F,
                            paint
                        )
                        canvas.drawText("Total Customer : $count", 20F, 275F, paint)

                        paint.textAlign = Paint.Align.CENTER
                        paint.textSize = 12F

                        canvas.drawText("Thank You!", canvas.width / 2F, 320F, paint)

                        myPdfDocument.finishPage(myPage)
                        val file: File =
                            File(activity!!.getExternalFilesDir("/"), "HettyWarehouseSystem123.pdf")

                        try {
                            myPdfDocument.writeTo(FileOutputStream(file))
                            Toast.makeText(activity,"Successfully Created PDF!",Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
                        }

                        myPdfDocument.close()
                        Navigation.findNavController(view!!).navigate(
                            R.id.nav_reportSalesOrder
                        )
                    }


                    override fun onCancelled(databaseError: DatabaseError) {}
                }
                database.getReference("Warehouse").child(savedWarehouse!!).child("salesOrder").addListenerForSingleValueEvent(eventListener)


            }


        }


        return view
    }

    override fun onStop() {
        super.onStop()

        myRef.child("product").removeEventListener(eventListener)
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
            150
        )


    }


}