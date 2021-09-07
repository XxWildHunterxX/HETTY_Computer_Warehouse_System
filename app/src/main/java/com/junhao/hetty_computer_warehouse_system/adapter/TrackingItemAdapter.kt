package com.junhao.hetty_computer_warehouse_system.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.TrackingItem
import com.squareup.picasso.Picasso

class TrackingItemAdapter(val context: Context, val TrackingItemList: ArrayList<TrackingItem>) :
    RecyclerView.Adapter<TrackingItemAdapter.ViewHolder>(){


    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView!!){

        val TrackingItemName : TextView = itemView.findViewById(R.id.tvTrackingItemName)
        val TrackingItemPrice : TextView = itemView.findViewById(R.id.tvTrackingItemPrice)
        val TrackingItemImage: ImageView = itemView.findViewById(R.id.imgTrackingItem)


        fun bind(ti: TrackingItem, context: Context) {
            var imageUri : String? = null
            imageUri = ti.productImg
            Picasso.get().load(imageUri).into(TrackingItemImage);
            TrackingItemName?.text = ti.productName
            TrackingItemPrice?.text = ti.productType

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.tracking_item_list,parent,false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bind(TrackingItemList[position], context)

    }

    override fun getItemCount(): Int {
        return TrackingItemList.size
    }

}