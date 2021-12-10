package com.example.windowshopper_mvvm.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.windowshopper_mvvm.R
import com.example.windowshopper_mvvm.databinding.FragmentLoginBinding
import com.example.windowshopper_mvvm.ui.shop.ShopActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private val viewModel : LoginViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            navigateToShop()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackButtonPress()

        initView()
        initListeners()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onBackButtonPress(){
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                navigateToShop()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initView(){
        binding.fragmentLoginImageviewBack.setOnClickListener { navigateToShop() }

        binding.fragmentLoginButtonCreateAccount.setOnClickListener { navigateToCreateAccount()  }

        binding.fragmentLoginButtonSignIn.setOnClickListener { signIn() }
    }

    private fun initListeners() {
        binding.fragmentLoginEdittextEmail.addTextChangedListener {
            viewModel.setEmail(it.toString())
        }
        binding.fragmentLoginEdittextPassword.addTextChangedListener {
            viewModel.setPassword(it.toString())
        }
    }

    private fun setupObservers(){
        lifecycleScope.launch {
            viewModel.isLoginEnabled.collect { value ->
                binding.fragmentLoginButtonSignIn.isEnabled = value
            }
        }

        viewModel.isUserLoggedIn.observe(viewLifecycleOwner) { it ->
            if(it == true){
                Toast.makeText(context, R.string.login_toast_welcome, Toast.LENGTH_SHORT).show()
                navigateToShop()
            }else{
                Toast.makeText(context, R.string.login_toast_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn(){
        val email: String = binding.fragmentLoginEdittextEmail.text.toString()
        val password: String = binding.fragmentLoginEdittextPassword.text.toString()

        viewModel.signIn(email, password)
    }

    private fun navigateToCreateAccount(){
        findNavController().navigate(R.id.signUpFragment)
    }

    private fun navigateToShop(){
        val intent = Intent(context, ShopActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}