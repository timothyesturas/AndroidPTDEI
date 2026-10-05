package com.it.ptdei.menu.returns.features.returnItems


data class ForReturnResponse(
    val sretNo: String,
    val rtvNo: String,
    val trackingNo: String,
    val custName: String,
    val branchName: String,
    val returnedItems: List<ReturnedItem>?,
)
