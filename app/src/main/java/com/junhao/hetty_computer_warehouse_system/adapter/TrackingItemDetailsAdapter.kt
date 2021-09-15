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
import com.junhao.hetty_computer_warehouse_system.data.TrackingItemDetails
import com.squareup.picasso.Picasso

class TrackingItemDetailsAdapter(val context: Context, private val TrackingItemDetailsList: ArrayList<TrackingItemDetails>):
    RecyclerView.Adapter<TrackingItemDetailsAdapter.trackingDetailsViewHolder>(){
    class trackingDetailsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        private val trackingItemDetailsDate : TextView = itemView.findViewById(R.id.tvTrackingDate)
        private val trackingItemDetailsTime: TextView = itemView.findViewById(R.id.tvTrackingTime)
        private val trackingItemDetailsDesc : TextView = itemView.findViewById(R.id.tvTrackingDesc)


        fun bind(tid: TrackingItemDetails, context: Context) {
            trackingItemDetailsDate?.text = tid.trackDate
            trackingItemDetailsTime?.text = tid.trackTime
            trackingItemDetailsDesc?.text = tid.trackDesc

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): trackingDetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.tracking_item_details_list,parent,false)

        return trackingDetailsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: trackingDetailsViewHolder, position: Int) {
        holder?.bind(TrackingItemDetailsList[position], context)
    }

    override fun getItemCount(): Int {
        return TrackingItemDetailsList.size
    }


}
