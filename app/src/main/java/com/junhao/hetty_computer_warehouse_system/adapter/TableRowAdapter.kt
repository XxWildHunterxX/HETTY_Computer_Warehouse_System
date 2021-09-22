package com.junhao.hetty_computer_warehouse_system.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.SalesOrder

class TableRowAdapter(val context: Context, private var userArrayList: List<SalesOrder>) :
    RecyclerView.Adapter<TableRowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.report_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.tvSalesOrderID.text = userArrayList[i].salesOrderID
        viewHolder.tvSalesCustomerName.text = userArrayList[i].salesCustomerName
        viewHolder.tvSalesDate.text = userArrayList[i].salesDate
        viewHolder.tvSalesPayment.text = userArrayList[i].salesPaymentType
        viewHolder.tvSalesName.text = userArrayList[i].salesProductName
        viewHolder.tvSalesQty.text = userArrayList[i].salesQuantity
        viewHolder.tvSalesPrice.text = userArrayList[i].salesPrice
    }

    override fun getItemCount(): Int {
        return userArrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSalesOrderID: TextView = itemView.findViewById(R.id.tvSalesOrderID)
        val tvSalesCustomerName: TextView = itemView.findViewById(R.id.tvSalesCustomerName)
        val tvSalesDate: TextView = itemView.findViewById(R.id.tvSalesDate)
        val tvSalesPayment: TextView = itemView.findViewById(R.id.tvSalesPayment)
        val tvSalesName: TextView = itemView.findViewById(R.id.tvSalesName)
        val tvSalesQty: TextView = itemView.findViewById(R.id.tvSalesQty)
        val tvSalesPrice: TextView = itemView.findViewById(R.id.tvSalesPrice)

    }
}