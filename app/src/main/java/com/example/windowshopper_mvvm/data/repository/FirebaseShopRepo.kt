package com.example.windowshopper_mvvm.data.repository

import com.example.windowshopper_mvvm.data.Resource
import com.example.windowshopper_mvvm.models.CartItem
import com.example.windowshopper_mvvm.models.Item
import kotlinx.coroutines.flow.Flow

interface FirebaseShopDAO {

    fun getClothes(): Flow<Resource<List<Item>>>

    fun addToCart(cartItem: CartItem, uid: String)

}