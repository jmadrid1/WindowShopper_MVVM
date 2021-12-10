package com.example.windowshopper_mvvm.ui.shop

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.windowshopper_mvvm.databinding.ListRowShopBinding
import com.example.windowshopper_mvvm.models.Item

class ShopAdapter : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    inner class ShopViewHolder(val binding: ListRowShopBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val binding = ListRowShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopViewHolder(binding)
    }

    private var onItemClickListener : ((Item) -> Unit)? = null

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.binding.rowShopTextviewTitle.text = item.title
        holder.binding.rowShopTextviewPrice.text = "$" + item.price.toString()

        Glide.with(holder.binding.rowShopImageviewClothes.context)
            .load(item.image)
            .into(holder.binding.rowShopImageviewClothes)

        holder.binding.root.setOnClickListener {
            onItemClickListener?.let {
                it(item)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.i("Jovel Adapter", "Size:  ${differ.currentList.size}")
        return differ.currentList.size
    }

    fun setOnItemClickListener(listener : (Item) -> Unit){
        onItemClickListener = listener
    }

}