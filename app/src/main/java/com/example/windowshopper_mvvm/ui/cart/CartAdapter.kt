package com.example.windowshopper_mvvm.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.windowshopper_mvvm.databinding.ListRowCartBinding
import com.example.windowshopper_mvvm.models.CartItem

class CartAdapter() : RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {

    inner class CartItemViewHolder(val binding: ListRowCartBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = ListRowCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemViewHolder(binding)
    }

    private var onItemClickListener : ((Int) -> Unit)? = null

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.binding.rowCartImageviewDelete.setOnClickListener {
            onItemClickListener?.let {
                it(position)
            }
        }

        holder.binding.rowCartTextviewTitle.text = item.title
        holder.binding.rowCartTextviewQuantity.text = "Qty: " + item.quantity.toString()
        holder.binding.rowCartTextviewPrice.text = "$ " + String.format("%.2f", item.price).toDouble()

        Glide.with(holder.binding.rowCartImageviewThumbnail.context)
            .load(item.thumbnail)
            .into(holder.binding.rowCartImageviewThumbnail)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun setOnItemClickListener(listener : (Int) -> Unit){
        onItemClickListener = listener
    }

}