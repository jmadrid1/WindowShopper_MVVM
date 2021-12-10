package com.example.windowshopper_mvvm.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.windowshopper_mvvm.data.repository.FirebaseAccountRepo
import com.example.windowshopper_mvvm.data.Resource
import com.example.windowshopper_mvvm.data.Status
import com.example.windowshopper_mvvm.models.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val _firebase: FirebaseAccountRepo): ViewModel() {

    private val _username = MutableLiveData<Resource<String>>()
    val username: LiveData<Resource<String>> = _username

    private val _email = MutableLiveData<Resource<String>>()
    val email: LiveData<Resource<String>> = _email

    private val _wasEmailUpdated = MutableLiveData<Boolean>()
    val wasEmailUpdated : LiveData<Boolean> = _wasEmailUpdated

    private lateinit var auth: FirebaseAuth

    @ExperimentalCoroutinesApi
    fun getUsersData(uid : String){
        viewModelScope.launch {
            val user: Unit = _firebase.getUsersData(uid).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        val account: Account = it.data!!
                        _username.postValue(Resource.success(account.username))
                        _email.postValue(Resource.success(account.email))
                    }
                }
            }
        }
    }

    fun updateEmailAddress(uid: String, email: String){
        viewModelScope.launch {
            _firebase.updateEmailAddress(uid, email)
            _wasEmailUpdated.postValue(true)
        }
    }

    fun signOut(){
        auth = Firebase.auth
        Firebase.auth.signOut()
    }

}