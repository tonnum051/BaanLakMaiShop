package com.natchanon.baanlakmaishop

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.natchanon.baanlakmaishop.fragment.AddProductFragment
import com.natchanon.baanlakmaishop.fragment.HomeFragment
import com.natchanon.baanlakmaishop.fragment.ProductListFragment
import com.natchanon.baanlakmaishop.model.ProductRepository

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ===== เริ่มต้น ProductRepository =====
        ProductRepository.initialize(this)

        // ===== Toolbar =====
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        // ===== Drawer =====
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // ===== Drawer menu =====
        findViewById<NavigationView>(R.id.main_navigation_view)
            .setNavigationItemSelectedListener { item ->
                handleNavigation(item.itemId)
                drawerLayout.closeDrawers()
                true
            }

        // ===== Bottom navigation =====
        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setOnItemSelectedListener { item ->
                handleNavigation(item.itemId)
                true
            }

        // ===== หน้าเริ่มต้น =====
        if (savedInstanceState == null) {
            navigateTo(HomeFragment())
        }
    }

    // รวม logic เปลี่ยนหน้าไว้ที่เดียว
    private fun handleNavigation(itemId: Int) {
        when (itemId) {
            R.id.homeFragment -> navigateTo(HomeFragment())
            R.id.productFragment -> navigateTo(ProductListFragment())
            R.id.addProductFragment -> navigateTo(AddProductFragment())
        }
    }

    // เรียกจาก Fragment ได้
    fun navigateTo(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .commit()
    }
    fun openProductList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, ProductListFragment())
            .commit()
    }
}
