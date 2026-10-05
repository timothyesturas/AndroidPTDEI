package com.it.ptdei.menu.returns

data class SaveToPrintedReturnsDataModel(
    val fromdate: String,
    val toDate: String,
    val custCd: String,
    val custBr: String,
    val printedBy: String,
    val returnType: String
)