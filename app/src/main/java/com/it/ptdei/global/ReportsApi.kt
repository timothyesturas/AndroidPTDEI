package com.it.ptdei.global

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportsApi {

    @POST("GeneralJasperReportsPreviewerServlet")
    suspend fun generateReport(
        @Body request: ReportRequest,
    ): ResponseBody

}