package com.example.windowshopper_mvvm.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.windowshopper_mvvm.R
import com.example.windowshopper_mvvm.data.Status
import com.example.windowshopper_mvvm.databinding.FragmentShopBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopFragment : Fragment(R.layout.fragment_shop) {

    companion object {
        const val BUNDLE_KEY = "clothes"
    }

    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!

    private val viewModel : ShopViewModel by viewModels()
    private lateinit var itemAdapter : ShopAdapter

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = lifecycleScope.launch {
            while (true) {
               viewModel.getClothes()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackButtonPress()
        initViews()
        setupObserver()
    }

    override fun onStop() {
        job?.cancel()
        job = null
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObserver(){
        viewModel.clothesList.observe(viewLifecycleOwner) { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    hideProgressBar()
                    result.data?.let { items ->
                        itemAdapter.differ.submitList(items)
                        showList()
                    }
                }
                Status.ERROR -> {
                    hideList()
                }
                Status.LOADING -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun onBackButtonPress(){
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity!!.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setupRecyclerView(){
        itemAdapter = ShopAdapter()
        binding.fragmentShopRecyclerview.apply{
            adapter = itemAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        itemAdapter.notifyDataSetChanged()
    }

    private fun initViews(){
        setupRecyclerView()
        itemAdapter.setOnItemClickListener {
            val bundle = Bundle().apply { putSerializable(BUNDLE_KEY, it) }
            findNavController().navigate(R.id.action_shopFragment_to_itemDetailsFragment, bundle)
        }
    }

    private fun showProgressBar(){
        binding.fragmentShopProgressbar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        binding.fragmentShopProgressbar.visibility = View.GONE
    }

    private fun showList(){
        binding.fragmentShopRecyclerview.visibility = View.VISIBLE
    }

    private fun hideList(){
        binding.fragmentShopRecyclerview.visibility = View.GONE
    }

}
