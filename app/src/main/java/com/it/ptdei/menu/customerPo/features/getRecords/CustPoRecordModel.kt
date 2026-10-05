package com.it.ptdei.menu.customerPo.features.getRecords

import com.it.ptdei.menu.customerPo.features.getEmpList.EmployeeModel

data class CustPoRecordModel(
    val cpNo: String?,
    val orderDate: String?,

    val busUnit: String?,
    val busName: String?,

    val rcvFrom: EmployeeModel?,

    val checkedBy: EmployeeModel?,

    val packedBy: EmployeeModel?,

    val assistedBy: EmployeeModel?,

    val itemType: String?,
    val custCd: String?,
    val poNo: String?,
    val custName: String?
)
