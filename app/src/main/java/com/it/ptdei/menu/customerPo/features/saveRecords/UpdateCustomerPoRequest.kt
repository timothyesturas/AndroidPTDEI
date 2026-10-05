package com.it.ptdei.menu.customerPo.features.saveRecords

data class UpdateCustomerPoRequest(
    val cpNo: String?,
    val checkedBy: String?,
    val packedBy: String?,
    val assistedBy: String?,
    val items: List<UpdateCustomerPoItem>
)

data class UpdateCustomerPoItem(
    val poNo: String?,
    val itemType: String?
)