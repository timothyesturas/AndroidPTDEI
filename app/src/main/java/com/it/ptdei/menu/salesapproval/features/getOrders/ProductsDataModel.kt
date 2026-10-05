package com.it.ptdei.menu.salesapproval.features.getOrders

import java.math.BigDecimal

data class ProductsDataModel(
    val orderNo: String = "",
    val prodCd: String = "",
    val prodName: String = "",
    val qty: Int = 0,
    val sku: String = "",

    val disc: BigDecimal? = null,
    val unitCd: String = "",
    val unitPrice: BigDecimal? = null,
    val priceUnitCd: String = "",
    val discPrice: BigDecimal = BigDecimal.ZERO,

    val itemNo: Int = 0,
    val custCd: String = "",
    val custBr: String = "",

    val effectiveDate: String? = "",

    // Current pricing
    val currentUnitPrice: BigDecimal? = null,
    val currentPriceUnitCd: String = "",
    val currentDisc: String = "",

    // Conversion quantities
    val unitEqQty: BigDecimal = BigDecimal.ZERO,
    val uopEqQty: BigDecimal = BigDecimal.ZERO
)