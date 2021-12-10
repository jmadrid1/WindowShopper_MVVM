package com.example.windowshopper_mvvm.ui.shop

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.windowshopper_mvvm.R
import com.example.windowshopper_mvvm.databinding.FragmentItemDetailsBinding
import com.example.windowshopper_mvvm.models.CartItem
import com.example.windowshopper_mvvm.models.Item
import com.example.windowshopper_mvvm.ui.cart.CartActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ItemDetailsFragment : Fragment(R.layout.fragment_item_details) {

    companion object {
        const val ARGS_KEY = "clothes"
    }

    private var _binding: FragmentItemDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShopViewModel by viewModels()
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
        _binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackButtonPress()

        val item = arguments!!.get(ARGS_KEY) as Item
        initViews(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onBackButtonPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initViews(item: Item) {
        Glide.with(binding.fragmentDetailsImageviewPicture.context)
            .load(item.image)
            .into(binding.fragmentDetailsImageviewPicture)

        binding.fragmentDetailsImageviewCart.setOnClickListener { navigateToCart() }

        binding.fragmentDetailsImageviewBack.setOnClickListener { findNavController().popBackStack() }

        binding.fragmentDetailsTextviewTitle.text = item.title
        binding.fragmentDetailsTextviewPrice.text = "$" + item.price.toString()
        binding.fragmentDetailsTextviewDescription.text = item.summary

        binding.fragmentDetailsRadioSmall.isChecked = true
        binding.fragmentDetailsRadioOne.isChecked = true

        val numberOfReviews: Int = item.reviews?.size ?: 0
        binding.fragmentDetailsTextviewReviews.text = "Reviews (" + numberOfReviews.toString() + ")"

        binding.fragmentDetailsTextviewReviews.setOnClickListener { navigateToReviews(item) }

        binding.fragmentDetailsButtonAdd.setOnClickListener { addToCart(item) }
    }

    private fun addToCart(item: Item){
        val uid : String = auth.currentUser.uid

        val id : Int = item.id
        val title : String = item.title
        val sizeSelectedID : Int = binding.fragmentDetailsRadioGroupSizes.checkedRadioButtonId
        val selectedSizeButton : Button = binding.fragmentDetailsRadioGroupSizes.findViewById(sizeSelectedID)

        val size : String = selectedSizeButton.text.toString()

        val price : Double = String.format("%.2f", item.price).toDouble()
        val thumbnail : String = item.image

        val quantitySelectedID : Int = binding.fragmentDetailsRadioGroupQuantity.checkedRadioButtonId
        val selectedQuantityButton : Button = binding.fragmentDetailsRadioGroupQuantity.findViewById(quantitySelectedID)
        val quantity : Int = selectedQuantityButton.text.toString().toInt()

        val cartItem = CartItem(id, title, size, price, thumbnail, quantity)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addToCart(cartItem, uid)
            findNavController().popBackStack()
        }
    }

    private fun navigateToReviews(item: Item){
        val id : String = item.id.toString()
        val action = ItemDetailsFragmentDirections.actionItemDetailsFragmentToReviewsFragment(id)
        findNavController().navigate(action)
    }

    private fun navigateToCart(){
        val intent = Intent(context, CartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}