package com.it.ptdei.menu.salesapproval

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it.ptdei.menu.salesapproval.features.getOrders.ApprovalKey
import com.it.ptdei.menu.salesapproval.features.getOrders.GetProductResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException


class SalesApprovalViewModel(
    private val repo: SalesApprovalRepository
) : ViewModel() {


    sealed class OrdersState {
        object Idle : OrdersState()
        object Loading : OrdersState()
        object Success : OrdersState()
        object SessionExpired : OrdersState()

        data class Error(
            val message: String
        ) : OrdersState()
    }

    private val _orderState =
        MutableStateFlow<OrdersState>(
            OrdersState.Idle
        )

    val orderState: StateFlow<OrdersState>
            = _orderState

    private val _orders =
        MutableStateFlow(
            GetOrdersResponse(
                orders = emptyList(),
                currentPage = 1,
                totalPages = 1,
                totalRecords = 0
            )
        )

    val orders: StateFlow<GetOrdersResponse> = _orders





    sealed class ProductsState {
        object Idle : ProductsState()
        object Loading : ProductsState()
        object Success : ProductsState()
        object SessionExpired : ProductsState()

        data class Error(
            val message: String
        ) : ProductsState()
    }

    private val _productState =
        MutableStateFlow<ProductsState>(
            ProductsState.Idle
        )

    private val _products =
        MutableStateFlow(
            GetProductResponse(emptyList())
        )

    val products: StateFlow<GetProductResponse> = _products
    
    
    
    

    private val _currentPage =
        MutableStateFlow(1)

    val currentPage: StateFlow<Int>
            = _currentPage

    private val _totalPages =
        MutableStateFlow(1)

    val totalPages: StateFlow<Int>
            = _totalPages

    private val pageSize = 20

    private val _searchText =
        MutableStateFlow("")

    val searchText: StateFlow<String>
            = _searchText



    fun onSearchTextChanged(
        text: String
    ) {
        _searchText.value = text
    }

    fun getOrders(
        page: Int = 1
    ) {

        viewModelScope.launch {

            _orderState.value =
                OrdersState.Loading

            try {

                val response =
                    repo.getOrders(
                        page = page,
                        pageSize = pageSize,
                        search = _searchText.value
                    )


                _orders.value =
                    response

                _currentPage.value =
                    response.currentPage

                _totalPages.value =
                    response.totalPages

                _orderState.value =
                    OrdersState.Success

            } catch (e: HttpException) {
                if (e.code() == 401) {
                    _orderState.value = OrdersState.SessionExpired
                } else {
                    _orderState.value =
                        OrdersState.Error(
                            e.message ?: "Unknown Error"
                        )
                }
            }
        }
    }

    fun getProductsByOrderNo(orderNo: String){

        Log.d("viewmodel", "getProductsByOrderNo")

        viewModelScope.launch {

            _productState.value = ProductsState.Loading

            try {
                val response = repo.getProductsByOrderNo(orderNo)

                _products.value = response

                _productState.value = ProductsState.Success
            } catch (e: HttpException){
                if(e.code() == 401){
                    _productState.value = ProductsState.SessionExpired
                } else {
                    _productState.value = ProductsState.Error(e.message ?: "Unknown Error")
                }
            }
        }
    }

    fun nextPage() {

        getOrders(
            page = _currentPage.value + 1
        )
    }

    fun previousPage() {

        if (_currentPage.value > 1) {

            getOrders(
                page = _currentPage.value - 1
            )
        }
    }























    enum class ApprovalState {
        APPROVED,
        DISAPPROVED
    }

    private val _pendingProductApprovals = MutableStateFlow<Map<ApprovalKey, ApprovalState>>(emptyMap())
    val pendingProductApprovals = _pendingProductApprovals.asStateFlow()

    fun setProductApproval(orderNo: String, itemNo: Int, state: ApprovalState){
        _pendingProductApprovals.update { it ->
            it + (ApprovalKey(orderNo, itemNo) to state)
        }
    }

    private val _pendingOrderApprovals = MutableStateFlow<Map<String, ApprovalState>>(emptyMap())
    val pendingOrderApprovals = _pendingOrderApprovals.asStateFlow()

    fun setOrderApproval(orderNo: String, state: ApprovalState) {
        _pendingOrderApprovals.update { current ->
            current + (
                    orderNo to state
                    )
        }
    }
    fun printPendingOrderApprovals() {
        Log.d("APPROVAL_DEBUG", "Pending Approvals:")

        _pendingOrderApprovals.value.forEach { (orderNo, state) ->
            Log.d("APPROVAL_DEBUG", "$orderNo -> $state")
        }
    }
    fun submitOrderApprovals() {
//        val approvals = _pendingOrderApprovals.value

//        val approvedList = approvals
//            .filterValues { it == ApprovalState.APPROVED }
//            .keys

//        val disapprovedList = approvals
//            .filterValues { it == ApprovalState.DISAPPROVED }
//            .keys

        // TODO: call API here
        // repository.submit(approvedList, disapprovedList)
    }





}