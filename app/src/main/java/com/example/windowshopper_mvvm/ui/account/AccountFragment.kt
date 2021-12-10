package com.example.windowshopper_mvvm.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.windowshopper_mvvm.R
import com.example.windowshopper_mvvm.data.Status
import com.example.windowshopper_mvvm.databinding.FragmentAccountBinding
import com.example.windowshopper_mvvm.ui.login.LoginActivity
import com.example.windowshopper_mvvm.ui.shop.ShopActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel : AccountViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser == null){
            navigateToLogin()
        }else{
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getUsersData(currentUser.uid)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackButtonPress()

        setupObservers()
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onBackButtonPress(){
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity!!.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initView(){
        hideUI()
        binding.fragmentAccountButtonUpdateEmail.setOnClickListener {
            navigateToAccountEdit()
        }

        binding.fragmentAccountButtonSignOut.setOnClickListener{
            signOut()
        }
    }

    private fun setupObservers(){
        viewModel.username.observe(viewLifecycleOwner) { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    hideProgressBar()
                    result.data?.let { it ->
                        binding.fragmentAccountTextviewGreeting.text = "Welcome back, $it!"
                    }
                }
                Status.ERROR -> {
//                    hideList()
                }
                Status.LOADING -> {
                    showProgressBar()
                }
            }
        }

        viewModel.email.observe(viewLifecycleOwner) { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    result.data?.let { it ->
                        showUI()
                        binding.fragmentAccountTextviewEmail.text = it
                    }
                }
                Status.ERROR -> { hideUI() }
                Status.LOADING -> {
                    showProgressBar()
                    hideUI()
                }
            }
        }
    }

    private fun navigateToLogin(){
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun navigateToShop(){
        val intent = Intent(context, ShopActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun navigateToAccountEdit(){
        val uid : String = auth.currentUser.uid
        val action = AccountFragmentDirections.actionAccountFragmentToAccountEditFragment(uid)
        findNavController().navigate(action)
    }

    private fun signOut(){
        viewModel.signOut()
        navigateToShop()
    }
    private fun showProgressBar(){
        binding.fragmentAccountProgressbar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        binding.fragmentAccountProgressbar.visibility = View.GONE
    }

    private fun showUI(){
        binding.fragmentAccountTextviewGreeting.visibility = View.VISIBLE
        binding.fragmentAccountTextviewGreeting.visibility = View.VISIBLE
        binding.fragmentAccountButtonUpdateEmail.visibility = View.VISIBLE
        binding.fragmentAccountButtonSignOut.visibility = View.VISIBLE
    }

    private fun hideUI(){
        binding.fragmentAccountTextviewGreeting.visibility = View.GONE
        binding.fragmentAccountTextviewGreeting.visibility = View.GONE
        binding.fragmentAccountButtonUpdateEmail.visibility = View.GONE
        binding.fragmentAccountButtonSignOut.visibility = View.GONE
    }

}
