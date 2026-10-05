package com.it.ptdei.menu.customerPo.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.it.ptdei.global.HiddenCameraQrScanner
import com.it.ptdei.global.SessionManager
import com.it.ptdei.menu.customerPo.CustomerPOViewModel
import com.it.ptdei.menu.customerPo.features.saveRecords.UpdateCustomerPoItem
import okhttp3.OkHttpClient

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight", "UnusedBoxWithConstraintsScope", "DefaultLocale",
    "FrequentlyChangingValue"
)
@Composable
fun CustomerPOScreen(
    viewModel: CustomerPOViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    okHttpClient: OkHttpClient,
    sessionManager: SessionManager,
) {
    val context = LocalContext.current

    val focusRequester = remember { FocusRequester() }

    //para pagkaopen ng screen, nakafocus agad sa textbox na may focusRequester na code
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val isSmallScreen = screenWidth < 361.dp


    var cpNo by rememberSaveable { mutableStateOf("") }
    var lastScanTime by remember { mutableLongStateOf(0L) }
    var isScanningEnabled by remember { mutableStateOf(true) }



//    var fromDate by remember { mutableStateOf(SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()))}
//    var toDate by remember { mutableStateOf(SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()))}

    val requestState by viewModel.requestState.collectAsState()
    val scannedState by viewModel.scannedCpRecords.collectAsState()
    var selectedItemTypes by remember {
        mutableStateOf<Map<String, String>>(emptyMap())
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var cameraLoading by remember {
        mutableStateOf(true)
    }

    val hasCamera = remember {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraManager.cameraIdList.isNotEmpty()
        } catch (_: Exception) {
            false
        }
    }

    if (hasCamera) {

        Log.d("DEVICE", "Device has camera")

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

    } else {

        Log.d("DEVICE", "Device has no camera")
    }



    var showApprovalDialog by remember {
        mutableStateOf(false)
    }

    var currentStep by remember {
        mutableIntStateOf(0)
    }

    val record = scannedState.firstOrNull()



    var checkedBy by remember { mutableStateOf(record?.checkedBy) }
    var packedBy by remember { mutableStateOf(record?.packedBy) }
    var assistedBy by remember { mutableStateOf(record?.assistedBy) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top blue curve
            val redPath = Path().apply {
                moveTo(0f, size.height * 0.20f)

                quadraticTo(
                    size.width * 0.5f, size.height * 0.05f, size.width, size.height * 0.10f
                )

                lineTo(size.width, 0f)
                lineTo(0f, 0f)
                close()
            }

            drawPath(
                path = redPath, brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD21919).copy(alpha = 0.8f), Color(0xFFD23519).copy(alpha = 0.35f)
                    )
                )
            )

            // Bottom red curve
            val bluePath = Path().apply {
                moveTo(0f, size.height * 0.95f)

                quadraticTo(
                    size.width * 0.5f, size.height * 0.95f, size.width, size.height * 0.65f
                )

                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = bluePath, brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3570E5).copy(alpha = 0.35f), Color(0xFF3593E5).copy(alpha = 0.75f)
                    )
                )
            )
        }


        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = {
//                                viewModel.clear()
                                onBack()
                                //clear the printerData
//                                viewModel.clearGeneratedResponses()
                            }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null
                            )
                        }
                    },
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Customer PO",
                                color = Color(0xFF000000),
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isSmallScreen) 15.sp
                                else 14.sp,

                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, false)
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->

            Column(
                modifier = Modifier.padding(paddingValues)
            ) {

                ProcessIndicator(currentStep = currentStep)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(6.dp)
                ) {

                    when (currentStep) {

                        // =====================================
                        // PROCESS 1
                        // =====================================

                        0 -> {

                            if (hasCamera) {
                                if (hasCameraPermission) {

                                    HiddenCameraQrScanner(
                                        hasPermission = true,

                                        onCameraReady = {
                                            cameraLoading = false
                                        }

                                    ) { result ->

                                        if (!isScanningEnabled) return@HiddenCameraQrScanner

                                        val now = System.currentTimeMillis()

                                        if (now - lastScanTime < 4000) {
                                            return@HiddenCameraQrScanner
                                        }

                                        lastScanTime = now

//                                        viewModel.setLastScannedTrackNo(result)
                                        cpNo = result
//                            playSound(R.raw.beep)
//                                        viewModel.getItemsByOrderId(result)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround,
                            ) {
                                if (hasCamera) {
                                    OutlinedTextField(
//                                        value = cpNo,
//                    value = "CPHQ202608140005UJ",//trsdb
                        value = "CPHQ2026032000003H",//dev
                                        onValueChange = { cpNo = it },
                                        label = {
                                            Text(
                                                text = "Enter Order ID",
                                                fontSize = if (isSmallScreen) 12.sp else 14.sp
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                if (cpNo.isNotBlank()) {
                                                    val scannedCode = cpNo.trim()

                                                    viewModel.getRecordsByPoNo(scannedCode)
//                                                    viewModel.setLastScannedTrackNo(trackNo)
//                                            playSound(R.raw.beep)
//                                                    viewModel.getItemsByOrderId(trackNo)
                                                }
                                            }),
                                    )
                                } else {
                                    OutlinedTextField(
                                        value = cpNo,
//                                value = "JT0019800930249",
//                                value = "P2423EWQ6JUAZ",
                                        onValueChange = {
                                            Log.d("SCANNER", "Received: [$it]")
                                            if (it.contains("\n")) {
                                                val scannedCode = it.trim()
                                                Log.d("SCANNER", scannedCode)
                                                viewModel.getRecordsByPoNo(scannedCode)
//                                                viewModel.setLastScannedTrackNo(scannedCode)
//                                        playSound(R.raw.beep)
//                                                viewModel.getItemsByOrderId(scannedCode)
                                                // Clear the field
                                                cpNo = ""

                                            } else {
                                                cpNo = it
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = "Enter CP No.",
                                                fontSize = if (isSmallScreen) 12.sp else 14.sp
                                            )
                                        },
                                        singleLine = false,
                                        modifier = Modifier
                                            .weight(1f)
                                            .focusRequester(focusRequester),
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                                    .verticalScroll(rememberScrollState())
                                    .height(screenHeight * 0.6f)
                            ) {
                                when {
                                    requestState is CustomerPOViewModel.RequestState.Loading -> {
                                        CircularProgressIndicator()
                                    }

                                    scannedState.isEmpty() -> {
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.White.copy(alpha = 0.8f),
                                            )
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(24.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "No records found for this CP No.",
                                                    color = Color.LightGray
                                                )
                                            }
                                        }
                                    }

                                    else -> {
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.White
                                            )
                                        ) {

                                            val listState = rememberLazyListState()

                                            Box(
                                                modifier = Modifier.fillMaxSize()
                                            ) {

                                                LazyColumn(
                                                    state = listState,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                                ) {

                                                    stickyHeader {
                                                        Surface(
                                                            color = Color.White
                                                        ) {
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(
                                                                        horizontal = 12.dp,
                                                                        vertical = 10.dp
                                                                    ),
                                                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {

                                                                listOf(
                                                                    "Item Type",
                                                                    "PO No.",
                                                                    "Customer"
                                                                ).forEach { title ->

                                                                    Text(
                                                                        text = title,
                                                                        modifier = Modifier.weight(1f),
                                                                        fontSize = if (isSmallScreen) 10.sp else 14.sp,
                                                                        fontWeight = FontWeight.SemiBold,
                                                                        color = Color.Gray,
                                                                        textAlign = TextAlign.Center
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }

                                                    itemsIndexed(
                                                        items = scannedState,
                                                        key = { _, record ->
                                                            "${record.cpNo}_${record.custCd}_${record.poNo}"
                                                        }
                                                    ) { index, record ->

                                                        val itemKey =
                                                            "${record.cpNo.orEmpty()}_${record.poNo.orEmpty()}"

                                                        val backgroundColor =
                                                            if (index % 2 == 0) {
                                                                Color.White
                                                            } else {
                                                                Color(0xFFEAF1F6)
                                                            }

                                                        CustomerPoRecordCard(
                                                            record = record,
                                                            selectedItemType = selectedItemTypes[itemKey]
                                                                ?: record.itemType,

                                                            onItemTypeChanged = { selectedType ->
                                                                selectedItemTypes =
                                                                    selectedItemTypes +
                                                                            (itemKey to selectedType)
                                                            },

                                                            backgroundColor = backgroundColor,
                                                            fontSize = if (isSmallScreen) 10 else 12
                                                        )
                                                    }
                                                }


                                                // ==============================
                                                // SCROLLBAR
                                                // ==============================

                                                val totalItems =
                                                    listState.layoutInfo.totalItemsCount

                                                val visibleItems =
                                                    listState.layoutInfo.visibleItemsInfo.size

                                                if (visibleItems in 1..<totalItems) {

                                                    val firstVisible =
                                                        listState.firstVisibleItemIndex

                                                    val scrollFraction =
                                                        firstVisible.toFloat() /
                                                                (totalItems - visibleItems).coerceAtLeast(1)

                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.CenterEnd)
                                                            .fillMaxHeight()
                                                            .width(6.dp)
                                                            .padding(
                                                                top = 8.dp,
                                                                bottom = 8.dp
                                                            )
                                                    ) {

                                                        // Track
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .background(
                                                                    Color.Gray.copy(alpha = 0.15f),
                                                                    RoundedCornerShape(50)
                                                                )
                                                        )

                                                        // Thumb
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .fillMaxHeight(
                                                                    (visibleItems.toFloat() / totalItems)
                                                                        .coerceIn(0.08f, 1f)
                                                                )
                                                                .align(
                                                                    Alignment.TopCenter
                                                                )
                                                                .offset {
                                                                    IntOffset(
                                                                        x = 0,
                                                                        y = (
                                                                                scrollFraction *
                                                                                        listState.layoutInfo
                                                                                            .viewportSize
                                                                                            .height
                                                                                ).toInt()
                                                                    )
                                                                }
                                                                .background(
                                                                    Color(0xFF1976D2),
                                                                    RoundedCornerShape(50)
                                                                )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {

                            LaunchedEffect(Unit) {
                                viewModel.getEmpList()
                            }

                            val employeeSuggestions by viewModel.empList.collectAsState()

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                // Header
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Verify Details",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF212121)
                                    )

                                    Text(
                                        text = "Please verify the information before saving.",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = Color(0xFFE5E5E5)
                                )

                                // Record details
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    DetailRow(
                                        label = "CP No.",
                                        value = record?.cpNo
                                    )

                                    DetailRow(
                                        label = "Order Date",
                                        value = formatOrderDate(record?.orderDate)
                                    )

                                    DetailRow(
                                        label = "Received From",
                                        value = record?.rcvFrom?.empName
                                    )

                                    DetailRow(
                                        label = "Business Unit",
                                        value = record?.busName
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = Color(0xFFE5E5E5)
                                )

                                // Checked By
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    EmployeeDropdownField(
                                        label = "Checked By",
                                        value = checkedBy,
                                        suggestions = employeeSuggestions,
                                        onValueChange = {
                                            checkedBy = it
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = { checkedBy = null }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear Checked By",
                                            tint = Color.Gray
                                        )
                                    }
                                }

                                // Packed By
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    EmployeeDropdownField(
                                        label = "Packed By",
                                        value = packedBy,
                                        suggestions = employeeSuggestions,
                                        onValueChange = {
                                            packedBy = it
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = { packedBy = null }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear Packed By",
                                            tint = Color.Gray
                                        )
                                    }
                                }

                                // Assisted By
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    EmployeeDropdownField(
                                        label = "Assisted By",
                                        value = assistedBy,
                                        suggestions = employeeSuggestions,
                                        onValueChange = {
                                            assistedBy = it
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = { assistedBy = null }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear Assisted By",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (currentStep > 0) {

                        OutlinedButton(
                            onClick = {
                                currentStep--
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(top = 0.dp, end = 8.dp, bottom = 24.dp, start = 8.dp)
                        ) {

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )
                            Text("Back")
                        }

                    } else {

                        Spacer(
                            modifier = Modifier.width(1.dp)
                        )
                    }

                    if (currentStep < 1) {

                        Button(
                            onClick = {
                                currentStep++
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(top = 0.dp, end = 8.dp, bottom = 24.dp, start = 8.dp)
                        ) {

                            Text("Next")

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next"
                            )
                        }

                    } else {

                        Button(
                            onClick = {


                                val cpNo = scannedState.firstOrNull()?.cpNo

                                if (cpNo.isNullOrBlank()) {
                                    Log.e(
                                        "CUSTOMER_PO_UPDATE",
                                        "Cannot save: CP No. is empty"
                                    )
                                    return@Button
                                }

                                val items = scannedState.map { record ->
                                    val itemKey = "${record.cpNo.orEmpty()}_${record.poNo.orEmpty()}"

                                    UpdateCustomerPoItem(
                                        poNo = record.poNo,
                                        itemType = selectedItemTypes[itemKey] ?: record.itemType
                                    )
                                }

                                Log.d("CUSTOMER_PO_UPDATE", "CP No       = $cpNo")
                                Log.d("CUSTOMER_PO_UPDATE", "Checked By  = ${checkedBy?.empNo}")
                                Log.d("CUSTOMER_PO_UPDATE", "Packed By   = ${packedBy?.empNo}")
                                Log.d("CUSTOMER_PO_UPDATE", "Assisted By = ${assistedBy?.empNo}")
                                Log.d("CUSTOMER_PO_UPDATE", "Items       = $items")

                                viewModel.updateCustomerPo(
                                    cpNo = cpNo,
                                    checkedBy = checkedBy?.empNo,
                                    packedBy = packedBy?.empNo,
                                    assistedBy = assistedBy?.empNo,
                                    items = items
                                )

                                showApprovalDialog = false
                            },
//                    enabled = images.isNotEmpty() && approvedSet.isNotEmpty(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EAD5B)),
                            modifier = Modifier.padding(top = 0.dp, end = 8.dp, bottom = 24.dp, start = 8.dp)
                        ) {

                            Text("Save")

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save"
                            )
                        }
                    }
                }

            }

            Column(
                modifier = Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding() - 16.dp,
                    )
                    .fillMaxSize()
                    .padding(16.dp)
            ) {


            }
        }
    }

    if (requestState is CustomerPOViewModel.RequestState.Loading) {
        Dialog(onDismissRequest = { } // Prevent dismiss
        ) {
            Card {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Processing, please wait...")
                }
            }
        }
    }


    when (requestState) {
        CustomerPOViewModel.RequestState.SessionExpired -> {

            AlertDialog(onDismissRequest = {}, title = {
                Text("Session Expired")
            }, text = {
                Text("Narestart yung Server. Please login again.")
            }, confirmButton = {
                Button(
                    onClick = {
                        onLogout()
                    }) {
                    Text("OK")
                }
            })
        }

        else -> {}
    }
}


fun getItemTypeDisplay(itemType: String?): String {
    return when (itemType?.uppercase()) {
        "CO" -> "Cosmetic"
        "GA" -> "Garments"
        else -> itemType ?: "Select"
    }
}


@Composable
fun ProcessIndicator(
    currentStep: Int
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(12.dp)
    ) {

        /*
         * ==========================================
         * CONNECTING LINE
         * ==========================================
         */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 17.5.dp)
                .padding(top = 17.5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            for (step in 0..1) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            if (step < currentStep) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.LightGray
                            }
                        )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (step in 0..1) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(
                                if (step <= currentStep) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color(0xFFF0F1F3)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (step < currentStep) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "${step + 1}",
                                color = if (step == currentStep) {
                                    Color.White
                                } else {
                                    Color.Gray
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = if(step == 0) {"Search CP No."} else {"Choose Persons"},
                        fontSize = 12.sp,
                        fontWeight = if (step == currentStep) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            }
        }
    }
}