package com.example.windowshopper_mvvm.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.windowshopper_mvvm.R
import com.example.windowshopper_mvvm.databinding.ActivityAccountBinding
import com.example.windowshopper_mvvm.ui.cart.CartActivity
import com.example.windowshopper_mvvm.ui.shop.ShopActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navController = findNavController(R.id.navHostFragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.accountFragment -> {
                    binding.accountBottomnavigation.visibility = View.VISIBLE
                }
                R.id.editAccountFragment -> {
                    binding.accountBottomnavigation.visibility = View.GONE
                }
            }
        }

        binding.accountBottomnavigation.menu.findItem(R.id.navigation_menu_account).isChecked = true
        binding.accountBottomnavigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        binding.accountBottomnavigation.menu.findItem(R.id.navigation_menu_account).isChecked = true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_menu_account -> {
                //Refresh Page or do nothing
            }

            R.id.navigation_menu_shop -> {
                val intent = Intent(this@AccountActivity, ShopActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            R.id.navigation_menu_cart -> {
                val intent = Intent(this@AccountActivity, CartActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
        return true
    }
}