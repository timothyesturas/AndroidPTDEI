package com.it.ptdei.menu.returns.features.returnItems

import java.math.BigDecimal


data class ForReturnRequest(
    val trackingNo: String,
    val rtvNo: String,
    val qty: BigDecimal?,
    val prodName: String,
    val items: List<ForReturnItem>,
    val attachmentPath: List<String>
)
