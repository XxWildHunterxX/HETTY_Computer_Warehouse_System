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

class SalesOrderProductAdapter (val context: Context, private val SalesProductItemList: List<Product>) :
RecyclerView.Adapter<SalesOrderProductAdapter.myViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {

        fun onItemClick(
            productName: String,
            productPrice: String,
            productQty: String
        )

    }

    fun setOnItemClickListener(listener: onItemClickListener) {

        mListener = listener

    }

    class myViewHolder(itemView: View, listener: SalesOrderProductAdapter.onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        private val productName: TextView = itemView.findViewById(R.id.tvSalesProductName)
        private val productPrice: TextView = itemView.findViewById(R.id.tvSalesProductPrice)
        private val productType: TextView = itemView.findViewById(R.id.tvSalesProductType)
        private val productRack: TextView = itemView.findViewById(R.id.tvSalesProductRack)
        private val productQty: TextView = itemView.findViewById(R.id.tvSalesProductQty)
        private val productImg: ImageView = itemView.findViewById(R.id.imgViewSalesProduct)

        fun bind(prod: Product, context: Context) {

            var imageUri: String? = null
            imageUri = prod.productImg
            Picasso.get().load(imageUri).into(productImg);
            productName.text = prod.productName
            productPrice.text = prod.productPrice
            productType.text = prod.productType
            productRack.text = prod.productRack
            productQty.text = prod.productQuantity

        }

        init {

            itemView.setOnClickListener {
                var imageUri: String? = null
                imageUri = productImg.toString()
                listener.onItemClick(
                    productName.text.toString(),
                    productPrice.text.toString(),
                    productQty.text.toString()
                )


            }

        }

    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.sales_order_product_item, parent, false
        )


        return myViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder?.bind(SalesProductItemList[position], context)
    }

    override fun getItemCount(): Int {
        return SalesProductItemList.size
    }


}