package com.junhao.hetty_computer_warehouse_system.ui.tracking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.TrackingItemAdapter
import com.junhao.hetty_computer_warehouse_system.data.TrackingItem
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage


class TrackingPendingFragment : Fragment() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("Warehouse").child("warehouse1").child("product")
    var TrackingItemList : ArrayList<TrackingItem> ? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment


        val view = inflater.inflate(R.layout.fragment_tracking_pending, container, false)

        TrackingItemList = arrayListOf<TrackingItem>()

        /* (activity as HomePage?)?.showFloatingActionButton() */
        (activity as HomePage?)?.hideFloatingActionButton()

        myRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot!!.exists()){

                    for (c in snapshot.children){

                        val trackingItem = c.getValue(TrackingItem::class.java)
                        TrackingItemList?.add(trackingItem!!)

                    }

                    val adapter = TrackingItemAdapter(context!!, TrackingItemList!!)

                    val recyclerView: RecyclerView = view.findViewById(R.id.trackingItemRecycleViewPending)


                    recyclerView?.adapter = adapter
                    recyclerView.setHasFixedSize(true)


                }

            }
        })

        return view

    }


}