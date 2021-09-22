package com.junhao.hetty_computer_warehouse_system.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.data.TrackingItem
import com.squareup.picasso.Picasso

class NotificationItemAdapter(val context: Context, private val NotificationItemList: ArrayList<TrackingItem>) :
    RecyclerView.Adapter<NotificationItemAdapter.NotificationViewHolder>() {

    private lateinit var mListener: NotificationItemAdapter.onItemClickListener

    interface onItemClickListener {

        fun onAcceptClick(productName: String, warehouseInvQty: String, warehouseInvNumber:String)


    }

    fun setOnItemClickListener(listener: NotificationItemAdapter.onItemClickListener) {

        mListener = listener

    }


        class NotificationViewHolder(itemView: View, listener: NotificationItemAdapter.onItemClickListener): RecyclerView.ViewHolder(itemView){


            //For NOTIFICATION
            private val notificationItemImage : ImageView = itemView.findViewById(R.id.imgNotification)
            private val notificationItemName : TextView = itemView.findViewById(R.id.tvNotificationItemName)
            private val notificationItemQty : TextView = itemView.findViewById(R.id.tvNotificationItemQty)
            private val notificationItemMsg : TextView = itemView.findViewById(R.id.tvNotificationMsg)
            private var notificationItemInvNumber = ""

            private val notificationAcceptButton : Button = itemView.findViewById(R.id.btnNotificationAccept)




            @SuppressLint("SetTextI18n")
            fun bind(ti: TrackingItem, context: Context) {
                var imageUri : String? = null
                imageUri = ti.productImg

                //FOR NOTIFICATION
                Picasso.get().load(imageUri).into(notificationItemImage)
                notificationItemName?.text = ti.productName
                notificationItemQty?.text = ti.warehouseInvQty
                notificationItemMsg?.text = ti.warehouseInvReq + " request the following item: "

                notificationItemInvNumber = ti.warehouseInvNumber.toString()

            }

            init {


                notificationAcceptButton.setOnClickListener {
                    if (listener != null) {
                        val position: Int = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onAcceptClick(notificationItemName.text.toString(),notificationItemQty.text.toString(), notificationItemInvNumber)
                        }

                    }
                }




            }



        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.notification_item_list,parent,false)

            return NotificationViewHolder(itemView, mListener)
        }

        override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
            holder?.bind(NotificationItemList[position], context)



        }

        override fun getItemCount(): Int {
            return NotificationItemList.size
        }
}