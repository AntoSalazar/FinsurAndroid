package com.example.finsur.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finsur.domain.common.Result
import com.example.finsur.domain.orders.models.Order
import com.example.finsur.domain.orders.usecases.GetOrderByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val getOrderByIdUseCase: GetOrderByIdUseCase
) : ViewModel() {

    private val _orderState = MutableStateFlow<OrderDetailState>(OrderDetailState.Initial)
    val orderState: StateFlow<OrderDetailState> = _orderState.asStateFlow()

    fun loadOrder(orderId: Int) {
        viewModelScope.launch {
            _orderState.value = OrderDetailState.Loading
            when (val result = getOrderByIdUseCase(orderId)) {
                is Result.Success -> {
                    _orderState.value = OrderDetailState.Success(result.data)
                }
                is Result.Error -> {
                    _orderState.value = OrderDetailState.Error(
                        result.message ?: "Error al cargar el pedido"
                    )
                }
            }
        }
    }
}

sealed class OrderDetailState {
    object Initial : OrderDetailState()
    object Loading : OrderDetailState()
    data class Success(val order: Order) : OrderDetailState()
    data class Error(val message: String) : OrderDetailState()
}
