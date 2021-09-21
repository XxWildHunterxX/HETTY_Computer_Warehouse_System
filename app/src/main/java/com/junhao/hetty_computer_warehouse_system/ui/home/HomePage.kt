package com.junhao.hetty_computer_warehouse_system.ui.home

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
import androidx.navigation.Navigation
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.android.gms.tasks.OnCompleteListener
import com.junhao.hetty_computer_warehouse_system.ui.login.Fragment_addStaff


class HomePage : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomePage2Binding
    private var fabs :FloatingActionButton? = null

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
        val navControl = findNavController(R.id.nav_host_fragment_content_home_page2)
        val fabSales : FloatingActionButton = findViewById(R.id.fab_sales)
        val fabItem : FloatingActionButton = findViewById(R.id.fab_item)
        val fabPurchase : FloatingActionButton = findViewById(R.id.fab_purchase)
        val fabStaff : FloatingActionButton =findViewById(R.id.fab_staff)
        val fabWarehouse : FloatingActionButton = findViewById(R.id.fab_warehouse)

        fabSales.setOnClickListener {
            //Add Sales Order Fragment here
            Toast.makeText(this, "Add Sales Order", Toast.LENGTH_LONG).show()
        }
        fabItem.setOnClickListener {
            //Add Sales Order Fragment here
            navControl.navigateUp()
            navControl.navigate(R.id.nav_add_item)
            Toast.makeText(this, "Add Item Product", Toast.LENGTH_LONG).show()
        }
        fabPurchase.setOnClickListener {
            //Add Sales Order Fragment here
            navControl.navigate(R.id.nav_purchase_create)
            Toast.makeText(this, "Add Purchase Order", Toast.LENGTH_LONG).show()
        }
        fabStaff.setOnClickListener {
            //Add Sales Order Fragment here
           // Navigation.findNavController(view).navigate(R.id.action_nav_home_to_fragment_addStaff)
            navControl.navigateUp()
            navControl.navigate(R.id.action_nav_home_to_fragment_addStaff)
            Toast.makeText(this, "Add Staff", Toast.LENGTH_LONG).show()
        }
        fabWarehouse.setOnClickListener {
            //Add Sales Order Fragment here
            Toast.makeText(this, "Sent Warehouse", Toast.LENGTH_LONG).show()
        }


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home_page2)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_items,R.id.nav_warehouseTracking,R.id.nav_salesOrder,R.id.nav_purchaseOrders,R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_page, menu)
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

}