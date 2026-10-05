package com.it.ptdei.global

data class ReportRequest(
    val reportId: String,
    val userId: String,
    val busUnit: String,
    val userIp: String,
    val userPort: String,
    val parameters: ArrayList<String> = ArrayList()
)