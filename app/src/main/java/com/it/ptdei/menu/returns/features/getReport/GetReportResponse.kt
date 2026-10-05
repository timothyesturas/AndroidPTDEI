package com.it.ptdei.menu.returns.features.getReport

data class GetReportResponse(
    val custName: String?,
    val courier: String?,
    val createdBy: String?,
    val cancelType: String?,
    val recordCount: String?
)
