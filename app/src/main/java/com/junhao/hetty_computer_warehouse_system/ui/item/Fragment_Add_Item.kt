package com.junhao.hetty_computer_warehouse_system.ui.item

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import java.text.SimpleDateFormat;
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.Product
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import java.util.*
import android.view.MotionEvent

import android.view.View.OnTouchListener
import com.google.zxing.integration.android.IntentIntegrator
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import kotlinx.android.synthetic.main.app_bar_home_page2.view.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask

import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class Fragment_Add_Item : Fragment() {

    private var imageURI: Uri = Uri.EMPTY
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Warehouse").child("warehouse1").child("product")
    private var found: Boolean = false
    private var getImgValue: String = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val patternBarcode = Regex("^123456\\d{4}\$")
        val patternRack = Regex("^[ABC]-\\d{2}\$")
        val view = inflater.inflate(R.layout.fragment_add_item, container, false)


        view.btnImg.setOnClickListener {
            selectImage()
        }

        view.btnAddItem.setOnClickListener() {

            val prodName = view.tfProductName.text.toString()
            val prodBarCode = view.tfProductBarcode.text.toString()
            val prodRack = view.tfProductRack.text.toString()
            val prodType = view.tfProductType.text.toString()
            val prodPrice = view.tfProductPrice.text.toString()
            val prodQTY = view.tfProductQuantity.text.toString()
            val prodMinQty = view.tfMinimumQTY.text.toString()

            if (prodName.isEmpty()) {
                view.tfProductName.error = "Product Name Required"
                return@setOnClickListener
            } else if (prodBarCode.isEmpty()) {
                view.tfProductBarcode.error = "Product Barcode Required!"
                return@setOnClickListener
            } else if (prodRack.isEmpty()) {
                view.tfProductRack.error = "Product Rack Required!"
                return@setOnClickListener
            } else if (prodType.isEmpty()) {
                view.tfProductType.error = "Product Type Required!"
                return@setOnClickListener
            } else if (prodPrice.isEmpty() || prodPrice == "0" || prodPrice == "0.0") {
                view.tfProductPrice.error = "Product Price Required!"
                return@setOnClickListener
            } else if (prodQTY.isEmpty() || prodQTY == "0") {
                view.tfProductQuantity.error = "Product Quantity Required!"
                return@setOnClickListener
            } else if (prodMinQty.isEmpty()) {
                view.tfMinimumQTY.error = "Product Minimum Quantity Required!"
                return@setOnClickListener
            } else if (imageURI == Uri.EMPTY) {
                Toast.makeText(activity, "Please Select One Image", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else if (!patternBarcode.containsMatchIn(prodBarCode)) {
                view.tfProductBarcode.error = "Format Wrong! Example: 123456XXXX"
                return@setOnClickListener
            } else if (!patternRack.containsMatchIn(prodRack)) {
                view.tfProductRack.error = "Format Wrong! Example: A-01"
                return@setOnClickListener
            } else {

                myRef.child(prodName).get().addOnSuccessListener {
                    if (it.exists()) {
                        view.tfProductName.error = "Product Name Existed!"
                        return@addOnSuccessListener
                    } else {
                        val eventListener: ValueEventListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (ds in dataSnapshot.children) {
                                    val barCode =
                                        ds.child("productBarcode").getValue(String::class.java)
                                    Log.d("TAG", barCode!!)
                                    if (barCode == prodBarCode) {
                                        found = true
                                        view.tfProductBarcode.error = "Product Barcode Existed!"
                                        break
                                    } else {
                                        found = false
                                    }
                                }
                                if (!found) {

                                    //upload image
                                    uploadImage()

                                    val product = Product(
                                        prodName,
                                        prodBarCode,
                                        prodRack,
                                        prodType,
                                        prodPrice,
                                        prodQTY,
                                        prodMinQty,
                                        view.chkbox_lowstock_addedit.isChecked.toString(),
                                        getImgValue
                                        )

                                    myRef.child(product.productName!!).setValue(product)

                                    view.tfProductName.text.clear()
                                    view.tfProductBarcode.text.clear()
                                    view.tfProductRack.text.clear()
                                    view.tfProductType.text.clear()
                                    view.tfProductPrice.setText("0")
                                    view.tfProductQuantity.setText("0")
                                    view.tfMinimumQTY.setText("0")

                                }

                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        }
                        myRef.addListenerForSingleValueEvent(eventListener)


                    }

                }.addOnFailureListener {

                    Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show()
                }
            }
        }

        view.tfProductBarcode.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= view.tfProductBarcode.right - view.tfProductBarcode.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    val scanner = IntentIntegrator.forSupportFragment(this)
                    scanner.setBeepEnabled(false)
                    scanner.initiateScan()

                    return@OnTouchListener true
                }
            }
            false
        })

        view.chkbox_lowstock_addedit.setOnClickListener() {
            if (view.chkbox_lowstock_addedit.isChecked) {
                view.tv_minimumquanalert_addedit.visibility = View.VISIBLE
                view.tfMinimumQTY.visibility = View.VISIBLE
            } else {
                view.tv_minimumquanalert_addedit.visibility = View.INVISIBLE
                view.tfMinimumQTY.visibility = View.INVISIBLE
            }

        }


        // Inflate the layout for this fragment

        (activity as HomePage?)?.hideFloatingActionButton()


        return view
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Uploading File ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy-MM-dd_hh_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)

        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
        CoroutineScope(Dispatchers.IO).launch {
            storageReference.putFile(imageURI)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->

                    btnImg.setImageURI(null)
                    Toast.makeText(activity, "Successfully Added", Toast.LENGTH_LONG).show()
                    imageURI = Uri.EMPTY
                    btnImg.setImageResource(R.drawable.ic_default_product_select_img)
                    if (progressDialog.isShowing) progressDialog.dismiss()

                    val downloadUrl =
                        taskSnapshot.storage.downloadUrl.addOnCompleteListener { task ->
                            Log.d("TAG", task.result.toString())
                            getImgValue = task.result.toString()
                        }


                }).addOnFailureListener {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageURI = data?.data!!
            btnImg.setImageURI(imageURI)

        }
        if (resultCode == RESULT_OK) {

            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    tfProductBarcode.setText(result.contents)
                    Toast.makeText(activity, "Scanned: " + result.contents, Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }

        }

    }


}