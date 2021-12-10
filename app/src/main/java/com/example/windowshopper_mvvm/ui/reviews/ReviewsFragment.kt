package com.example.windowshopper_mvvm.ui.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.windowshopper_mvvm.R
import com.example.windowshopper_mvvm.data.Status
import com.example.windowshopper_mvvm.databinding.FragmentReviewsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReviewsFragment : Fragment(R.layout.fragment_reviews) {

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel : ReviewsViewModel by viewModels()

    private lateinit var reviewAdapter : ReviewAdapter
    private val args : ReviewsFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser == null){
            hideUI()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val itemId = args.itemId
            viewModel.getReviews(itemId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        onBackButtonPress()

        initView()
        setObservers()
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
        setupRecyclerView()
        binding.fragmentReviewsImageviewAdd.setOnClickListener{
            val itemId = args.itemId
            val action = ReviewsFragmentDirections.actionReviewsFragmentToSubmitReviewsFragment(itemId)
            findNavController().navigate(action)
        }
    }

    private fun setObservers(){
        viewModel.reviewsList.observe(viewLifecycleOwner) { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    hideProgressBar()
                    result.data?.let { songs ->
                        reviewAdapter.differ.submitList(songs)
                        showList()
                        if(songs.isEmpty()){
                            hideList()
                        }else{
                            showList()
                        }
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

    private fun setupRecyclerView(){
        reviewAdapter = ReviewAdapter()
        binding.fragmentReviewsRecyclerview.apply{
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showProgressBar(){
        binding.fragmentReviewsProgressbar.visibility = View.VISIBLE
        showList()
    }

    private fun hideProgressBar(){
        binding.fragmentReviewsProgressbar.visibility = View.GONE
    }

    private fun showList(){
        binding.fragmentReviewsRecyclerview.visibility = View.VISIBLE
        binding.fragmentReviewsFrameNoReviews.visibility = View.GONE
        binding.fragmentReviewsImageviewNoReviews.visibility = View.GONE
    }

    private fun hideList(){
        binding.fragmentReviewsRecyclerview.visibility = View.GONE
        binding.fragmentReviewsFrameNoReviews.visibility = View.VISIBLE
        binding.fragmentReviewsImageviewNoReviews.visibility = View.VISIBLE
    }

    private fun hideUI(){
        binding.fragmentReviewsImageviewAdd.visibility = View.GONE
    }

}