package com.example.windowshopper_mvvm.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.windowshopper_mvvm.data.repository.FirebaseAccountRepoImpl
import com.example.windowshopper_mvvm.models.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val _firebaseImpl: FirebaseAccountRepoImpl): ViewModel() {

    private lateinit var auth: FirebaseAuth

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn : LiveData<Boolean> = _isUserLoggedIn

    private val _wasNewUserCreated = MutableLiveData<Boolean>()
    val wasNewUserCreated : LiveData<Boolean> = _wasNewUserCreated

    private val _username = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _confirmPassword = MutableStateFlow("")

    fun setUsername(username: String) {
        _username.value = username
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setConfirmPassword(password: String) {
        _confirmPassword.value = password
    }

    val isLoginEnabled: Flow<Boolean> = combine(_email, _password) { email, password ->
        val isEmailCorrect = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordCorrect = password.length > 6
        return@combine isEmailCorrect and isPasswordCorrect
    }

    val isSignUpEnabled: Flow<Boolean> = combine(_username, _email, _password, _confirmPassword) { username, email, password, confirmPassword ->
        val isUsernameEntered = username.isNotEmpty()
        val isEmailCorrect = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordCorrect = password.length > 6
        val doPasswordsMatch = password == confirmPassword
        return@combine isUsernameEntered and isEmailCorrect and isPasswordCorrect and doPasswordsMatch
    }

    fun signIn(email: String, password: String){
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                _isUserLoggedIn.postValue(true)
            } else {
                _isUserLoggedIn.postValue(false)
            }
        }
    }

    fun signUpNewUser(username: String, email: String, password: String){
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = auth.currentUser
                val account = Account(username, email, password)

                _firebaseImpl.insertUserAccount(account, user.uid)

                _wasNewUserCreated.postValue(true)
            } else {
                _wasNewUserCreated.postValue(false)
            }
        }
    }

}