package com.it.ptdei.menu.salesapproval

import android.util.Log
import com.it.ptdei.menu.salesapproval.features.getOrders.GetProductResponse

class SalesApprovalRepository(
    private val api: SalesApprovalApi
) {

    suspend fun getOrders(
        page: Int,
        pageSize: Int,
        search: String
    ): GetOrdersResponse {

        return api.getOrders(
            page,
            pageSize,
            search

        )
    }

    suspend fun getProductsByOrderNo(orderNo: String): GetProductResponse {
        Log.d("repo", "getProductsByOrderNo")
        return api.getProductsByOrderNo(orderNo)
    }
}