package com.junhao.hetty_computer_warehouse_system.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.Product
import com.junhao.hetty_computer_warehouse_system.data.TrackingItem
import com.squareup.picasso.Picasso

class ProductItemAdapter(val context: Context, val ProductItemList: List<Product>) :
    RecyclerView.Adapter<ProductItemAdapter.myViewHolder>() {


    class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val productName: TextView = itemView.findViewById(R.id.tvProductName)
        private val productPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val productType: TextView = itemView.findViewById(R.id.tvProductType)
        private val productRack: TextView = itemView.findViewById(R.id.tvProductRack)
        private val productQty: TextView = itemView.findViewById(R.id.tvProductQty)
        private val productImg: ImageView = itemView.findViewById(R.id.imgViewProduct)

        fun bind(prod: Product, context: Context) {

            var imageUri : String? = null
            imageUri = prod.productImg
            Picasso.get().load(imageUri).into(productImg);
            productName.text = "Name : " + prod.productName
            productPrice.text = "Price : RM" + prod.productPrice
            productType.text = "Type : " + prod.productType
            productRack.text = "Rack : " + prod.productRack
            productQty.text = "Qty : " + prod.productQuantity

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.product_item, parent, false
        )

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder?.bind(ProductItemList[position], context)
    }

    override fun getItemCount(): Int {
        return ProductItemList.size
    }


}