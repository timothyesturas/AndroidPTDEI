package com.it.ptdei.menu.customerPo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it.ptdei.global.ApiConfig
import com.it.ptdei.global.RetrofitClient
import com.it.ptdei.menu.customerPo.features.getEmpList.EmployeeModel
import com.it.ptdei.menu.customerPo.features.getRecords.CustPoRecordModel
import com.it.ptdei.menu.customerPo.features.saveRecords.UpdateCustomerPoItem
import com.it.ptdei.menu.customerPo.features.saveRecords.UpdateCustomerPoRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.HttpException

class CustomerPOViewModel(
    private val okHttpClient: OkHttpClient,
) : ViewModel() {

    private val custPoApi = RetrofitClient.createCustPoApi(
        baseUrl = ApiConfig.BASE_URL, okHttpClient = okHttpClient
    )

    private val custPoRepo = CustPoRepository(custPoApi)

    // --------------------------------------------------
    // EMPLOYEE LIST
    // --------------------------------------------------

    private val _empList = MutableStateFlow<List<EmployeeModel>>(
        emptyList()
    )

    val empList: StateFlow<List<EmployeeModel>> = _empList


    fun getEmpList() {
        if (_empList.value.isNotEmpty()) return
        viewModelScope.launch {
            try {
                val start = System.currentTimeMillis()

                val response = withContext(Dispatchers.IO) {
                    custPoRepo.getEmpList()
                }
                Log.d(
                    "CustomerPOViewModel",
                    "Updating employee list: ${response.size} items, " + "${System.currentTimeMillis() - start} ms"
                )

                _empList.value = response

            } catch (e: Exception) {
                Log.e(
                    "getEmpList", "ERROR", e
                )
            }
        }
    }

    // --------------------------------------------------
    // SCANNED RECORDS
    // --------------------------------------------------

    private val _scannedCpRecords = MutableStateFlow<List<CustPoRecordModel>>(
        emptyList()
    )

    val scannedCpRecords: StateFlow<List<CustPoRecordModel>> = _scannedCpRecords

    fun getRecordsByPoNo(
        cpNo: String,
    ) {

        Log.d(
            "VIEWMODEL", "Fetching items for: $cpNo"
        )

        viewModelScope.launch {

            requestState.value = RequestState.Loading

            try {

                val response = custPoRepo.getRecordsByPoNo(cpNo)

                _scannedCpRecords.value = response

                requestState.value = RequestState.ItemsLoaded

            } catch (e: HttpException) {

                if (e.code() == 401) {

                    requestState.value = RequestState.SessionExpired

                } else {

                    requestState.value = RequestState.Error(
                        e.message() ?: "Unknown error"
                    )
                }

            } catch (e: Exception) {

                Log.e(
                    "VIEWMODEL", "API ERROR", e
                )

                requestState.value = RequestState.Error(
                    e.message ?: "Unknown error"
                )
            }
        }
    }

    // --------------------------------------------------
    // UPDATE CUSTOMER PO
    // --------------------------------------------------

    fun updateCustomerPo(
        cpNo: String,

        checkedBy: String?,
        packedBy: String?,
        assistedBy: String?,

        items: List<UpdateCustomerPoItem>,
    ) {

        viewModelScope.launch {

            requestState.value = RequestState.Loading

            try {

                val request = UpdateCustomerPoRequest(
                    cpNo = cpNo,

                    checkedBy = checkedBy,

                    packedBy = packedBy,

                    assistedBy = assistedBy,

                    items = items
                )

                Log.d(
                    "CUSTOMER_PO_UPDATE", "Request: $request"
                )

                custPoRepo.updateCustomerPo(
                    request
                )

                /*
                 * Refresh the records after saving.
                 *
                 * This is useful because the UI will
                 * immediately contain the values returned
                 * by the database.
                 */
                val updatedRecords = custPoRepo.getRecordsByPoNo(
                    cpNo
                )

                _scannedCpRecords.value = updatedRecords

                requestState.value = RequestState.UpdateSuccess

            } catch (e: HttpException) {

                if (e.code() == 401) {

                    requestState.value = RequestState.SessionExpired

                } else {

                    Log.e(
                        "CUSTOMER_PO_UPDATE", "HTTP ERROR", e
                    )

                    requestState.value = RequestState.Error(
                        e.message() ?: "Update failed"
                    )
                }

            } catch (e: Exception) {

                Log.e(
                    "CUSTOMER_PO_UPDATE", "UPDATE ERROR", e
                )

                requestState.value = RequestState.Error(
                    e.message ?: "Update failed"
                )
            }
        }
    }

    // --------------------------------------------------
    // REQUEST STATE
    // --------------------------------------------------

    val requestState = MutableStateFlow<RequestState>(
        RequestState.Idle
    )

    sealed class RequestState {

        data object Idle : RequestState()

        data object Loading : RequestState()

        data object ItemsLoaded : RequestState()

        data object UpdateSuccess : RequestState()

        data object SessionExpired : RequestState()

        data class Error(
            val message: String,
        ) : RequestState()
    }
}