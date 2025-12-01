package com.example.finsur.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finsur.domain.common.Result
import com.example.finsur.domain.orders.models.Order
import com.example.finsur.domain.orders.usecases.CancelOrderUseCase
import com.example.finsur.domain.orders.usecases.GetMyOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getMyOrdersUseCase: GetMyOrdersUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase
) : ViewModel() {

    private val _ordersState = MutableStateFlow<OrdersState>(OrdersState.Initial)
    val ordersState: StateFlow<OrdersState> = _ordersState.asStateFlow()

    private val _cancelState = MutableStateFlow<CancelState>(CancelState.Initial)
    val cancelState: StateFlow<CancelState> = _cancelState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders(page: Int = 1, limit: Int = 10) {
        viewModelScope.launch {
            _ordersState.value = OrdersState.Loading
            when (val result = getMyOrdersUseCase(page, limit)) {
                is Result.Success -> {
                    _ordersState.value = OrdersState.Success(
                        orders = result.data.orders,
                        total = result.data.total,
                        page = result.data.page,
                        pages = result.data.pages
                    )
                }
                is Result.Error -> {
                    _ordersState.value = OrdersState.Error(
                        result.message ?: "Error al cargar pedidos"
                    )
                }
            }
        }
    }

    fun cancelOrder(orderId: Int) {
        viewModelScope.launch {
            _cancelState.value = CancelState.Loading
            when (val result = cancelOrderUseCase(orderId)) {
                is Result.Success -> {
                    _cancelState.value = CancelState.Success("Pedido cancelado exitosamente")
                    loadOrders() // Reload orders
                }
                is Result.Error -> {
                    _cancelState.value = CancelState.Error(
                        result.message ?: "Error al cancelar pedido"
                    )
                }
            }
        }
    }

    fun clearCancelState() {
        _cancelState.value = CancelState.Initial
    }

    fun retry() {
        loadOrders()
    }
}

sealed class OrdersState {
    object Initial : OrdersState()
    object Loading : OrdersState()
    data class Success(
        val orders: List<Order>,
        val total: Int,
        val page: Int,
        val pages: Int
    ) : OrdersState()
    data class Error(val message: String) : OrdersState()
}

sealed class CancelState {
    object Initial : CancelState()
    object Loading : CancelState()
    data class Success(val message: String) : CancelState()
    data class Error(val message: String) : CancelState()
}
