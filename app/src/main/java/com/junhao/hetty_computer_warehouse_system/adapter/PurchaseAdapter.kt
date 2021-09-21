package com.junhao.hetty_computer_warehouse_system.adapter

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.Purchase


class PurchaseAdapter(val context: Context, private val purchaseList :ArrayList<Purchase>): RecyclerView.Adapter<PurchaseAdapter.myViewHolder>() {
    private lateinit var mListener: onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position :Int)
        fun onDeleteClick(position:Int)
        fun onUpdateClick(position: Int)
    }



    fun setOnItemClickListener(listener:onItemClickListener){
        mListener=listener
    }
    class myViewHolder(itemView:View,listener: onItemClickListener):RecyclerView.ViewHolder(itemView){
        val tvPurchaseID: TextView = itemView.findViewById(R.id.tf_PurchaseID)
        val tvProductName: TextView = itemView.findViewById(R.id.tf_ProductName)
        val tvQty: TextView = itemView.findViewById(R.id.tf_Qty)
        val tvStatus: TextView = itemView.findViewById(R.id.tf_Status)
        val imgDelete : ImageButton = itemView.findViewById(R.id.imgbtn_delete)
        val imgUpdate : ImageButton=itemView.findViewById(R.id.imgbtn_update)
        init{
            itemView.setOnClickListener {
                val position:Int = adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                listener.onItemClick(adapterPosition)}
            }
            imgDelete.setOnClickListener{
                val position:Int = adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    listener.onDeleteClick(adapterPosition)}
            }
            imgUpdate.setOnClickListener{
                val position:Int = adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    listener.onUpdateClick(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.purchase_item,parent ,false)
        return myViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentPurchase = purchaseList[position]
        holder.tvPurchaseID.text = currentPurchase.purchaseID
        holder.tvProductName.text=currentPurchase.purProductName
        holder.tvQty.text =currentPurchase.purQty
        holder.tvStatus.text =currentPurchase.status

        if(currentPurchase.status =="Rejected"){
            holder.tvStatus.setTextColor(Color.parseColor("#FF0000"))
        }
        else if(currentPurchase.status =="Received"){
            holder.tvStatus.setTextColor(Color.parseColor("228B22"))
        }
    }

    override fun getItemCount(): Int {
        return purchaseList.size
    }

}