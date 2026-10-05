package com.it.ptdei.menu.customerPo

import com.it.ptdei.menu.customerPo.features.getEmpList.EmployeeModel
import com.it.ptdei.menu.customerPo.features.getRecords.CustPoRecordModel
import com.it.ptdei.menu.customerPo.features.saveRecords.UpdateCustomerPoRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface CustPoApi {

    @POST("GetRecordsByCpNoServlet")
    suspend fun getRecordsByPoNo(
        @Body request: String
    ): List<CustPoRecordModel>

    @GET("GetEmployeesServlet")
    suspend fun getEmpList(): List<EmployeeModel>

    @PUT("UpdateCustomerPoServlet")
    suspend fun updateCustomerPo(
        @Body request: UpdateCustomerPoRequest
    ): Response<Unit>
}