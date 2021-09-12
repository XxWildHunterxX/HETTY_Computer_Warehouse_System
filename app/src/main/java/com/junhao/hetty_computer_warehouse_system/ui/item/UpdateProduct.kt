package com.junhao.hetty_computer_warehouse_system.ui.item

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.zxing.integration.android.IntentIntegrator
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_home_page2.*
import kotlinx.android.synthetic.main.fragment_update_product.*
import kotlinx.android.synthetic.main.fragment_update_product.view.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.Navigation.findNavController

class UpdateProduct : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Warehouse").child("warehouse1").child("product")
    private var imageURI: Uri = Uri.EMPTY

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val patternBarcode = Regex("^123456\\d{4}\$")
        val patternRack = Regex("^[ABC]-\\d{2}\$")

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update_product, container, false)
        (activity as HomePage?)?.hideFloatingActionButton()
        val productName = arguments?.getString("productName")
        val productPrice = arguments?.getString("productPrice")
        val productType = arguments?.getString("productType")
        val productRack = arguments?.getString("productRack")
        val productQty = arguments?.getString("productQty")

        val productImg: ImageView = view.findViewById(R.id.updateImgProduct)

        view.etProductName.setText(productName)
        view.etProductPrice.setText(productPrice)
        view.etProductType.setText(productType)
        view.etProductRack.setText(productRack)
        view.etProductQuantity.setText(productQty)

        myRef.child(productName.toString()).get().addOnSuccessListener {
            if (it.exists()) {
                var imageUri: String? = null
                imageUri = it.child("productImg").value.toString()
                Picasso.get().load(imageUri).into(productImg)


                view.etProductBarcode.setText(it.child("productBarcode").value.toString())
                view.etMinQTY.setText(it.child("minimumAlertQty").value.toString())
                view.chkbox_lowstock_edit.isChecked =
                    toBoolean(it.child("alertChk").value.toString())

                if (view.chkbox_lowstock_edit.isChecked) {
                    view.et_minquanalert_edit.visibility = View.VISIBLE
                    view.etMinQTY.visibility = View.VISIBLE
                } else {
                    view.et_minquanalert_edit.visibility = View.GONE
                    view.etMinQTY.visibility = View.GONE
                }


            }

        }.addOnFailureListener {

            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show()
        }


        view.btnUpdateItem.setOnClickListener() {

            if (view.btnUpdateItem.text == "Edit") {

                view.etProductPrice.isEnabled = true
                view.etProductType.isEnabled = true
                view.etProductRack.isEnabled = true
                view.etProductQuantity.isEnabled = true
                view.etMinQTY.isEnabled = true
                view.chkbox_lowstock_edit.isEnabled = true
                productImg.setOnClickListener {
                    selectImage()
                }
                view.btnUpdateItem.text = "Update"
            } else {

                val prodName = view.etProductName.text.toString()
                val prodBarCode = view.etProductBarcode.text.toString()
                val prodRack = view.etProductRack.text.toString()
                val prodType = view.etProductType.text.toString()
                val prodPrice = view.etProductPrice.text.toString()
                val prodQTY = view.etProductQuantity.text.toString()
                val prodMinQty = view.etMinQTY.text.toString()

                if (prodRack.isEmpty()) {
                    view.etProductRack.error = "Product Rack Required!"
                    return@setOnClickListener
                } else if (prodType.isEmpty()) {
                    view.etProductType.error = "Product Type Required!"
                    return@setOnClickListener
                } else if (prodPrice.isEmpty() || prodPrice == "0" || prodPrice == "0.0") {
                    view.etProductPrice.error = "Product Price Required!"
                    return@setOnClickListener
                } else if (prodQTY.isEmpty() || prodQTY == "0") {
                    view.etProductQuantity.error = "Product Quantity Required!"
                    return@setOnClickListener
                } else if (prodMinQty.isEmpty()) {
                    view.etMinQTY.error = "Product Minimum Quantity Required!"
                    return@setOnClickListener
                } else if (!patternBarcode.containsMatchIn(prodBarCode)) {
                    view.etProductBarcode.error = "Format Wrong! Example: 123456XXXX"
                    return@setOnClickListener
                } else if (!patternRack.containsMatchIn(prodRack)) {
                    view.etProductRack.error = "Format Wrong! Example: A-01"
                    return@setOnClickListener
                } else {
                    //do Update here
                    val progressDialog = ProgressDialog(activity)
                    progressDialog.setMessage("Uploading File ...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()

                    val formatter =
                        SimpleDateFormat("yyyy-MM-dd_hh_mm_ss", Locale.getDefault())
                    val now = Date()
                    val fileName = formatter.format(now)

                    val storageReference = FirebaseStorage.getInstance()
                        .getReference("images/$fileName")

                    storageReference.putFile(imageURI)
                        .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->

                            updateImgProduct.setImageURI(null)
                            Toast.makeText(
                                activity,
                                "Successfully Updated",
                                Toast.LENGTH_LONG
                            ).show()
                            imageURI = Uri.EMPTY
                            if (progressDialog.isShowing) progressDialog.dismiss()

                            taskSnapshot.storage.downloadUrl.addOnCompleteListener { task ->
                                val getImgValue = task.result.toString()
                                Log.d("TAG", task.result.toString())

                                val product = mapOf<String, String>(
                                    "productName" to prodName,
                                    "productBarcode" to prodBarCode,
                                    "productRack" to prodRack,
                                    "productType" to prodType,
                                    "productPrice" to prodPrice,
                                    "productQuantity" to prodQTY,
                                    "minimumAlertQty" to prodMinQty,
                                    "alertChk" to view.chkbox_lowstock_edit.isChecked.toString(),
                                    "productImg" to getImgValue
                                )
                                myRef.child(prodName).updateChildren(product).addOnSuccessListener {

                                    if (findNavController(view).currentDestination?.id == R.id.updateProduct) {
                                        findNavController(view).navigate(R.id.action_updateProduct_to_nav_items)
                                    }

                                }

                            }

                        }).addOnFailureListener {

                            //update without select photo
                            if (progressDialog.isShowing) progressDialog.dismiss()

                            val product = mapOf<String, String>(
                                "productName" to prodName,
                                "productBarcode" to prodBarCode,
                                "productRack" to prodRack,
                                "productType" to prodType,
                                "productPrice" to prodPrice,
                                "productQuantity" to prodQTY,
                                "minimumAlertQty" to prodMinQty,
                                "alertChk" to view.chkbox_lowstock_edit.isChecked.toString()
                            )
                            myRef.child(prodName).updateChildren(product).addOnSuccessListener {

                                if (findNavController(view).currentDestination?.id == R.id.updateProduct) {
                                    findNavController(view).navigate(R.id.action_updateProduct_to_nav_items)
                                }

                            }
                            Toast.makeText(activity, "Updated Without Select Photo", Toast.LENGTH_LONG).show()

                        }

                }

            }
        }


        view.etProductBarcode.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= view.etProductBarcode.right - view.etProductBarcode.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    val scanner = IntentIntegrator.forSupportFragment(this)
                    scanner.setBeepEnabled(false)
                    scanner.initiateScan()

                    return@OnTouchListener true
                }
            }
            false
        })

        view.chkbox_lowstock_edit.setOnClickListener() {
            if (view.chkbox_lowstock_edit.isChecked) {
                view.et_minquanalert_edit.visibility = View.VISIBLE
                view.etMinQTY.visibility = View.VISIBLE
            } else {
                view.et_minquanalert_edit.visibility = View.GONE
                view.etMinQTY.visibility = View.GONE
            }

        }

        return view
    }

    fun toBoolean(s: String): Boolean {
        return s.toBoolean()
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageURI = data?.data!!
            updateImgProduct.setImageURI(imageURI)

        }
        if (resultCode == Activity.RESULT_OK) {

            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    etProductBarcode.setText(result.contents)
                    Toast.makeText(activity, "Scanned: " + result.contents, Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }

        }
    }
}