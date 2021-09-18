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
import com.squareup.picasso.Picasso

class WarehouseAdapter (val context: Context, private val ProductItemList: List<Product>) :
    RecyclerView.Adapter<WarehouseAdapter.WarehouseViewHolder>() {

    private lateinit var mListener: WarehouseAdapter.onItemClickListener

    interface onItemClickListener {

        fun onItemClick(
            productName: String,
            productType: String,
            productBarcode: String,
            productQty: String
        )


    }
    fun setOnItemClickListener(listener: WarehouseAdapter.onItemClickListener) {

        mListener = listener

    }


    class WarehouseViewHolder(itemView: View, listener: WarehouseAdapter.onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        private val productName: TextView = itemView.findViewById(R.id.tvWarehouseProductName)
        private val productType: TextView = itemView.findViewById(R.id.tvWarehouseProductType)
        private val productBarcode: TextView = itemView.findViewById(R.id.tvWarehouseProductBarcode)
        private val productQty: TextView = itemView.findViewById(R.id.tvWarehouseProductQty)
        private val productImg: ImageView = itemView.findViewById(R.id.imgViewWarehouseProduct)

        fun bind(prod: Product, context: Context) {

            var imageUri: String? = null
            imageUri = prod.productImg
            Picasso.get().load(imageUri).into(productImg);
            productName.text = prod.productName
            productType.text = prod.productType
            productBarcode.text = prod.productBarcode
            productQty.text = prod.productQuantity

        }
        init {

            itemView.setOnClickListener {
                var imageUri: String? = null
                imageUri = productImg.toString()
                listener.onItemClick(
                    productName.text.toString(),
                    productType.text.toString(),
                    productBarcode.text.toString(),
                    productQty.text.toString()
                )


            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WarehouseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.search_product_item, parent, false
        )


        return WarehouseViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: WarehouseViewHolder, position: Int) {
        holder?.bind(ProductItemList[position], context)
    }

    override fun getItemCount(): Int {
        return ProductItemList.size
    }
}