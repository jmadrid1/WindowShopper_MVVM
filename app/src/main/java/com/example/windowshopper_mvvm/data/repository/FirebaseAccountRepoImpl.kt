package com.example.windowshopper_mvvm.data.repository

import com.example.windowshopper_mvvm.data.Resource
import com.example.windowshopper_mvvm.models.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
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

class FirebaseAccountRepoImpl @Inject constructor(
    private var database: DatabaseReference
    ) : FirebaseAccountRepo {

    private lateinit var auth: FirebaseAuth

    companion object {
        const val KEY_USERS = "users"
        const val KEY_USERNAME = "username"
        const val KEY_EMAIL = "email"
        const val KEY_PASSWORD = "password"
    }

    override fun getUsersData(uid: String) = callbackFlow<Resource<Account>> {
        database = Firebase.database
            .getReference(KEY_USERS)
            .child(uid)

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.child(KEY_USERNAME).getValue(String::class.java).toString()
                val emailAddress = dataSnapshot.child(KEY_EMAIL).getValue(String::class.java).toString()
                val password = dataSnapshot.child(KEY_PASSWORD).getValue(String::class.java).toString()
                val account = Account(name, emailAddress, password)

                this@callbackFlow.sendBlocking(Resource.success(account))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                this@callbackFlow.sendBlocking(Resource.error(databaseError.toString(), null))
            }
        }
        database.addValueEventListener(userListener)

        awaitClose {
            database.removeEventListener(userListener)
        }
    }

    override fun insertUserAccount(account: Account, uid: String) {
        auth = Firebase.auth

        database = Firebase.database.reference
        database.child(KEY_USERS)
            .child(uid)
            .setValue(account)
    }

    override fun updateEmailAddress(uid: String, email: String) {
        auth = Firebase.auth

        database = Firebase.database.reference
        database.child(KEY_USERS)
            .child(uid)
            .child(KEY_EMAIL)
            .setValue(email)

        auth.currentUser.updateEmail(email)
    }


}
