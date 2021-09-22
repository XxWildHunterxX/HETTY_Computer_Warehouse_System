package com.junhao.hetty_computer_warehouse_system.ui.sales


import android.content.Context
import android.content.SharedPreferences
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.database.*
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_select_report.*
import kotlinx.android.synthetic.main.fragment_select_report.view.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.junhao.hetty_computer_warehouse_system.adapter.TableRowAdapter
import com.junhao.hetty_computer_warehouse_system.data.SalesOrder
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class SelectReport : Fragment() {


    private lateinit var tableRecyclerView : RecyclerView
    private var salesorderList : ArrayList<SalesOrder>? = null
    private lateinit var tableRowAdapter: TableRowAdapter
    private lateinit var salesorder : SalesOrder
    private val PERMISSION_REQUEST_CODE = 200
    val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private lateinit var eventListener: ValueEventListener
    var spinnerItemList: ArrayList<String>? = null
    var datePatternFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")

    @RequiresApi(Build.VERSION_CODES.O)
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

        if (checkPermission()) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }

        salesorderList= arrayListOf<SalesOrder>()
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

        eventListener = myRef?.child("salesOrder").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                salesorderList!!.clear()
                for (c in snapshot.children) {

                    if (c.exists()) {
                        val salesOrderItem = c.getValue(SalesOrder::class.java)
                        salesorderList?.add(salesOrderItem!!)
                    }

                }
                tableRowAdapter = TableRowAdapter(context!!, salesorderList!!)

                tableRecyclerView = view.findViewById(R.id.table_recycler_view)

                tableRecyclerView.layoutManager = LinearLayoutManager(activity)
                tableRecyclerView.adapter = tableRowAdapter

            }
        })

        view.btnSearch.setOnClickListener {

            val getSelectedItem = view.spinnerReportSalesProduct.selectedItem.toString()

            eventListener = myRef?.child("salesOrder").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    salesorderList!!.clear()
                    for (c in snapshot.children) {

                        if (c.exists()) {
                            val getProductName = c.child("salesProductName").getValue(String::class.java)
                            if(getProductName.toString() == getSelectedItem){
                                val salesOrderItem = c.getValue(SalesOrder::class.java)
                                salesorderList?.add(salesOrderItem!!)
                            }

                        }

                    }
                    tableRowAdapter = TableRowAdapter(context!!, salesorderList!!)

                    tableRecyclerView = view.findViewById(R.id.table_recycler_view)

                    tableRecyclerView.layoutManager = LinearLayoutManager(activity)
                    tableRecyclerView.adapter = tableRowAdapter

                }
            })


        }


        view.btnDownload.setOnClickListener {
            generatePDF()
        }


        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generatePDF() {

        //pdf layout
        val mFileName = "HettyWarehouseSystemReport"

        val cw = ContextWrapper(requireContext())
        val fileDirectory = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val path = File(fileDirectory, "/$mFileName.pdf")
        val outputStream = FileOutputStream(path)

        val writer = PdfWriter(path)
        val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(writer)
        val document = Document(pdfDocument)

        //image
        val draw1 = ContextCompat.getDrawable(requireContext(), R.drawable.ic_logo)
        val bitmap1 = (draw1 as BitmapDrawable).bitmap
        val stream1 = ByteArrayOutputStream()
        bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream1)
        val bitmapData1 = stream1.toByteArray()

        val imageData1 = ImageDataFactory.create(bitmapData1)
        val image1 = Image(imageData1)

        //Current Date Time
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formatted = current.format(formatter).toString()

        //pdf header
        val headerArray = floatArrayOf(140F, 140F, 140F, 140F)
        val headerTable = Table(headerArray)

        //row 1
        headerTable.addCell(Cell(3, 1).add(image1).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
        headerTable.addCell(
            Cell(1, 2).add(Paragraph("Sales Order Report").setFontSize(26F).setBold())
                .setBorder(Border.NO_BORDER)
        )
        //headerTable.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))

        //row 2
        //headerTable.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))

        //row 3
        //headerTable.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))

        //row 4
        headerTable.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
        headerTable.addCell(
            Cell().add(Paragraph("Generated on: ")).setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER)
        )
        headerTable.addCell(Cell().add(Paragraph(formatted)).setBorder(Border.NO_BORDER))

        //row 5
        headerTable.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )
        val savedStaffName = sharedPreferences.getString("getStaffName", null)
        headerTable.addCell(
            Cell().add(Paragraph("By: $savedStaffName")).setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER)
        )
        headerTable.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))

        //row 6
        headerTable.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("\n")).setBorder(Border.NO_BORDER))

        //Content Header
        val columnWidth = floatArrayOf(100f, 100f, 100f, 100f, 100f, 100f, 100f)
        val tableContent = Table(columnWidth)

        tableContent.addCell(
            Cell().add(Paragraph("Sales Order ID")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Name")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Sales Date")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Payment Type")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Product Name")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Quantity")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(Cell().add(Paragraph("Price")).setTextAlignment(TextAlignment.CENTER))

        val columnReport = floatArrayOf(100f, 100f, 100f, 100f, 100f, 100f, 100f)
        val reportItem = Table(columnReport)
        var totalPrice = 0.00
        var totalQty = 0
        var totalSales = 1


        for (i in salesorderList!!){
            tableContent.addCell(Cell().add(Paragraph(i.salesOrderID.toString())).setTextAlignment(TextAlignment.CENTER))
            tableContent.addCell(Cell().add(Paragraph(i.salesCustomerName.toString())).setTextAlignment(TextAlignment.CENTER))
            tableContent.addCell(Cell().add(Paragraph(i.salesDate.toString())).setTextAlignment(TextAlignment.CENTER))
            tableContent.addCell(Cell().add(Paragraph(i.salesPaymentType.toString())).setTextAlignment(TextAlignment.CENTER))
            tableContent.addCell(Cell().add(Paragraph(i.salesProductName.toString())).setTextAlignment(TextAlignment.CENTER))
            tableContent.addCell(Cell().add(Paragraph(i.salesQuantity.toString())).setTextAlignment(TextAlignment.CENTER))
            tableContent.addCell(Cell().add(Paragraph(i.salesPrice.toString())).setTextAlignment(TextAlignment.CENTER))
            totalSales++
            totalQty += i.salesQuantity!!.toInt()
            totalPrice += i.salesPrice!!.split(" ")[1].toDouble()
        }

        val columnWidth2 = floatArrayOf(200F, 200F, 200F, 200F)
        val footerContent = Table(columnWidth2)

        footerContent.addCell(
            Cell().add(Paragraph("Total Sales Order: $totalSales")).setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER)
        )

        footerContent.addCell(
            Cell().add(Paragraph("Total Quantity: $totalQty")).setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER)
        )
        footerContent.addCell(
            Cell().add(Paragraph("Total Price: RM $totalPrice")).setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER)
        )

        document.add(headerTable)
        document.add(Paragraph("\n"))
        document.add(tableContent)
        document.add(footerContent)
        document.add(reportItem)

        document.close()

        Toast.makeText(context, "Successfully Generated!", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()

        myRef.child("product").removeEventListener(eventListener)
        myRef.child("salesOrder").removeEventListener(eventListener)
    }

    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 = ContextCompat.checkSelfPermission(
            requireContext().applicationContext,
            WRITE_EXTERNAL_STORAGE
        )
        val permission2 = ContextCompat.checkSelfPermission(
            requireContext().applicationContext,
            READ_EXTERNAL_STORAGE
        )
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (writeStorage && readStorage) {
                    Toast.makeText(context, "Permission Granted..", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Permission Denined.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}