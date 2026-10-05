package com.it.ptdei.menu.salesapproval

data class GetOrdersResponse(
    val orders: List<OrdersDataModel>,
    val currentPage: Int,
    val totalPages: Int,
    val totalRecords: Int
)
