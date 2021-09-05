package com.junhao.hetty_computer_warehouse_system.ui.item

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import java.text.SimpleDateFormat;
import android.net.Uri
import android.os.Bundle
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


class Fragment_Add_Item : Fragment() {

    var imageURI: Uri = Uri.EMPTY

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val patternString = Regex("[a-zA-Z]")
        val patternBarcode = Regex("^123456\\d{8}\$")
        val view = inflater.inflate(R.layout.fragment_add_item, container, false)


        view.btnImg.setOnClickListener {
            selectImage()

        }

        view.btnAddItem.setOnClickListener() {

            val prodName = view.tfProductName.text.toString()
            val prodBarCode = view.tfProductBarcode.text.toString()
            val prodRack = view.tfProductRack.text.toString()
            val prodType = view.tfProductType.text.toString()
            val prodDesc = view.tfProductDesc.text.toString()
            val prodMinQty = view.tfMinimumQTY.text.toString()
            val prodImg = null

            if (prodName.isEmpty()) {
                view.tfProductName.error = "Product Name Required"
                return@setOnClickListener
            } else if (prodBarCode.isEmpty()) {
                view.tfProductBarcode.error = "Product Barcode Required!"
                return@setOnClickListener
            }else if (prodRack.isEmpty()) {
                view.tfProductRack.error = "Product Rack Required!"
                return@setOnClickListener
            } else if (prodType.isEmpty()) {
                view.tfProductType.error = "Product Type Required!"
                return@setOnClickListener
            }else if (prodDesc.isEmpty()) {
                view.tfProductDesc.error = "Product Description Required!"
                return@setOnClickListener
            }else if (prodMinQty.isEmpty()) {
                view.tfMinimumQTY.error = "Product Minimum Quantity Required!"
                return@setOnClickListener
            }else if (imageURI == Uri.EMPTY) {
                Toast.makeText(activity, "Please Select One Image", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }else {

                //upload image
                uploadImage()

                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("Product")

                val product = Product(
                    prodName, "ProductName", "prodBarcode",
                    "A-01", "Keyboard", "Bad", "50")

                myRef.child(product.id).setValue(product)

                Toast.makeText(activity, "Validation Completed", Toast.LENGTH_LONG).show()


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




        // Inflate the layout for this fragment
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

        storageReference.putFile(imageURI).addOnSuccessListener {

            btnImg.setImageURI(null)
            btnImg.setImageResource(R.drawable.ic_default_product_select_img)
            Toast.makeText(activity, "Successfully uploaded", Toast.LENGTH_LONG).show()
            if (progressDialog.isShowing) progressDialog.dismiss()

        }.addOnFailureListener {
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show()
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
        if(resultCode == RESULT_OK){

            val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)

            if(result !=null){
                if(result.contents ==null){
                    Toast.makeText(activity,"Cancelled",Toast.LENGTH_LONG).show()
                }else{
                    tfProductBarcode.setText(result.contents)
                    Toast.makeText(activity,"Scanned: "+result.contents,Toast.LENGTH_LONG).show()
                }
            }else{
                super.onActivityResult(requestCode,resultCode,data)
            }

        }

    }

}