package com.example.windowshopper_mvvm.ui.account

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
import com.example.windowshopper_mvvm.databinding.FragmentSignUpBinding
import com.example.windowshopper_mvvm.ui.login.LoginViewModel
import com.example.windowshopper_mvvm.ui.shop.ShopActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val viewModel : LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initView()
        initObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onBackButtonPress(){
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initView(){
        binding.fragmentSignUpButtonSignUp.setOnClickListener { signUpNewUser() }
        binding.fragmentSignUpImageviewBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initListeners() {
        binding.fragmentSignUpEdittextUsername.addTextChangedListener {
            viewModel.setUsername(it.toString())
        }

        binding.fragmentSignUpEdittextEmail.addTextChangedListener {
            viewModel.setEmail(it.toString())
        }

        binding.fragmentSignUpEdittextPassword.addTextChangedListener {
            viewModel.setPassword(it.toString())
        }

        binding.fragmentSignUpEdittextConfirmPassword.addTextChangedListener {
            viewModel.setConfirmPassword(it.toString())
        }
    }

    private fun initObserver(){
        lifecycleScope.launch {
            viewModel.isSignUpEnabled.collect { value ->
                binding.fragmentSignUpButtonSignUp.isEnabled = value
            }
        }

        viewModel.wasNewUserCreated.observe(viewLifecycleOwner) { it ->
            if(it == true){
                Toast.makeText(context, R.string.account_created_snackbar_success, Toast.LENGTH_SHORT).show()
                navigateToShop()
            }else{
                Toast.makeText(context,  R.string.account_created_snackbar_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpNewUser(){
        val username: String = binding.fragmentSignUpEdittextUsername.text.toString()
        val email: String = binding.fragmentSignUpEdittextEmail.text.toString()
        val password: String = binding.fragmentSignUpEdittextPassword.text.toString()

        viewModel.signUpNewUser(username, email, password)
    }

    private fun navigateToShop(){
        val intent = Intent(context, ShopActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}