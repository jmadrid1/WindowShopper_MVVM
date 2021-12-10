package com.example.windowshopper_mvvm.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.windowshopper_mvvm.data.repository.FirebaseCartRepo
import com.example.windowshopper_mvvm.data.Resource
import com.example.windowshopper_mvvm.data.Status
import com.example.windowshopper_mvvm.models.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val _firebase: FirebaseCartRepo): ViewModel() {

    private val _cartList = MutableLiveData<Resource<List<CartItem>>>()
    val cartList: LiveData<Resource<List<CartItem>>> = _cartList

    val numOfCartItems = MutableLiveData<Int>()
    val totalAmountOfCart = MutableLiveData<Double>()

    @ExperimentalCoroutinesApi
    suspend fun getCartItems(uid: String){
        _firebase.getCartItems(uid).collect {
            val queryList = mutableListOf<CartItem>()
            _cartList.postValue(Resource.loading(null))
            when(it.status) {
                Status.SUCCESS -> {
                    it.data?.let { items ->
                        var totalPrice = 0.00
                        var amount = 0
                        for(e in items){
                            totalPrice += e!!.price * e.quantity
                            amount += e.quantity
                            queryList.add(e)
                        }
                        _cartList.postValue(Resource.success(queryList))
                        numOfCartItems.postValue(amount)
                        totalAmountOfCart.postValue(String.format("%.2f", totalPrice).toDouble())
                    }
                }
                Status.ERROR -> {  _cartList.postValue(Resource.error("Failed to grab items from Firebase", emptyList())) }
            }
        }
    }

    suspend fun removeItemFromList(uid: String, itemID: String, position: Int){
        viewModelScope.launch {
            _firebase.removeItemFromList(uid, itemID, position)
        }
    }

}