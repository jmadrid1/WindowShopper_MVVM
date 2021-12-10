package com.example.windowshopper_mvvm.ui.reviews

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.windowshopper_mvvm.R
import com.example.windowshopper_mvvm.databinding.FragmentSubmitReviewBinding
import com.example.windowshopper_mvvm.models.Review
import com.example.windowshopper_mvvm.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SubmitReviewFragment : Fragment(R.layout.fragment_submit_review) {

    private var _binding: FragmentSubmitReviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private val args : SubmitReviewFragmentArgs by navArgs()
    private val viewModel : ReviewsViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser == null){
            redirectToLogin()
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
        _binding = FragmentSubmitReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackButtonPress()

        initView()
        initListeners()
        initObserver()
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

    private fun initView(){
        binding.fragmentSubmitReviewImageviewBack.setOnClickListener { findNavController().popBackStack() }

        binding.fragmentSubmitReviewButtonSubmit.setOnClickListener {
            submitReview()
        }
    }

    private fun initListeners() {
        binding.fragmentSubmitReviewEdittextText.addTextChangedListener {
            viewModel.setReview(it.toString())
        }
    }

    private fun initObserver(){
        lifecycleScope.launch {
            viewModel.isSubmitEnabled.collect { value ->
                binding.fragmentSubmitReviewButtonSubmit.isEnabled = value
            }
        }
        lifecycleScope.launch {
            viewModel.characterCount.collect { value ->
                binding.fragmentSubmitReviewTextviewTextCount.text = "$value/150"
            }
        }
    }

    private fun submitReview(){
        val itemId = args.itemId.toInt()
        val comment = _binding!!.fragmentSubmitReviewEdittextText.text.toString() ?: ""

        val sdf = SimpleDateFormat("M/dd/yyyy")
        val currentDate = sdf.format(Date())

        val rating = binding.fragmentSubmitReviewRatingStars.rating.toInt()
        val review = Review(itemId, comment, currentDate,rating)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.submitReview(itemId.toString(), review)
            findNavController().popBackStack()
        }
    }

    private fun redirectToLogin(){
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}