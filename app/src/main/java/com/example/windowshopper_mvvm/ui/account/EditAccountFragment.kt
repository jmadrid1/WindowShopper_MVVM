package com.example.windowshopper_mvvm.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.windowshopper_mvvm.R
import com.example.windowshopper_mvvm.databinding.FragmentEditAccountBinding
import com.example.windowshopper_mvvm.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAccountFragment : Fragment(R.layout.fragment_edit_account) {

    private var _binding: FragmentEditAccountBinding? = null
    private val binding get() = _binding!!

    val args: EditAccountFragmentArgs by navArgs()
    private val viewModel : AccountViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser == null){
            navigateToLogin()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackButtonPress()

        val uid = args.userArg

        setupObservers()
        initView(uid)
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

    private fun initView(uid: String){
        binding.fragmentEditAccountImageviewBack.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.fragmentEditAccountButtonUpdate.setOnClickListener {
            updateEmail(uid)
        }
    }

    private fun setupObservers(){
        viewModel.wasEmailUpdated.observe(viewLifecycleOwner) {
            if(it == true){
                Toast.makeText(context, R.string.account_edit_toast_update_success, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }else{
                Toast.makeText(context, R.string.account_edit_toast_update_fail, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToLogin(){
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun updateEmail(uid: String){
        val email = binding.fragmentEditAccountEdittextEmail.text.toString()
        viewModel.updateEmailAddress(uid, email)
    }

}