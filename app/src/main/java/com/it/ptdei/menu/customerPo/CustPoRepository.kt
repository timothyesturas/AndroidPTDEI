package com.it.ptdei.menu.customerPo

import android.util.Log
import com.it.ptdei.menu.customerPo.features.getEmpList.EmployeeModel
import com.it.ptdei.menu.customerPo.features.getRecords.CustPoRecordModel
import com.it.ptdei.menu.customerPo.features.saveRecords.UpdateCustomerPoRequest

class CustPoRepository(
    private val api: CustPoApi,
) {

    suspend fun getRecordsByPoNo(
        poNo: String,
    ): List<CustPoRecordModel> {
        Log.d("CustPoRepository getRecordsByPoNo", "Fetching items for: $poNo")
        return api.getRecordsByPoNo(poNo)
    }

    suspend fun getEmpList(): List<EmployeeModel> {
        val start = System.currentTimeMillis()

        Log.d(
            "CustPoRepository", "Fetching employees..."
        )

        val result = api.getEmpList()

        Log.d(
            "CustPoRepository", "Fetched ${result.size} employees in ${
                System.currentTimeMillis() - start
            } ms"
        )

        return result
    }


    suspend fun updateCustomerPo(
        request: UpdateCustomerPoRequest,
    ) {

        val response = api.updateCustomerPo(request)

        if (!response.isSuccessful) {
            throw Exception(
                "Update failed: HTTP ${response.code()} ${response.message()}"
            )
        }
    }
}