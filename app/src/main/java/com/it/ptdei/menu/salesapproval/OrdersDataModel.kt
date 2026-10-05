package com.it.ptdei.menu.salesapproval

data class OrdersDataModel(
    val custName: String = "",
    val branchName: String = "",
    val orderNo: String = "",
    val orderDate: String? = null,
    val poNo: String = "",
    val delDate: String? = null,
    val cancelDate: String? = null,
    val imgFiles: List<String> = emptyList()
)
