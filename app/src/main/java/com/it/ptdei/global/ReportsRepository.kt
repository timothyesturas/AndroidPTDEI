package com.it.ptdei.global

import android.util.Log
import okhttp3.ResponseBody

class ReportsRepository(
    private val api: ReportsApi,
) {

    suspend fun generateReport(request: ReportRequest): ResponseBody {
        Log.d("ReportVM", "In ReportsRepository generateReport")
        return api.generateReport(request)
    }

}