package com.example.hrrestaurant.ui.fragment.order

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.hrrestaurant.data.dataSources.local.Meal
import com.example.hrrestaurant.data.dataSources.local.Order
import com.example.hrrestaurant.domain.CreateFireStoreOrderUseCase
import com.example.hrrestaurant.domain.CreateOrderUseCase
import com.example.hrrestaurant.domain.GetCartItemsUseCase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val createOrderUseCase: CreateOrderUseCase,
    private val createFireStoreOrderUseCase: CreateFireStoreOrderUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,

    ) : ViewModel() {

    private var registration: ListenerRegistration? = null
    val cartItems = getCartItemsUseCase().asLiveData()

    fun createNewOrder(
        cartItems: List<Meal?>,
        fireStoreDb: FirebaseFirestore,
        currentUserId: String,
        orderLocation: String,
        userName: String,
        userPrimaryPhone: String,
        userSecondaryPhone: String,
        context:Context
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val order = createOrderUseCase(
                    cartItems,
                    currentUserId,
                    orderLocation,
                    userPrimaryPhone,
                    userName,
                    userSecondaryPhone
                )
                Log.d("Firebase", "order Created = $order ....")
                addNewOrderToFireStore(order, fireStoreDb , context)
            }
        }
    }

    private fun addNewOrderToFireStore(
        order: Order,
        fireStoreDb: FirebaseFirestore,
        context:Context
    ) {
        createFireStoreOrderUseCase(order, fireStoreDb ,context)
        Log.d("Firebase", "order created")
        registration = createFireStoreOrderUseCase.registration
    }


    override fun onCleared() {
        if (registration != null) registration!!.remove()
        super.onCleared()
    }

}