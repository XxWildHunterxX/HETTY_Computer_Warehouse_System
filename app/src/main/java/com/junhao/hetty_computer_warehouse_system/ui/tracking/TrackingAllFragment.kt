package com.junhao.hetty_computer_warehouse_system.ui.tracking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.adapter.TrackingAdapter
import com.junhao.hetty_computer_warehouse_system.adapter.TrackingItemAdapter
import com.junhao.hetty_computer_warehouse_system.data.TrackingItem
import com.junhao.hetty_computer_warehouse_system.databinding.FragmentTrackingAllBinding
import com.junhao.hetty_computer_warehouse_system.databinding.ProductItemBinding
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.Query;
import com.junhao.hetty_computer_warehouse_system.ui.home.HomePage


class TrackingAllFragment : Fragment() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("Warehouse").child("warehouse1").child("product")
    var TrackingItemList : ArrayList<TrackingItem> ? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_tracking_all, container, false)


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

                    val recyclerView: RecyclerView = view.findViewById(R.id.trackingItemRecycleViewAll)


                    recyclerView?.adapter = adapter
                    recyclerView.setHasFixedSize(true)


                }

            }
        })

        return view
    }

    //edit text here

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



}


