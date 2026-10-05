package com.it.ptdei.menu.returns

import com.it.ptdei.menu.returns.features.getItems.ScanTrackNoRequest
import com.it.ptdei.menu.returns.features.getItems.ScanTrackNoResponse
import com.it.ptdei.menu.returns.features.getReport.GetReportResponse
import com.it.ptdei.menu.returns.features.returnItems.AttachmentItem
import com.it.ptdei.menu.returns.features.returnItems.ForReturnResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ReturnsApi {
//    @POST("SaveToReturnReverseImageServlet")
//    suspend fun saveToReturnReverseImage(@Part("poNo") poNo: String,
//                                         @Part("media") media: List<MultipartBody.Part?>): Boolean

    @Multipart
    @POST("SaveReturnsMonitoringServlet")
    suspend fun saveReturnsMonitoring(
        @Part("request") request: ReturnsMonitoring,
        @Part medias: List<MultipartBody.Part>
    ): Response<Boolean>


    @Multipart
    @POST("CreateSretByRtvServlet")
    suspend fun createSretByRtvServlet(
        @Part("request") request: RequestBody,
        @Part file: MultipartBody.Part?,
        @Part medias: List<MultipartBody.Part>?
    ): ForReturnResponse


    @GET("GetCourierServlet")
    suspend fun getCourier(@Query("custCd") customer: String): Map<String, String>

    @GET("GetCustomersServlet")
    suspend fun getCustomers(): Map<String, String>

    @POST("GetReportServlet")
    suspend fun getReport(@Body request: String): Response<GetReportResponse>?

    @POST("SaveToPrintedReturnsServlet")
    suspend fun saveToPrintedReturns(
        @Body request: SaveToPrintedReturnsDataModel
    ): Response<String>

    @GET("GetUserIpServlet")
    suspend fun getUserIp(): List<UserIpDataModel>

    @POST("GetItemsByTrackingNoServlet")
    suspend fun getItemsByTrackingNo(
        @Body request: ScanTrackNoRequest
    ): ScanTrackNoResponse


    @POST("GetItemsByOrderIdServlet")
    suspend fun getItemsByOrderId(
        @Body request: ScanTrackNoRequest
    ): ScanTrackNoResponse



    @GET("GetImageListServlet")
    suspend fun getImageList(
        @Query("po_no") poNo: String
    ): List<AttachmentItem>

}