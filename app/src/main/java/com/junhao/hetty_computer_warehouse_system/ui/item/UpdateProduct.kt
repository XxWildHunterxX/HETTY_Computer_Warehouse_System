package com.junhao.hetty_computer_warehouse_system.ui.item

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.junhao.hetty_computer_warehouse_system.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import kotlinx.android.synthetic.main.fragment_update_product.view.*
import kotlinx.android.synthetic.main.fragment_update_product.view.chkbox_lowstock_addedit
import kotlinx.android.synthetic.main.fragment_update_product.view.tfProductBarcode
import kotlinx.android.synthetic.main.fragment_update_product.view.tfProductName
import kotlinx.android.synthetic.main.fragment_update_product.view.tfProductPrice
import kotlinx.android.synthetic.main.fragment_update_product.view.tfProductQuantity
import kotlinx.android.synthetic.main.fragment_update_product.view.tfProductRack
import kotlinx.android.synthetic.main.fragment_update_product.view.tfProductType


class UpdateProduct : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Warehouse").child("warehouse1").child("product")
    private var imageURI: Uri = Uri.EMPTY

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update_product, container, false)

        val productName = arguments?.getString("productName")
        val productPrice = arguments?.getString("productPrice")
        val productType = arguments?.getString("productType")
        val productRack = arguments?.getString("productRack")
        val productQty = arguments?.getString("productQty")

        val productImg : ImageView = view.findViewById(R.id.updateImgProduct)

        view.tfProductName.setText(productName)
        view.tfProductPrice.setText(productPrice)
        view.tfProductType.setText(productType)
        view.tfProductRack.setText(productRack)
        view.tfProductQuantity.setText(productQty)

        myRef.child(productName.toString()).get().addOnSuccessListener {
            if (it.exists()) {
                var imageUri : String? = null
                imageUri = it.child("productImg").value.toString()
                Picasso.get().load(imageUri).into(productImg);

                view.tfProductBarcode.setText(it.child("productBarcode").value.toString())
                view.tfMinQTY.setText(it.child("minimumAlertQty").value.toString())
                view.chkbox_lowstock_addedit.isChecked = toBoolean(it.child("alertChk").value.toString())

                if (view.chkbox_lowstock_addedit.isChecked) {
                    view.tv_minquanalert_edit.visibility = View.VISIBLE
                    view.tfMinQTY.visibility = View.VISIBLE
                } else {
                    view.tv_minquanalert_edit.visibility = View.INVISIBLE
                    view.tfMinQTY.visibility = View.INVISIBLE
                }


            }

        }.addOnFailureListener {

            Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show()
        }


        view.btnUpdateItem.setOnClickListener(){

            if(view.btnUpdateItem.text == "Edit"){

                view.tfProductName.isEnabled = true
                view.tfProductPrice.isEnabled = true
                view.tfProductType.isEnabled = true
                view.tfProductRack.isEnabled = true
                view.tfProductQuantity.isEnabled = true
                view.tfProductBarcode.isEnabled = true
                view.tfMinQTY.isEnabled = true
                view.chkbox_lowstock_addedit.isEnabled = true
                productImg.setOnClickListener {
                    selectImage()
                }

            }else{


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
    }
}