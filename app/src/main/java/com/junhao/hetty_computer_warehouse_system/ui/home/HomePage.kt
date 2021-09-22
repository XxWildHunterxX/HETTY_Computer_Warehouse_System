package com.junhao.hetty_computer_warehouse_system.ui.home

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.junhao.hetty_computer_warehouse_system.R
import com.junhao.hetty_computer_warehouse_system.databinding.ActivityHomePage2Binding
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.app_bar_home_page2.*
import android.content.SharedPreferences
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.android.gms.tasks.OnCompleteListener
import com.junhao.hetty_computer_warehouse_system.ui.login.Fragment_addStaff
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.nav_header_home_page2.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.activityManager
import java.lang.Exception


class HomePage : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomePage2Binding
    private var fabs: FloatingActionButton? = null
    private lateinit var optionsMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_HETTY_Computer_Warehouse_System_NoActionBar)

        binding = ActivityHomePage2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHomePage2.toolbar)
        registerForContextMenu(binding.appBarHomePage2.fab)
/*
        binding.appBarHomePage2.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG) .setAction("Action", null).show()
        }

 */
        val navigationView = binding.navView
        val header = navigationView.getHeaderView(0)
        val staffName = header.findViewById<TextView>(R.id.tvNavProfile)
        val staffEmail = header.findViewById<TextView>(R.id.tvNavEmail)
        val staffImg = header.findViewById<ImageView>(R.id.imgNavProfile)

        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )

        val savedStaffEmail = sharedPreferences.getString("getStaffEmail", null)
        val savedStaffName = sharedPreferences.getString("getStaffName", null)
        val savedStaffImg = sharedPreferences.getString("getStaffImg", null)

        staffName.text = savedStaffName.toString()
        staffEmail.text = savedStaffEmail.toString()

        var imageUri: String? = null
        imageUri = savedStaffImg.toString()
        Picasso.get().load(imageUri).into(staffImg);

        val navControl = findNavController(R.id.nav_host_fragment_content_home_page2)
        val fabSales: FloatingActionButton = findViewById(R.id.fab_sales)
        val fabItem: FloatingActionButton = findViewById(R.id.fab_item)
        val fabPurchase: FloatingActionButton = findViewById(R.id.fab_purchase)
        val fabStaff: FloatingActionButton = findViewById(R.id.fab_staff)
        val fabWarehouse: FloatingActionButton = findViewById(R.id.fab_warehouse)

        fabSales.setOnClickListener {
            //Add Sales Order Fragment here
            navControl.navigateUp()
            navControl.navigate(R.id.nav_searchSalesProduct)
            Toast.makeText(this, "Add Sales Order", Toast.LENGTH_LONG).show()
        }
        fabItem.setOnClickListener {
            //Add item Fragment here
            navControl.navigateUp()
            navControl.navigate(R.id.action_nav_home_to_nav_add_item)
            Toast.makeText(this, "Add Item Product", Toast.LENGTH_LONG).show()
        }
        fabPurchase.setOnClickListener {
            //Add purchase Fragment here
            navControl.navigateUp()
            navControl.navigate(R.id.nav_purchase_create)
            Toast.makeText(this, "Add Purchase Order", Toast.LENGTH_LONG).show()
        }
        fabStaff.setOnClickListener {
            //Add Staff Fragment here
            // Navigation.findNavController(view).navigate(R.id.action_nav_home_to_fragment_addStaff)
            navControl.navigateUp()
            navControl.navigate(R.id.action_nav_home_to_fragment_addStaff)
            Toast.makeText(this, "Add Staff", Toast.LENGTH_LONG).show()
        }
        fabWarehouse.setOnClickListener {
            //Add warehouse Fragment here
            navControl.navigateUp()
            navControl.navigate(R.id.nav_selectWarehouse)
            Toast.makeText(this, "Sent Warehouse", Toast.LENGTH_LONG).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home_page2)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_profile,
                R.id.nav_items,
                R.id.nav_warehouseTracking,
                R.id.nav_salesOrder,
                R.id.nav_purchaseOrders,
                R.id.nav_settings,
                R.id.nav_reports,
                R.id.nav_searchWarehouseProduct,
                R.id.nav_purchase_create_success
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_settings -> {
                val navControl = findNavController(R.id.nav_host_fragment_content_home_page2)
                navControl.navigateUp()
                navControl.navigate(R.id.nav_showNotification)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_page, menu)
        optionsMenu = menu
        optionsMenu.findItem(R.id.action_add).isVisible = false

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home_page2)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun showFloatingActionButton() {
        binding.appBarHomePage2.fab.visibility = View.VISIBLE
    }

    fun hideFloatingActionButton() {
        binding.appBarHomePage2.fab.visibility = View.GONE
    }

    fun setVisibilityForButton(shouldHide: Boolean) {
        if (shouldHide) {
            binding.appBarHomePage2.fab.visibility = View.GONE
        } else {
            binding.appBarHomePage2.fab.visibility = View.VISIBLE
        }
    }


    /*
        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            super.onCreateContextMenu(menu, v, menuInfo)
            menuInflater.inflate(R.menu.floating_context_menu,menu)
        }

        override fun onContextItemSelected(item: MenuItem): Boolean {
            when(item.itemId){
                R.id.item1 -> Toast.makeText(this,"Item 1", Toast.LENGTH_LONG).show()
                R.id.item2 -> Toast.makeText(this,"Item 2", Toast.LENGTH_LONG).show()
                R.id.item3 -> Toast.makeText(this,"Item 3", Toast.LENGTH_LONG).show()
                R.id.item4 -> Toast.makeText(this,"Item 4", Toast.LENGTH_LONG).show()
            }
            return super.onContextItemSelected(item)
        }
    */
    override fun onStop() {
        Log.i("Lifecycle", "onStop")
        super.onStop()

    }

    override fun onDestroy() {

        Log.i("Lifecycle", "onDestroy")
        super.onDestroy()

    }

    override fun onStart() {
        super.onStart()
    }

}