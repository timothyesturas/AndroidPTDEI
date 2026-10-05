package com.it.ptdei.menu.returns.features.printItems

data class DataToPrint(
    val dateToday: String,
    val user: String,
    val trackingNo: String,
    val rtvNo: String,
    val sretNo: String,
    val prodName: String,
    val qty: Int
)