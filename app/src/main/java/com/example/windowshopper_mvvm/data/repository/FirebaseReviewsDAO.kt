package com.example.windowshopper_mvvm.data.repository

import com.example.windowshopper_mvvm.data.Resource
import com.example.windowshopper_mvvm.models.Review
import kotlinx.coroutines.flow.Flow

interface FirebaseReviewsDAO {

    fun getReviews(itemId: String): Flow<Resource<List<Review>>>

    fun submitReview(itemID: String, review: Review)

}