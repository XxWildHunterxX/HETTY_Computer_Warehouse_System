package com.junhao.hetty_computer_warehouse_system.ui.home

import android.os.Bundle
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.app_bar_home_page2.*

class HomePage : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomePage2Binding
    var trackingtab: TabLayout? = null
    var trackingviewpager: ViewPager? = null
    lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_HETTY_Computer_Warehouse_System_NoActionBar)

        binding = ActivityHomePage2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHomePage2.toolbar)
        registerForContextMenu(binding.appBarHomePage2.fab)

        this.setVisibilityForButton(false)


        //registerForContextMenu(binding.appBarHomePage2.fab)

        binding.appBarHomePage2.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
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
        binding.appBarHomePage2.fab.show()
    }

    fun hideFloatingActionButton() {
        binding.appBarHomePage2.fab.hide()
    }

    fun setVisibilityForButton(shouldHide: Boolean) {
        if (shouldHide) {
            binding.appBarHomePage2.fab.hide()
        } else {
            binding.appBarHomePage2.fab.show()
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
}