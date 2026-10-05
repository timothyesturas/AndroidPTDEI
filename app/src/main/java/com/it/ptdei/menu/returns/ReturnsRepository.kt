package com.it.ptdei.menu.returns

import android.util.Log
import com.google.gson.Gson
import com.it.ptdei.menu.returns.features.getItems.ScanTrackNoRequest
import com.it.ptdei.menu.returns.features.getItems.ScanTrackNoResponse
import com.it.ptdei.menu.returns.features.getReport.GetReportResponse
import com.it.ptdei.menu.returns.features.returnItems.AttachmentItem
import com.it.ptdei.menu.returns.features.returnItems.ForReturnRequest
import com.it.ptdei.menu.returns.features.returnItems.ForReturnResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class ReturnsRepository(
    private val api: ReturnsApi,
) {

//    suspend fun saveToReturnReverseImage(
//        poNo: String,
//        media: List<MultipartBody.Part?>
//    ): Boolean {
//        Log.d("ReturnsRepository saveToReturnReverseImage", "Fetching items for PO_NO: $poNo")
//        return api.saveToReturnReverseImage(poNo, media)
//    }

    suspend fun saveReturnsMonitoring(
        request: ReturnsMonitoring,
        medias: List<MultipartBody.Part>
    ): Boolean {

        Log.d(
            "ReturnsRepository",
            "saveReturnsMonitoring request: $request"
        )

        return try {
            val response = api.saveReturnsMonitoring(request, medias)

            Log.d(
                "ReturnsRepository",
                "HTTP code: ${response.code()}"
            )

            Log.d(
                "ReturnsRepository",
                "isSuccessful: ${response.isSuccessful}"
            )

            Log.d(
                "ReturnsRepository",
                "body: ${response.body()}"
            )

            Log.d(
                "ReturnsRepository",
                "errorBody: ${response.errorBody()?.string()}"
            )

            if (response.isSuccessful) {
                response.body() ?: false
            } else {
                false
            }

        } catch (e: Exception) {
            Log.e(
                "ReturnsRepository",
                "Error saving returns monitoring",
                e
            )
            false
        }
    }

    suspend fun getCourier(custCd: String): Map<String, String> {
        val result = api.getCourier(custCd)
        return result
    }

    suspend fun getCustomers(): Map<String, String> {
        Log.d("Inside ReturnsRepository", "getCustomers")
        val result = api.getCustomers()
        return result
    }


    suspend fun getReport(
        printRef: String,
    ): GetReportResponse? {
        return try {
            Log.d("ReturnsRepository", "Inside getReport")
            val response = api.getReport(printRef)


            if (response?.isSuccessful == true) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveToPrintedReturns(
        request: SaveToPrintedReturnsDataModel,
    ): String? {
        Log.d(
            "ReturnsRepository", "saveToPrintedReturns request: $request"
        )

        return try {
            val response = api.saveToPrintedReturns(request)

            Log.d(
                "ReturnsRepository", "HTTP code: ${response.code()}"
            )

            Log.d(
                "ReturnsRepository", "isSuccessful: ${response.isSuccessful}"
            )

            Log.d(
                "ReturnsRepository", "body: ${response.body()}"
            )

            Log.d(
                "ReturnsRepository", "errorBody: ${response.errorBody()?.string()}"
            )

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }

        } catch (e: Exception) {
            Log.e(
                "ReturnsRepository", "Error saving printed returns", e
            )
            null
        }
    }

    suspend fun getUserIp(): List<UserIpDataModel> {
        Log.d("ReturnsRepository getUserIp", "Fetching user ip.")
        return api.getUserIp()
    }

    suspend fun getItemsByTrackingNo(
        request: ScanTrackNoRequest,
    ): ScanTrackNoResponse {
        Log.d("ReturnsRepository getRecordsByPoNo", "Fetching items for: $request")
        return api.getItemsByTrackingNo(request)
    }

    suspend fun getItemsByOrderId(
        request: ScanTrackNoRequest,
    ): ScanTrackNoResponse {
        Log.d("ReturnsRepository getItemsByOrderId", "Fetching items for: $request")
        return api.getItemsByOrderId(request)
    }


    suspend fun createSretByRtvServlet(
        request: ForReturnRequest,
        file: MultipartBody.Part?,
        medias: List<MultipartBody.Part>?
    ): ForReturnResponse {

        Log.d(
            "ReturnsRepository createSretByRtvServlet", "Generating SRET for: $request"
        )

        val gson = Gson()

        val json = gson.toJson(request)

        val requestBody: RequestBody = json.toRequestBody(
            "application/json".toMediaType()
        )

        return api.createSretByRtvServlet(
            requestBody, file, medias
        )
    }


    suspend fun getImageList(
        poNo: String,
    ): List<AttachmentItem> {

        Log.d(
            "ReturnsRepository getImageList", "Fetching images for: $poNo"
        )

        return api.getImageList(poNo)
    }

}