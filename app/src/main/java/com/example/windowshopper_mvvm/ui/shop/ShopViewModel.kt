package com.example.windowshopper_mvvm.ui.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.windowshopper_mvvm.data.repository.FirebaseShopRepoImpl
import com.example.windowshopper_mvvm.data.Resource
import com.example.windowshopper_mvvm.data.Status
import com.example.windowshopper_mvvm.models.CartItem
import com.example.windowshopper_mvvm.models.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(private val _firebaseImpl: FirebaseShopRepoImpl): ViewModel() {

    private val _clothesList = MutableLiveData<Resource<List<Item>>>()
    val clothesList: LiveData<Resource<List<Item>>> = _clothesList

    @ExperimentalCoroutinesApi
    suspend fun getClothes(){
        _firebaseImpl.getClothes().collect {
            _clothesList.postValue(Resource.loading(null))
            when(it.status) {
                Status.SUCCESS -> {
                    it.data?.let { items ->
                        _clothesList.postValue(Resource.success(items))
                    }
                }
                Status.ERROR -> { _clothesList.postValue(Resource.error(it.message.toString(), emptyList())) }
            }
        }
    }

    fun addToCart(cartItem: CartItem, uid: String) {
        viewModelScope.launch {
            _firebaseImpl.addToCart(cartItem, uid)
        }
    }

}

