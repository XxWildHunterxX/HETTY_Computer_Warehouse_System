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

class ProductItemAdapter(val context: Context, private val ProductItemList: List<Product>) :
    RecyclerView.Adapter<ProductItemAdapter.myViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {

        fun onItemClick(
            productName: String,
            productPrice: String,
            productType: String,
            productRack: String,
            productQty: String
        )

        fun onDeleteClick(productName: String)

        fun onLocationClick(productName: String, productRack: String)

    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    class myViewHolder(itemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        private val productName: TextView = itemView.findViewById(R.id.tvProductName)
        private val productPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val productType: TextView = itemView.findViewById(R.id.tvProductType)
        private val productRack: TextView = itemView.findViewById(R.id.tvProductRack)
        private val productQty: TextView = itemView.findViewById(R.id.tvProductQty)
        private val productImg: ImageView = itemView.findViewById(R.id.imgViewProduct)

        //delete product
        private val mDeleteImg: ImageView = itemView.findViewById(R.id.imgDeleteBtn)

        //location rack
        private val mLocationImg: ImageView = itemView.findViewById(R.id.btnLocationRack)

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
                    productType.text.toString(),
                    productRack.text.toString(),
                    productQty.text.toString()
                )


            }

            mDeleteImg.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    if (listener != null) {
                        val position: Int = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(productName.text.toString())
                        }

                    }


                }

            })

            mLocationImg.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    if (listener != null) {
                        val position: Int = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onLocationClick(productName.text.toString(),productRack.text.toString())
                        }
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.product_item, parent, false
        )


        return myViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder?.bind(ProductItemList[position], context)
    }

    override fun getItemCount(): Int {
        return ProductItemList.size
    }


}