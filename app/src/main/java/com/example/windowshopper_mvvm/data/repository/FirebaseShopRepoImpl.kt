package com.example.windowshopper_mvvm.data.repository

import com.example.windowshopper_mvvm.data.Resource
import com.example.windowshopper_mvvm.models.CartItem
import com.example.windowshopper_mvvm.models.Item
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseShopRepoImpl @Inject constructor(
    private var database: DatabaseReference
) : FirebaseShopDAO {

    companion object {
        const val KEY_USERS = "users"
        const val KEY_CART = "cart"
    }

    override fun getClothes() = callbackFlow<Resource<List<Item>>> {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val queryList = mutableListOf<Item>()
                if(dataSnapshot!!.exists()){
                    for (e in dataSnapshot.children){
                        val item = e.getValue(Item::class.java)
                        if(item != null){
                            queryList.add(item)
                        }
                    }
                    this@callbackFlow.sendBlocking(Resource.success(queryList))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                this@callbackFlow.sendBlocking(Resource.error(databaseError.toString(), emptyList()))
            }
        }
        database.addValueEventListener(postListener)

        awaitClose {
            database.removeEventListener(postListener)
        }
    }

    override fun addToCart(cartItem: CartItem, uid: String) {
        database = Firebase.database.reference
        database.child(KEY_USERS)
            .child(uid)
            .child(KEY_CART)
            .child(cartItem.id.toString())
            .setValue(cartItem)
    }

}