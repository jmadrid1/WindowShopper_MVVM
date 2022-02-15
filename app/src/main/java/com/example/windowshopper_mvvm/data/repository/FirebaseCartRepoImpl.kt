package com.example.windowshopper_mvvm.data.repository

import com.example.windowshopper_mvvm.data.Resource
import com.example.windowshopper_mvvm.models.CartItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseCartRepoImpl @Inject constructor(
private var database: DatabaseReference
) : FirebaseCartRepo {

    companion object {
        const val KEY_USERS = "users"
        const val KEY_CART = "cart"
    }

    override fun getCartItems(uid :String) = callbackFlow<Resource<List<CartItem>>> {
        database = Firebase.database
            .getReference(KEY_USERS)
            .child(uid)
            .child(KEY_CART)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val queryList = mutableListOf<CartItem>()
                if(dataSnapshot.exists()){
                    for (e in dataSnapshot.children){
                        val item = e.getValue(CartItem::class.java)
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

    override fun removeItemFromList(uid: String, itemID: String, position: Int) {
        database = Firebase.database.reference
        database.child(KEY_USERS)
            .child(uid)
            .child(KEY_CART)
            .child(itemID)
            .removeValue()
    }

}