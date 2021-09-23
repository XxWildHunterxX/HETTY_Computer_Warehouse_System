package com.junhao.hetty_computer_warehouse_system.ui.purchase

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.google.firebase.database.FirebaseDatabase
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.Purchase
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.fragment_purchase_view_details.*
import kotlinx.android.synthetic.main.fragment_purchase_view_details.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class Fragment_purchase_view_details : Fragment() {

    private val PERMISSION_REQUEST_CODE = 200
    var purchase =
        Purchase(null, null, null, null, null, null, null, null, null, null, null, null, null, null)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_purchase_view_details, container, false)
        (activity as HomePage?)?.hideFloatingActionButton()

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedWarehouse = sharedPreferences.getString("getWarehouse", null)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Warehouse").child(savedWarehouse!!).child("Purchase")
        val purchaseID = arguments?.getString("purchaseID")
        if (checkPermission()) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }

        val udpatebtn: ImageButton = view.findViewById(R.id.btn_update)
        val receivedbtn: Button = view.findViewById(R.id.btnReceive)
        val tvpurchaseID: TextView = view.findViewById(R.id.tf_purchaseID_details)
        val tvpurProductName: TextView = view.findViewById(R.id.tf_productName_details)
        val tvpurQty: TextView = view.findViewById(R.id.tf_purchaseQuantity_details)
        val tvcostPerUnit: TextView = view.findViewById(R.id.tf_costPerUnit_details)
        val tvtotalCost: TextView = view.findViewById(R.id.tf_totalCost_details)
        val tvsupplierName: TextView = view.findViewById(R.id.tf_supplierName_details)
        val tvsupplierAddress: TextView = view.findViewById(R.id.tf_supplierAddress_details)
        val tvsupplierContact: TextView = view.findViewById(R.id.tf_supplierContact_details)
        val tvrequestDate: TextView = view.findViewById(R.id.tf_requestDate_details)
        val tvacceptDate: TextView = view.findViewById(R.id.tf_acceptDate_details)
        val tvdeliverDate: TextView = view.findViewById(R.id.tf_deliverDate_details)
        val tvreceivedDate: TextView = view.findViewById(R.id.tf_receivedDate_details)
        val tvstatus: TextView = view.findViewById(R.id.tf_status_details)

        myRef.child(purchaseID!!).get().addOnSuccessListener {
            if (it.exists()) {
                purchase = Purchase(
                    it.child("purchaseID").value.toString(),
                    it.child("purProductName").value.toString(),
                    it.child("purQty").value.toString(),
                    it.child("costPerUnit").value.toString(),
                    it.child("totalCost").value.toString(),
                    it.child("supplierName").value.toString(),
                    it.child("supplierAddress").value.toString(),
                    it.child("supplierContact").value.toString(),
                    it.child("requestDate").value.toString(),
                    it.child("acceptDate").value.toString(),
                    it.child("rejectDate").value.toString(),
                    it.child("deliverDate").value.toString(),
                    it.child("receivedDate").value.toString(),
                    it.child("status").value.toString(),
                )
                tvpurchaseID.text = it.child("purchaseID").value.toString()
                tvpurProductName.text = it.child("purProductName").value.toString()
                tvpurQty.text = it.child("purQty").value.toString()
                tvcostPerUnit.text = it.child("costPerUnit").value.toString()
                tvtotalCost.text = it.child("totalCost").value.toString()
                tvsupplierName.text = it.child("supplierName").value.toString()
                tvsupplierAddress.text = it.child("supplierAddress").value.toString()
                tvsupplierContact.text = it.child("supplierContact").value.toString()
                tvrequestDate.text = it.child("requestDate").value.toString()
                tvstatus.text = it.child("status").value.toString()

                if (it.child("status").value.toString() == "Rejected") {
                    tvreceivedDate.isVisible = false
                    tv_receivedDate.isVisible = false
                    tvdeliverDate.isVisible = false
                    tv_deliverDate.isVisible = false
                    tv_acceptDate.text = getString(R.string.request_date)
                    tvacceptDate.text = it.child("rejectDate").value.toString()
                } else if (it.child("status").value.toString() == "Accepted") {
                    tvreceivedDate.isVisible = false
                    tv_receivedDate.isVisible = false
                    tvdeliverDate.isVisible = false
                    tv_deliverDate.isVisible = false
                    tvacceptDate.text = it.child("acceptDate").value.toString()
                } else if (it.child("status").value.toString() == "Delivering") {
                    tvreceivedDate.isVisible = false
                    tv_receivedDate.isVisible = false
                    receivedbtn.isVisible = true
                    tvacceptDate.text = it.child("acceptDate").value.toString()
                    tvdeliverDate.text = it.child("deliverDate").value.toString()
                } else if (it.child("status").value.toString() == "Received") {
                    tvacceptDate.text = it.child("acceptDate").value.toString()
                    tvdeliverDate.text = it.child("deliverDate").value.toString()
                    tvreceivedDate.text = it.child("receivedDate").value.toString()
                }
                if (it.child("status").value.toString() == "Pending") {
                    tvreceivedDate.isVisible = false
                    tv_receivedDate.isVisible = false
                    tvdeliverDate.isVisible = false
                    tv_deliverDate.isVisible = false
                    tvacceptDate.isVisible = false
                    tv_acceptDate.isVisible = false
                    udpatebtn.isVisible = true
                }
            } else {
                Toast.makeText(activity, "Record Not Founded", Toast.LENGTH_LONG).show()
            }
        }

        view.btn_update.setOnClickListener {
            val bundle = bundleOf(
                Pair("purchaseID", purchaseID)
            )

            Navigation.findNavController(view!!).navigate(
                R.id.nav_purchase_update,
                bundle
            )

        }

        view.btn_delete.setOnClickListener {
            AlertDialog.Builder(requireContext()).also {
                it.setTitle(getString(R.string.delete_confirmation))
                it.setPositiveButton(getString(R.string.yes),
                    DialogInterface.OnClickListener { dialog, id ->
                        myRef.child(purchaseID!!).setValue(null).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    activity,
                                    "Purchase $purchaseID deleted successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        Navigation.findNavController(view!!).navigate(
                            R.id.nav_purchaseOrders
                        )
                    })
                it.setNegativeButton(
                    getString(R.string.no),
                    DialogInterface.OnClickListener { dialog, id ->
                        Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                    })
            }.create().show()
        }

        view.btnReceive.setOnClickListener {
            AlertDialog.Builder(requireContext()).also {
                it.setTitle(getString(R.string.received_confirmation))
                it.setPositiveButton(getString(R.string.yes),
                    DialogInterface.OnClickListener { dialog, id ->

                        purchase.status = "Received"
                        purchase.receivedDate = DateFormat.getDateTimeInstance().format(Date())
                        myRef.child(purchaseID!!).setValue(purchase)
                        receivedPurchase()
                        Navigation.findNavController(view!!).navigate(
                            R.id.nav_purchaseOrders
                        )
                    })
                it.setNegativeButton(
                    getString(R.string.no),
                    DialogInterface.OnClickListener { dialog, id ->
                        Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                    })
            }.create().show()

        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun receivedPurchase() {
        //pdf layout
        val mFileName = "Invoice${purchase.purchaseID}"

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


        //generate invoice number
        val invoiceNum = ((10000..90000).random()).toString()
        //row 1
        headerTable.addCell(Cell(3, 1).add(image1).setBorder(Border.NO_BORDER))
        headerTable.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
        headerTable.addCell(
            Cell(1, 2).add(Paragraph("Invoice : $invoiceNum").setFontSize(26F).setBold())
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
        val columnWidth = floatArrayOf(100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f)
        val tableContent = Table(columnWidth)

        tableContent.addCell(
            Cell().add(Paragraph("Purchase ID")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Product Name")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Quantity")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Cost Per Unit(RM)")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Total Cost(RM)")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Requested Date")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Accepted Date")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Delivered Date")).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph("Received Date")).setTextAlignment(TextAlignment.CENTER)
        )

        val columnReport = floatArrayOf(100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f)
        val reportItem = Table(columnReport)

        tableContent.addCell(
            Cell().add(Paragraph(purchase.purchaseID.toString()))
                .setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph(purchase.purProductName.toString()))
                .setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph(purchase.purQty.toString())).setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph(purchase.costPerUnit.toString()))
                .setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph(purchase.totalCost.toString()))
                .setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph(purchase.requestDate.toString()))
                .setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph(purchase.acceptDate.toString()))
                .setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph(purchase.deliverDate.toString()))
                .setTextAlignment(TextAlignment.CENTER)
        )
        tableContent.addCell(
            Cell().add(Paragraph(purchase.receivedDate.toString()))
                .setTextAlignment(TextAlignment.CENTER)
        )

        val columnWidth2 = floatArrayOf(400F, 400F, 400F)
        val footerContent = Table(columnWidth2)

        footerContent.addCell(
            Cell().add(Paragraph("Supplier Name : ${purchase.supplierName}        Supplier Contact: ${purchase.supplierContact}"))
                .setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
        )

        document.add(headerTable)
        document.add(Paragraph("\n"))
        document.add(tableContent)
        document.add(footerContent)
        document.add(reportItem)

        document.close()

        Toast.makeText(context, "Successfully Generated!", Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 = ContextCompat.checkSelfPermission(
            requireContext().applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permission2 = ContextCompat.checkSelfPermission(
            requireContext().applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
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