package com.example.windowshopper_mvvm.ui.reviews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.example.windowshopper_mvvm.databinding.ListRowReviewsBinding
import com.example.windowshopper_mvvm.models.Review

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(val binding: ListRowReviewsBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ListRowReviewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = differ.currentList[position]

        holder.binding.rowReviewRating.rating = review.rating.toFloat()
        holder.binding.rowReviewRating.setOnDragListener(null)
        holder.binding.rowReviewTextviewDate.text = review.date
        holder.binding.rowReviewTextviewComment.text = review.comment
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}