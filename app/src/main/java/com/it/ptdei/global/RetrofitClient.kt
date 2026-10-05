package com.it.ptdei.global

import com.it.ptdei.features.login.AuthApi
import com.it.ptdei.menu.customerPo.CustPoApi
import com.it.ptdei.menu.returns.ReturnsApi
import com.it.ptdei.menu.salesapproval.SalesApprovalApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // JASPER REPORTS PREVIEWER API
    fun createGeneralJasperReportsPreviewerApi(
        ip: String,
        okHttpClient: OkHttpClient
    ): ReportsApi {

        return Retrofit.Builder()
            .baseUrl("http://$ip:9070/GeneralJasperReportsPreviewer/preview/")
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ReportsApi::class.java)
    }

    // AUTH API
    fun createAuthApi(
        baseUrl: String,
        okHttpClient: OkHttpClient
    ): AuthApi {

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(AuthApi::class.java)
    }

    // RETURNS API
    fun createCustPoApi(
        baseUrl: String,
        okHttpClient: OkHttpClient
    ): CustPoApi {

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(CustPoApi::class.java)
    }

    // RETURNS API
    fun createReturnsApi(
        baseUrl: String,
        okHttpClient: OkHttpClient
    ): ReturnsApi {

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ReturnsApi::class.java)
    }

    // SALES APPROVAL API
    fun createSalesApprovalApi(
        baseUrl: String,
        okHttpClient: OkHttpClient
    ): SalesApprovalApi {

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(SalesApprovalApi::class.java)
    }

}