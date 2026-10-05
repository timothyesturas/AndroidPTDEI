package com.it.ptdei.menu.salesapproval

import com.it.ptdei.menu.salesapproval.features.getOrders.GetProductResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SalesApprovalApi {

    @GET("GetOrdersServlet")
    suspend fun getOrders(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("search") search: String
    ): GetOrdersResponse

    @POST("GetProductsByOrderNoServlet")
    suspend fun getProductsByOrderNo(
        @Query("orderNo") orderNo: String
    ): GetProductResponse

}