package com.example.windowshopper_mvvm.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.windowshopper_mvvm.R
import com.example.windowshopper_mvvm.databinding.ActivityLoginBinding
import com.example.windowshopper_mvvm.ui.cart.CartActivity
import com.example.windowshopper_mvvm.ui.shop.ShopActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navController = findNavController(R.id.navHostFragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> {
                    binding.loginBottomnavigation.visibility = View.GONE
                }
                R.id.signUpFragment -> {
                    binding.loginBottomnavigation.visibility = View.GONE
                }
            }
        }

        binding.loginBottomnavigation.menu.findItem(R.id.navigation_menu_account).isChecked = true
        binding.loginBottomnavigation!!.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_menu_account -> {

            }

            R.id.navigation_menu_shop -> {
                val intent = Intent(this@LoginActivity, ShopActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            R.id.navigation_menu_cart -> {
                val intent = Intent(this@LoginActivity, CartActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
        return true
    }
}

