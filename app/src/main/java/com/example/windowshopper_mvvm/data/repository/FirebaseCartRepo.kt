package com.example.windowshopper_mvvm.data.repository

import com.example.windowshopper_mvvm.data.Resource
import com.example.windowshopper_mvvm.models.CartItem
import kotlinx.coroutines.flow.Flow

interface FirebaseCartRepo {

    fun getCartItems(uid: String): Flow<Resource<List<CartItem>>>

    fun removeItemFromList(uid: String, itemID: String, position: Int)

}