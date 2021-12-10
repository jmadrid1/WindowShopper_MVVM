package com.example.windowshopper_mvvm.ui.cart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.windowshopper_mvvm.R
import com.example.windowshopper_mvvm.data.Status
import com.example.windowshopper_mvvm.databinding.FragmentCartBinding
import com.example.windowshopper_mvvm.models.CartItem
import com.example.windowshopper_mvvm.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart) {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel : CartViewModel by viewModels()
    private lateinit var cartAdapter : CartAdapter

    private lateinit var auth: FirebaseAuth
    private var cartList: MutableList<CartItem> = mutableListOf()

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser == null){
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }else{
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getCartItems(auth.currentUser.uid)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        cartList = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackButtonPress()

        initView()
        setupObservers()
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
        resetItemsUI()
        hideProgressBar()
        setupRecyclerView()
        binding.fragmentCartImageviewBack.setOnClickListener { findNavController().popBackStack() }
        cartAdapter.setOnItemClickListener { viewLifecycleOwner.lifecycleScope.launch { removeItemFromList(it) } }
    }

    private fun setupObservers(){
        viewModel.numOfCartItems.observe( viewLifecycleOwner, {
            if(it == 0) binding.fragmentCartButtonPayment.isEnabled = false

            binding.fragmentCartTextviewCartTotalItems.text = "Items:  "+ it.toString()
        })

        viewModel.totalAmountOfCart.observe( viewLifecycleOwner, {
            binding.fragmentCartTextviewCartTotalAmount.text = "Amount:  $" + it.toString()
        })

        viewModel.cartList.observe(viewLifecycleOwner) { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    hideProgressBar()
                    result.data?.let { items ->
                        cartList = items.toMutableList()
                        cartAdapter.differ.submitList(items)
                        showList()
                    }
                }
                Status.ERROR -> {
                    hideList()
                    resetItemsUI()
                }
                Status.LOADING -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun setupRecyclerView(){
        cartAdapter = CartAdapter()
        binding.fragmentCartRecyclerview.apply{
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private suspend fun removeItemFromList(position: Int){
        val it = cartList[position]
        cartList.removeAt(position)

        val uid : String = auth.currentUser.uid
        viewLifecycleOwner.lifecycleScope.launch { viewModel.removeItemFromList(uid, it.id.toString(), position) }

        if(cartList.isEmpty()) resetItemsUI()

        cartAdapter.differ.submitList(cartList)
        cartAdapter.notifyDataSetChanged()
    }

    private fun showProgressBar(){
        binding.fragmentCartProgressbar.visibility = View.VISIBLE
        showList()
    }

    private fun hideProgressBar(){
        binding.fragmentCartProgressbar.visibility = View.GONE
    }

    private fun showList(){
        binding.fragmentCartRecyclerview.visibility = View.VISIBLE
        binding.fragmentCartFrameNoCart.visibility = View.GONE
    }

    private fun hideList(){
        binding.fragmentCartRecyclerview.visibility = View.GONE
        binding.fragmentCartFrameNoCart.visibility = View.VISIBLE
    }

    private fun resetItemsUI(){
        binding.fragmentCartFrameNoCart.visibility = View.VISIBLE
        binding.fragmentCartProgressbar.visibility = View.GONE

        binding.fragmentCartTextviewCartTotalItems.text = "Items:  0"
        binding.fragmentCartTextviewCartTotalAmount.text = "Amount:  $0.00"
    }

}