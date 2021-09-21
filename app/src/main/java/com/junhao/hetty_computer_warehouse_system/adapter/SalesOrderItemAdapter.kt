package com.junhao.hetty_computer_warehouse_system.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.SalesOrder

class SalesOrderItemAdapter(
    val context: Context,
    private val SalesOrderItemList: List<SalesOrder>
) :
    RecyclerView.Adapter<SalesOrderItemAdapter.myViewHolder>() {

    private lateinit var mListener: SalesOrderItemAdapter.onItemClickListener

    interface onItemClickListener {

        fun onItemClick(
            salesOrderID: String,
            salesOrderBarCode: String,
            salesOrderDate: String,
            salesOrderPrice: String,
            salesOrderQty: String,
            salesOrderPaymentType: String
        )

        fun onDeleteClick(salesOrderID: String)

        fun onEditClick(
            salesOrderID: String,
            salesOrderBarCode: String,
            salesOrderDate: String,
            salesOrderPrice: String,
            salesOrderQty: String,
            salesOrderPaymentType: String
        )
    }

    fun setOnItemClickListener(listener: SalesOrderItemAdapter.onItemClickListener) {

        mListener = listener

    }

    class myViewHolder(itemView: View, listener: SalesOrderItemAdapter.onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        private val so_salesOrderID: TextView = itemView.findViewById(R.id.so_salesOrderID)
        private val so_barcode: TextView = itemView.findViewById(R.id.so_barcode)
        private val so_date: TextView = itemView.findViewById(R.id.so_date)
        private val so_price: TextView = itemView.findViewById(R.id.so_price)
        private val so_quantity: TextView = itemView.findViewById(R.id.so_quantity)
        private val so_type: TextView = itemView.findViewById(R.id.so_type)

        private val mDeleteImg: ImageView = itemView.findViewById(R.id.so_imgDeleteBtn)

        private val mEditImg: ImageView = itemView.findViewById(R.id.btnEditSO)

        fun bind(salesOrder: SalesOrder, context: Context) {

            so_salesOrderID.text = salesOrder.salesOrderID
            so_barcode.text = salesOrder.salesProductBarcode
            so_date.text = salesOrder.salesDate
            so_price.text = salesOrder.salesPrice
            so_quantity.text = salesOrder.salesQuantity
            so_type.text = salesOrder.salesPaymentType
        }

        init {

            itemView.setOnClickListener {

                listener.onItemClick(
                    so_salesOrderID.text.toString(),
                    so_barcode.text.toString(),
                    so_date.text.toString(),
                    so_price.text.toString(),
                    so_quantity.text.toString(),
                    so_type.text.toString()
                )


            }

            mDeleteImg.setOnClickListener {
                if (listener != null) {
                    val position: Int = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(so_salesOrderID.text.toString())
                    }

                }
            }

            mEditImg.setOnClickListener {
                if (listener != null) {
                    val position: Int = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(
                            so_salesOrderID.text.toString(),
                            so_barcode.text.toString(),
                            so_date.text.toString(),
                            so_price.text.toString(),
                            so_quantity.text.toString(),
                            so_type.text.toString()
                        )
                    }

                }
            }


        }


    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SalesOrderItemAdapter.myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.sales_order_item, parent, false
        )

        return SalesOrderItemAdapter.myViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: SalesOrderItemAdapter.myViewHolder, position: Int) {
        holder?.bind(SalesOrderItemList[position], context)
    }

    override fun getItemCount(): Int {
        return SalesOrderItemList.size
    }


}