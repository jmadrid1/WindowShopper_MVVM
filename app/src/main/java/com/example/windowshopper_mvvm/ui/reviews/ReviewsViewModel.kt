package com.example.windowshopper_mvvm.ui.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.windowshopper_mvvm.data.repository.FirebaseReviewsRepoImpl
import com.example.windowshopper_mvvm.data.Resource
import com.example.windowshopper_mvvm.data.Status
import com.example.windowshopper_mvvm.models.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(private val _firebaseImpl: FirebaseReviewsRepoImpl): ViewModel() {

    private val _reviewsList = MutableLiveData<Resource<List<Review>>>()
    val reviewsList: LiveData<Resource<List<Review>>> = _reviewsList

    private val _reviewMessage = MutableStateFlow("")
    private val _reviewCharacterCount = MutableStateFlow(0)

    val isSubmitEnabled: Flow<Boolean> = combine(_reviewMessage) {
        return@combine _reviewMessage.value.isNotEmpty()
    }

    val characterCount: Flow<Int> = combine(_reviewMessage) {
        return@combine _reviewMessage.value.length
    }

    fun setReview(review: String) {
        _reviewMessage.value = review
        _reviewCharacterCount.value = review.length
    }

    @ExperimentalCoroutinesApi
    suspend fun submitReview(itemID: String, review: Review){
        viewModelScope.launch {
            _firebaseImpl.submitReview(itemID, review)
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun getReviews(itemID: String){
        _firebaseImpl.getReviews(itemID).collect {
            _reviewsList.postValue(Resource.loading(null))
            when(it.status) {
                Status.SUCCESS -> {
                    it.data?.let { reviews ->
                        _reviewsList.postValue(Resource.success(reviews))
                    }
                }
                Status.ERROR -> { _reviewsList.postValue(Resource.error("Failed to get reviews from Firebase", emptyList()))}
            }
        }
    }

}