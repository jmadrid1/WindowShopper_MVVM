package com.example.windowshopper_mvvm.data.repository

import com.example.windowshopper_mvvm.data.Resource
import com.example.windowshopper_mvvm.models.Account
import kotlinx.coroutines.flow.Flow

interface FirebaseAccountRepo {

    fun getUsersData(uid: String): Flow<Resource<Account>>

    fun insertUserAccount(account: Account, uid: String)

    fun updateEmailAddress(uid: String, email: String)

}