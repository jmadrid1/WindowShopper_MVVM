package com.example.windowshopper_mvvm.ui.shop

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.windowshopper_mvvm.R
import com.example.windowshopper_mvvm.databinding.ActivityShopBinding
import com.example.windowshopper_mvvm.ui.account.AccountActivity
import com.example.windowshopper_mvvm.ui.cart.CartActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityShopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navController = findNavController(R.id.navHostFragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.shopFragment -> { binding.shopBottomnavigation.visibility = View.VISIBLE }
                R.id.itemDetailsFragment -> { binding.shopBottomnavigation.visibility = View.GONE }
            }
        }

        binding.shopBottomnavigation.menu.findItem(R.id.navigation_menu_shop).isChecked = true
        binding.shopBottomnavigation!!.setOnNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        binding.shopBottomnavigation.menu.findItem(R.id.navigation_menu_shop).isChecked = true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_menu_account -> {
                val intent = Intent(this@ShopActivity, AccountActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }

            R.id.navigation_menu_shop -> {

            }
            R.id.navigation_menu_cart -> {
                val intent = Intent(this@ShopActivity, CartActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
        return true
    }

}