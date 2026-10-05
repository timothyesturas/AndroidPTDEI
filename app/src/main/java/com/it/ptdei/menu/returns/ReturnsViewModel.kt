package com.it.ptdei.menu.returns

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it.ptdei.global.ApiConfig
import com.it.ptdei.global.ReportRequest
import com.it.ptdei.global.ReportsRepository
import com.it.ptdei.global.RetrofitClient
import com.it.ptdei.global.SessionManager
import com.it.ptdei.menu.returns.features.getItems.ScanTrackNoRequest
import com.it.ptdei.menu.returns.features.getItems.ScanTrackNoResponse
import com.it.ptdei.menu.returns.features.getReport.GetReportResponse
import com.it.ptdei.menu.returns.features.returnItems.AttachmentItem
import com.it.ptdei.menu.returns.features.returnItems.ForReturnItem
import com.it.ptdei.menu.returns.features.returnItems.ForReturnRequest
import com.it.ptdei.menu.returns.features.returnItems.ForReturnResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.HttpException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReturnsViewModel(private val okHttpClient: OkHttpClient) : ViewModel() {

    val returnsApi = RetrofitClient.createReturnsApi(
        baseUrl = ApiConfig.BASE_URL, okHttpClient = okHttpClient
    )

    private var returnsRepo = ReturnsRepository(returnsApi)


    val rtvNo: String
        get() = _scannedItems.value.rtvItemModelList.firstOrNull()?.rtvNo ?: "Returned"


    val cancelType: String
        get() = _scannedItems.value.rtvItemModelList.firstOrNull()?.cancelType ?: ""

    private var selectedCustCd: String? = null
    private var selectedCustBr: String? = null

    val custName: String
        get() = _scannedItems.value.rtvItemModelList.firstOrNull()?.custName ?: ""

    val branchName: String
        get() = _scannedItems.value.rtvItemModelList.firstOrNull()?.branchName ?: ""

    val custCd: String
        get() = _scannedItems.value.rtvItemModelList.firstOrNull()?.custCd ?: ""

    val custBr: String
        get() = _scannedItems.value.rtvItemModelList.firstOrNull()?.custBr ?: ""

    private val _lastScannedTrackNo = MutableStateFlow("")
    val lastScannedTrackNo: StateFlow<String> = _lastScannedTrackNo


    private val _generatedResponses = MutableStateFlow<List<ForReturnResponse>>(emptyList())

//    val generatedResponses: StateFlow<List<ForReturnResponse>> =
//        _generatedResponses


    private val _waybillCount = MutableStateFlow(0)

    val waybillCount: StateFlow<Int> = _waybillCount

    private val _report = MutableStateFlow<GetReportResponse?>(null)

    val report: StateFlow<GetReportResponse?> = _report.asStateFlow()

    private val _printRefNo = MutableStateFlow<String?>("")



    private val _couriers = MutableStateFlow<Map<String, String>>(emptyMap())
    val couriers: StateFlow<Map<String, String>> = _couriers
    fun getCourier(custCd: String){
        Log.d("Inside ReturnsViewModel", "getCourier")
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    returnsRepo.getCourier(custCd)
                }
                _couriers.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private val _customers  = MutableStateFlow<Map<String, String>>(emptyMap())
    val customers : StateFlow<Map<String, String>> = _customers
    fun getCustomers() {
        Log.d("Inside ReturnsViewModel", "getCustomers")
        if (_customers.value.isNotEmpty()) return
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    returnsRepo.getCustomers()
                }
                _customers.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    suspend fun getReport(
        printRef: String,
    ) {
        _report.value = returnsRepo.getReport(printRef)
    }

    suspend fun saveReturnsMonitoring(
        trackingNo: String,
        orderId: String,
        orderId1: String,
        orderId2: String,
        custCd: String?,
        custBr: String?,
        itemDescAndStatus: List<ItemDescription>,
        mediaNames: List<String>,
        medias: List<MultipartBody.Part>,
    ): Boolean {

        return try {

            val request = ReturnsMonitoring(
                trackingNo = trackingNo,
                orderId = orderId,
                orderId1 = orderId1,
                orderId2 = orderId2,
                custCd = custCd,
                custBr = custBr,
                itemDescAndStatus = itemDescAndStatus,
                medias = mediaNames
            )

            returnsRepo.saveReturnsMonitoring(
                request = request,
                medias = medias
            )

        } catch (e: Exception) {

            Log.e(
                "saveReturnsMonitoring",
                "Error saving returns monitoring",
                e
            )

            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveToPrintedReturns(
        fromDate: String,
        toDate: String,
        custCd: String,
        custBr: String,
        printedBy: String,
        returnType: String,
    ): String? {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

            val request = SaveToPrintedReturnsDataModel(
                LocalDate.parse(fromDate, inputFormatter).toString(),
                LocalDate.parse(toDate, inputFormatter).toString(),
                custCd,
                custBr,
                printedBy,
                returnType
            )


            val result = returnsRepo.saveToPrintedReturns(request)

            // Store the result
            _printRefNo.value = result

            reportStatusText.value = "Print Ref No: $result"
            // Return the result as well
            result

        } catch (e: Exception) {
            Log.e(
                "saveToPrintedReturns", "Error saving printed returns", e
            )

            null
        }
    }


    private val apiCache = mutableMapOf<String, ReportsRepository>()

    private fun getRepo(ip: String, okHttpClient: OkHttpClient): ReportsRepository {
        Log.d("ReportVM", "getRepo called for IP: $ip")
        return apiCache.getOrPut(ip) {
            Log.d("ReportVM", "Creating NEW repository for IP: $ip")
            val api = RetrofitClient.createGeneralJasperReportsPreviewerApi(
                ip = ip, okHttpClient = okHttpClient
            )
            ReportsRepository(api)
        }
    }

    var reportStatusText = mutableStateOf("Generating report...")
        private set

    suspend fun generateReport(
        selectedPc: UserIpDataModel,
        sessionManager: SessionManager,
        printRefNo: String,
    ) {
        try {
            Log.d("ReportVM", "Selected IP: ${selectedPc.userIp}")

            val reportsRepo = getRepo(
                selectedPc.userIp, okHttpClient
            )

            val params = arrayListOf(
                sessionManager.getUsername(), printRefNo
            )
            Log.d("ReportVM", "params: $params")

            val request = ReportRequest(
                reportId = "RPHQ2026071000002",
                userId = sessionManager.getUsername(),
                busUnit = sessionManager.getBusUnit(),
                userIp = selectedPc.userIp,
                userPort = selectedPc.userPort,
                parameters = params
            )

            Log.d(
                "ReportVM", "Request payload: $request"
            )

            val response = reportsRepo.generateReport(request)

            val responseString = response.string()

            Log.d(
                "ReportVM", "API response body and print ref: $responseString | $printRefNo"
            )


            if (responseString.contains("\"success\":true")) {
                reportStatusText.value = "Report generated"
            } else {
                reportStatusText.value = "Failed to generate report"
            }

        } catch (e: Exception) {
            Log.e(
                "ReportVM", "Error generating report", e
            )

            reportStatusText.value = "Error generating report: ${
                e.message ?: "Unknown error"
            }"
        }
    }


    fun setLastScannedTrackNo(value: String) {
        _lastScannedTrackNo.value = value
    }

    // =========================================
    // STATE FOR GENERATING SRET
    // =========================================
    var returnState = MutableStateFlow<ReturnState>(ReturnState.Idle)
        private set

    // =========================================
    // STATE FOR SCANNED ITEMS
    // =========================================
    private val _scannedItems = MutableStateFlow(
        ScanTrackNoResponse(
            rtvItemModelList = emptyList()
        )
    )

    val scannedItems: StateFlow<ScanTrackNoResponse> = _scannedItems


    // =========================================
    // GET ITEMS FROM TRACKING NO
    // =========================================
    fun getItemsByTrackingNo(
        trackingNo: String,
    ) {
        Log.d("VIEWMODEL getItemsByTrackingNo", "Fetching items for: $trackingNo")
        viewModelScope.launch {

            returnState.value = ReturnState.Loading

            try {

                val request = ScanTrackNoRequest(
                    trackingNo = trackingNo
                )

                val response = returnsRepo.getItemsByTrackingNo(request)

                val firstItem = response.rtvItemModelList.firstOrNull()


                if (firstItem != null) {

                    // First scan
                    if (selectedCustCd == null && selectedCustBr == null) {

                        selectedCustCd = firstItem.custCd
                        selectedCustBr = firstItem.custBr

                        Log.d("AAA", firstItem.cancelType)

                        Log.d(
                            "CUSTOMER", "Saved customer ${firstItem.custCd}-${firstItem.custBr}"
                        )
                    }
                    // Next scans
                    else if (selectedCustCd != firstItem.custCd || selectedCustBr != firstItem.custBr) {
                        _scannedItems.value = ScanTrackNoResponse(
                            rtvItemModelList = emptyList()
                        )

                        returnState.value = ReturnState.CustomerMismatch

                        ReturnState.Error(
                            "Waybill belongs to a different customer/courier."
                        )

                        return@launch
                    }
                }

                Log.d("VIEWMODEL getRecordsByPoNo", "RAW RESPONSE = ${response.rtvItemModelList}")
                Log.d("VIEWMODEL getRecordsByPoNo", "SIZE = ${response.rtvItemModelList.size}")

                // save scanned items
                _scannedItems.value = ScanTrackNoResponse(
                    rtvItemModelList = response.rtvItemModelList.toList()
                )

                returnState.value = ReturnState.ItemsLoaded

            } catch (e: HttpException) {

                if (e.code() == 401) {
                    returnState.value = ReturnState.SessionExpired
                } else {
                    returnState.value = ReturnState.Error(
                        e.message ?: "Unknown error"
                    )
                }

            } catch (e: Exception) {
                Log.e("VIEWMODEL getRecordsByPoNo", "API ERROR", e)
                returnState.value = ReturnState.Error(
                    e.message ?: "Unknown error"
                )
            }
        }
    }


    fun getItemsByOrderId(
        orderId: String,
    ) {
        Log.d("VIEWMODEL getRecordsByPoNo", "Fetching items for: $orderId")
        viewModelScope.launch {

            returnState.value = ReturnState.Loading

            try {

                val request = ScanTrackNoRequest(
                    trackingNo = orderId
                )

                Log.d("getItemsByOrderId", "before getItemsByOrderId $request")
                val response = returnsRepo.getItemsByOrderId(request)

                val firstItem = response.rtvItemModelList.firstOrNull()


                if (firstItem != null) {

                    // First scan
                    if (selectedCustCd == null && selectedCustBr == null) {

                        selectedCustCd = firstItem.custCd
                        selectedCustBr = firstItem.custBr

                        Log.d("AAA", firstItem.cancelType)

                        Log.d(
                            "CUSTOMER", "Saved customer ${firstItem.custCd}-${firstItem.custBr}"
                        )
                    }
                    // Next scans
                    else if (selectedCustCd != firstItem.custCd || selectedCustBr != firstItem.custBr) {
                        _scannedItems.value = ScanTrackNoResponse(
                            rtvItemModelList = emptyList()
                        )

                        returnState.value = ReturnState.CustomerMismatch

                        ReturnState.Error(
                            "Waybill belongs to a different customer/courier."
                        )

                        return@launch
                    }
                }

                Log.d("VIEWMODEL getRecordsByPoNo", "RAW RESPONSE = ${response.rtvItemModelList}")
                Log.d("VIEWMODEL getRecordsByPoNo", "SIZE = ${response.rtvItemModelList.size}")

                // save scanned items
                _scannedItems.value = ScanTrackNoResponse(
                    rtvItemModelList = response.rtvItemModelList.toList()
                )

                returnState.value = ReturnState.ItemsLoaded

            } catch (e: HttpException) {

                if (e.code() == 401) {
                    returnState.value = ReturnState.SessionExpired
                } else {
                    returnState.value = ReturnState.Error(
                        e.message ?: "Unknown error"
                    )
                }

            } catch (e: Exception) {
                Log.e("VIEWMODEL getRecordsByPoNo", "API ERROR", e)
                returnState.value = ReturnState.Error(
                    e.message ?: "Unknown error"
                )
            }
        }
    }


    fun createSretByRtvServlet(
        trackingNo: String,
        items: List<ForReturnItem>,
        file: MultipartBody.Part?,
        medias: List<MultipartBody.Part>?,
        onResult: (Boolean) -> Unit,
    ) {

        if (items.isEmpty()) {

            returnState.value = ReturnState.Error("No items selected")

            onResult(false)
            return
        }

        viewModelScope.launch {

            returnState.value = ReturnState.Loading

            val firstItem = _scannedItems.value.rtvItemModelList.firstOrNull()

            try {

                val request = ForReturnRequest(
                    trackingNo = trackingNo,
                    rtvNo = firstItem?.rtvNo ?: "",
                    qty = firstItem?.qty,
                    prodName = firstItem?.prodName ?: "",
                    items = items,
                    attachmentPath = getCheckedAttachments().map { it.filePath }
                )


                val response = returnsRepo.createSretByRtvServlet(
                    request, file, medias
                )

                _generatedResponses.value += response

                _waybillCount.value += 1

                returnState.value = ReturnState.Success(response)
                onResult(true)

            } catch (e: Exception) {

                Log.e(
                    "VIEWMODEL createSretByRtvServlet", "API ERROR", e
                )

                returnState.value = ReturnState.Error(
                    e.message ?: "Unknown error"
                )

                onResult(false)
            }
        }
    }

    fun clearGeneratedResponses() {
        _generatedResponses.value = emptyList()
    }


    // =========================================
    // CLEAR SCANNED ITEMS
    // =========================================
    fun clear() {

        selectedCustCd = null
        selectedCustBr = null

        _scannedItems.value = ScanTrackNoResponse(
            rtvItemModelList = emptyList()
        )

        returnState.value = ReturnState.Idle
    }

    // =========================================
    // UI STATES
    // =========================================
    sealed class ReturnState {

        object Idle : ReturnState()

        object Loading : ReturnState()

        object ItemsLoaded : ReturnState()

        object CustomerMismatch : ReturnState()

        object SessionExpired : ReturnState()

        data class Success(
            val response: ForReturnResponse,
        ) : ReturnState()

        data class Error(
            val message: String,
        ) : ReturnState()
    }

//    sealed class UploadImageState {
//        object Processing: UploadImageState()
////        object Idle: UploadImageState()
//
//        data class Success(
//            val response: Boolean,
//        ) : UploadImageState()
//
//        data class Error(
//            val message: String,
//        ) : UploadImageState()
//    }


    private val _attachmentList = MutableStateFlow<List<AttachmentItem>>(emptyList())

    val attachmentList: StateFlow<List<AttachmentItem>> = _attachmentList


    private val _ipList = MutableStateFlow<List<UserIpDataModel>>(emptyList())
    val ipList: StateFlow<List<UserIpDataModel>> = _ipList

    fun loadAttachments(poNo: String) {

        viewModelScope.launch {

            try {

                val response = returnsRepo.getImageList(poNo)

                _attachmentList.value = response.map {

                    it.copy(
                        checked = false
                    )
                }

                Log.d(
                    "ATTACHMENTS", "Loaded: ${response.size}"
                )

            } catch (e: Exception) {

                Log.e(
                    "ATTACHMENTS", "ERROR", e
                )
            }
        }
    }


    fun getUserIp() {

        viewModelScope.launch {

            try {

                val response = returnsRepo.getUserIp()

                _ipList.value = response

            } catch (e: Exception) {

                Log.e(
                    "ATTACHMENTS", "ERROR", e
                )
            }
        }
    }

    fun updateAttachmentChecked(
        url: String,
        checked: Boolean,
    ) {
        _attachmentList.value = _attachmentList.value.map { item ->
            if (item.url == url) {
                item.copy(checked = checked)
            } else item
        }
    }

    fun getCheckedAttachments(): List<AttachmentItem> {

        return _attachmentList.value.filter {

            it.checked
        }
    }
}