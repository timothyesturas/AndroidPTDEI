package com.it.ptdei.menu.returns

data class ReturnsMonitoring(
    var trackingNo: String? = null,
    var orderId: String? = null,
    var orderId1: String? = null,
    var orderId2: String? = null,
    var custCd: String? = null,
    var custBr: String? = null,
    var itemDescAndStatus: List<ItemDescription>,
    var medias: List<String>? = null
)
