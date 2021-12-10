package com.example.windowshopper_mvvm.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.windowshopper_mvvm.R
import com.example.windowshopper_mvvm.databinding.ActivityCartBinding
import com.example.windowshopper_mvvm.ui.account.AccountActivity
import com.example.windowshopper_mvvm.ui.shop.ShopActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val navController = findNavController(R.id.navHostFragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.itemDetailsFragment -> { binding.cartBottomnavigation.visibility = View.GONE }
            }
        }

        binding.cartBottomnavigation.menu.findItem(R.id.navigation_menu_cart).isChecked = true
        binding.cartBottomnavigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        binding.cartBottomnavigation.menu.findItem(R.id.navigation_menu_cart).isChecked = true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_menu_account -> {
                val intent = Intent(this@CartActivity, AccountActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }

            R.id.navigation_menu_shop -> {
                val intent = Intent(this@CartActivity, ShopActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            R.id.navigation_menu_cart -> {
//                val intent = Intent(this@CartActivity, CartActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                startActivity(intent)
//                overridePendingTransition(0, 0)
            }
        }
        return true
    }

}