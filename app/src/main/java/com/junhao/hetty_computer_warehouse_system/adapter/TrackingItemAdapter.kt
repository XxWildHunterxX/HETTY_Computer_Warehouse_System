package com.junhao.hetty_computer_warehouse_system.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.TrackingItem
import com.squareup.picasso.Picasso

class TrackingItemAdapter(val context: Context, private val TrackingItemList: ArrayList<TrackingItem>) :
    RecyclerView.Adapter<TrackingItemAdapter.trackingViewHolder>(){

    private var  vListener: onItemClickListener? = null

    interface onItemClickListener {

        fun onItemClick(
            productName: String,
            warehouseInvNumber:String

        )
    }

    fun setOnItemClickListener(listener: onItemClickListener) {

        vListener = listener

    }


    class trackingViewHolder(itemView: View, listener: onItemClickListener?):RecyclerView.ViewHolder(itemView){

        private val trackingItemName : TextView = itemView.findViewById(R.id.tvTrackingItemName)
        private val trackingItemImage: ImageView = itemView.findViewById(R.id.imgTrackingItem)
        private val trackingItemInvNumber : TextView = itemView.findViewById(R.id.tvTrackingItemNumber)
        private val trackingItemInvStatus : TextView = itemView.findViewById(R.id.tvTrackingItemStatus)

        /*
        //For NOTIFICATION
        private val notificationItemImage : ImageView = itemView.findViewById(R.id.imgNotification)
        private val notificationItemName : TextView = itemView.findViewById(R.id.tvNotificationItemName)
        private val notificationItemQty : TextView = itemView.findViewById(R.id.tvNotificationItemQty)
        private val notificationItemMsg : TextView = itemView.findViewById(R.id.tvNotificationMsg)


         */

        @SuppressLint("SetTextI18n")
        fun bind(ti: TrackingItem, context: Context) {
            var imageUri : String? = null
            imageUri = ti.productImg
            Picasso.get().load(imageUri).into(trackingItemImage)
            trackingItemName?.text = ti.productName
            trackingItemInvNumber?.text = ti.warehouseInvNumber
            trackingItemInvStatus?.text = ti.warehouseInvStatus

            /*
            //FOR NOTIFICATION
            Picasso.get().load(imageUri).into(notificationItemImage)
            notificationItemName?.text = ti.productName
            notificationItemQty?.text = ti.warehouseInvQty
            notificationItemMsg?.text = ti.warehouseInvReq + " request the following item: "

             */
        }

        init{
            itemView.setOnClickListener{
                listener?.onItemClick(
                    trackingItemName?.text.toString(),
                    trackingItemInvNumber?.text.toString()

                )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): trackingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.tracking_item_list,parent,false)

        return trackingViewHolder(itemView, vListener)
    }

    override fun onBindViewHolder(holder: trackingViewHolder, position: Int) {
        holder?.bind(TrackingItemList[position], context)

    }

    override fun getItemCount(): Int {
        return TrackingItemList.size
    }

}