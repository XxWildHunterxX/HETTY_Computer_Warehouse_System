package com.junhao.hetty_computer_warehouse_system.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.junhao.hetty_computer_warehouse_system.data.Product

class SalesOrderProductAdapter (val context: Context, private val ProductItemList: List<Product>) :
RecyclerView.Adapter<SalesOrderProductAdapter.myViewHolder>() {



    class myViewHolder(itemView: View, listener: ProductItemAdapter.onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SalesOrderProductAdapter.myViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: SalesOrderProductAdapter.myViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


}