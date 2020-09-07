package com.example.spicezilla.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.spicezilla.*
import com.example.spicezilla.fragment.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout:DrawerLayout
    lateinit var coordLayout:CoordinatorLayout
    lateinit var frameLayout: FrameLayout
    lateinit var NavigationView:NavigationView
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var sharedpreference:SharedPreferences

    var previousmenuItem:MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedpreference=getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        drawerLayout = findViewById(R.id.drawerLayout)
        coordLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        toolbar = findViewById(R.id.toolbar)
        NavigationView = findViewById(R.id.navigation)
        toolbar()
        openHome()

        val actionbarDrawerToggle=ActionBarDrawerToggle(this@MainActivity,drawerLayout,R.string.open_drawer,R.string.close_drawer)
        drawerLayout.addDrawerListener(actionbarDrawerToggle)
        actionbarDrawerToggle.syncState()

        NavigationView.setNavigationItemSelectedListener {
            if(previousmenuItem!=null)
            {
                previousmenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousmenuItem=it
            when(it.itemId)
            {
                R.id.home ->{
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.profile ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frame,ProfileFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="Profile"
                }
                R.id.favorite ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frame, FavoriteFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="Favorite"
                }
                R.id.order_history ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frame, OrderHistoryFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="Order History"
                }
                R.id.FAQ ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frame, FAQFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="FAQ"
                }
                R.id.Logout ->{
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want exit?")
                        .setPositiveButton("Yes") { _, _ ->
                            sharedpreference.edit().clear().apply()
                            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
                            ActivityCompat.finishAffinity(this)
                        }
                        .setNegativeButton("No") { _, _ ->openHome()
                        }
                        .create().show()
                }
            }
            return@setNavigationItemSelectedListener false
        }
    }
        private fun toolbar()
        {
            setSupportActionBar(toolbar)
            supportActionBar?.title="Toolbar Title"
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
    private fun openHome()
    {
        val fragment= HomeFragment()
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame,fragment)
        transaction.commit()
        supportActionBar?.title="All Restaurants"
        NavigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.frame)
        when(frag){
            !is HomeFragment ->openHome()
            else->super.onBackPressed()
        }
    }
}