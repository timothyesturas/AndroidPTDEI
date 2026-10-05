package com.it.ptdei.menu.returns

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.filled.ContentPasteSearch
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.it.ptdei.R
import com.it.ptdei.global.HiddenCameraQrScanner
import com.it.ptdei.global.SessionManager
import com.it.ptdei.global.createImageFile
import com.it.ptdei.menu.returns.features.returnItems.ForReturnItem
import com.it.ptdei.menu.returns.ui.AttachmentDialog
import com.it.ptdei.menu.returns.ui.GenerateReportDialog
import com.it.ptdei.menu.returns.ui.ItemCard
import com.it.ptdei.menu.returns.ui.PrintReportDialog
import com.it.ptdei.menu.returns.ui.ReportPreviewDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



@SuppressLint("ConfigurationScreenWidthHeight", "UnusedBoxWithConstraintsScope", "DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnsScreen(
    viewModel: ReturnsViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    okHttpClient: OkHttpClient,
    sessionManager: SessionManager,
) {


    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val drawerScope = rememberCoroutineScope()


    var isEnabled by remember { mutableStateOf(false) }

    val report by viewModel.report.collectAsState()

    var printRef by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    var showPreviewDialog by remember { mutableStateOf(false) }
    var previewPcName by remember { mutableStateOf("") }
    val reportStatusText: String = viewModel.reportStatusText.value

    val configuration = LocalConfiguration.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val screenWidth = configuration.screenWidthDp.dp
    val isSmallScreen = screenWidth < 361.dp
    val returnState by viewModel.returnState.collectAsState()
    val lastScannedTrackNo by viewModel.lastScannedTrackNo.collectAsState()
    val scannedState by viewModel.scannedItems.collectAsState()

    var approvedSet by remember {
        mutableStateOf(setOf<ForReturnItem>())
    }

    val items = scannedState.rtvItemModelList
    val selectableItems = items.filter { it.sretNo.isNullOrBlank() }
    val allSelected = selectableItems.isNotEmpty() && selectableItems.all {
        ForReturnItem(it.rtvNo, it.itemNo) in approvedSet
    }

    var returnType by remember {
        mutableStateOf("NOT DELIVERED")
    }

    val returnTypes = listOf(
        "NOT DELIVERED", "DELIVERED", "AWAITING RETURN"
    )

    var lastScanTime by remember { mutableLongStateOf(0L) }
    var isScanningEnabled by remember { mutableStateOf(true) }

    var showAttachmentDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(allSelected) {
        if (allSelected) {
            showAttachmentDialog = true
        }
    }


    val context = LocalContext.current

    var capturedImageFile by remember {
        mutableStateOf<File?>(null)
    }

    val addPhotoImages = remember {
        mutableStateListOf<Uri>()
    }


    var trackNoInput by rememberSaveable { mutableStateOf("") }

    fun playSound(resId: Int) {
        val mp = MediaPlayer.create(context, resId)
        mp.setOnCompletionListener { it.release() }
        mp.start()
    }

    var hasCameraPermission by remember {
        mutableStateOf(false)
    }

    var cameraLoading by remember {
        mutableStateOf(true)
    }

    var showReportDialog by remember {
        mutableStateOf(false)
    }

    var showReportDialog2 by remember {
        mutableStateOf(false)
    }

    var customerCode by remember(viewModel.custCd) {
        mutableStateOf(viewModel.custCd)
    }

    var customerBranch by remember(viewModel.custBr) {
        mutableStateOf(viewModel.custBr)
    }

    var fromDate by remember {
        mutableStateOf(
            SimpleDateFormat(
                "MM/dd/yyyy", Locale.getDefault()
            ).format(Date())
        )
    }
    var toDate by remember {
        mutableStateOf(
            SimpleDateFormat(
                "MM/dd/yyyy", Locale.getDefault()
            ).format(Date())
        )
    }
    var expanded by remember { mutableStateOf(false) }


    LaunchedEffect(returnState) {

        when (returnState) {
            ReturnsViewModel.ReturnState.ItemsLoaded -> {
                playSound(R.raw.beep)
            }

            ReturnsViewModel.ReturnState.CustomerMismatch -> {
                playSound(R.raw.wrongcourier)
            }

            else -> {}
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->

        if (success) {

            capturedImageFile?.let { file ->
                Log.d(
                    "CAMERA", "Image saved: ${file.absolutePath}"
                )
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->

        if (granted) {

            try {

                val file = createImageFile(context)

                capturedImageFile = file

                val uri = FileProvider.getUriForFile(
                    context, "${context.packageName}.provider", file
                )

                cameraLauncher.launch(uri)

            } catch (e: Exception) {

                Log.e(
                    "CAMERA", "Failed to open camera", e
                )
            }
        }
    }

    //check if the device has a camera
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
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasCameraPermission = isGranted
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            launcher.launch(Manifest.permission.CAMERA)
        }

    } else {

        Log.d("DEVICE", "Device has no camera")
    }

    // ---------------- CLEAR ON EXIT ----------------
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clear()
        }
    }


    val attachmentList by viewModel.attachmentList.collectAsState()

    viewModel.getCheckedAttachments().forEach { it.url }

    LaunchedEffect(scannedState.rtvItemModelList) {

        val poNo = scannedState.rtvItemModelList.firstOrNull()?.poNo

        if (!poNo.isNullOrBlank()) {

            viewModel.loadAttachments(poNo)
        }
    }

    val ipList by viewModel.ipList.collectAsState()

    var selectedPc by remember {
        mutableStateOf(UserIpDataModel())
    }

    LaunchedEffect(scannedState.rtvItemModelList) {
        viewModel.getUserIp()
    }

    fun onPrintReportClick() {
        drawerScope.launch {
            drawerState.close()
        }

        showReportDialog = true
    }

    val waybillCount by viewModel.waybillCount.collectAsState()

    LaunchedEffect(waybillCount) {
        if (waybillCount == 20) {
            onPrintReportClick()
        }
    }


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

        Canvas(modifier = Modifier.fillMaxSize()) {

            val wavePath = Path().apply {

                moveTo(
                    0f, size.height * 0.72f
                )

                quadraticTo(
                    size.width * 0.20f, size.height * 0.60f, size.width * 0.45f, size.height * 0.72f
                )

                quadraticTo(
                    size.width * 0.75f, size.height * 0.88f, size.width, size.height * 0.70f
                )

                lineTo(size.width, size.height)
                lineTo(0f, size.height)

                close()
            }

            val wavePaths = Path().apply {

                moveTo(0f, size.height * 0.15f)

                cubicTo(
                    size.width * 0.25f, size.height * 0.02f,

                    size.width * 0.75f, size.height * 0.28f,

                    size.width, size.height * 0.12f
                )

                lineTo(size.width, 0f)
                lineTo(0f, 0f)

                close()
            }

            drawPath(
                path = wavePaths,

                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Blue.copy(alpha = .4f),
                        Color.Blue.copy(alpha = .9f),
                        Color.Blue.copy(alpha = .5f),
                        Color.Blue.copy(alpha = .9f),
                        Color.Blue.copy(alpha = .4f),
                    )
                )
            )

            drawPath(
                path = wavePath,

                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Red.copy(alpha = .4f),
                        Color.Red.copy(alpha = .9f),
                        Color.Red.copy(alpha = .5f),
                        Color.Red.copy(alpha = .9f),
                        Color.Red.copy(alpha = .4f),
                    )
                )
            )
        }


        ModalNavigationDrawer(
            drawerState = drawerState,

            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    drawerContainerColor = Color.White,
                    drawerContentColor = Color(0xFF1A1A1A),
                    windowInsets = WindowInsets(0)
                ) {

                    // ==============================
                    // HEADER
                    // ==============================

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF1565C0),
                                        Color(0xFF1976D2)
                                    )
                                )
                            )
                            .padding(
                                horizontal = 24.dp,
                                vertical = 28.dp
                            )
                    ) {

                        Column {

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.AssignmentReturn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                text = "Returned Orders",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "Manage return transactions",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 13.sp
                            )
                        }
                    }


                    // ==============================
                    // MENU
                    // ==============================

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )


                    Text(
                        text = "ACTIONS",
                        modifier = Modifier.padding(
                            horizontal = 24.dp,
                            vertical = 8.dp
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )


                    // ATTACHMENTS
                    NavigationDrawerItem(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 2.dp
                        ),
                        icon = {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                tint = Color(0xFF1976D2)
                            )
                        },
                        label = {
                            Text(
                                text = "Attachments",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        selected = false,
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedTextColor = Color(0xFF333333),
                            unselectedIconColor = Color(0xFF1976D2)
                        ),
                        onClick = {

                            drawerScope.launch {
                                drawerState.close()
                            }

                            val poNo = scannedState.rtvItemModelList
                                .firstOrNull()
                                ?.poNo

                            if (poNo != null) {
                                viewModel.loadAttachments(poNo)
                                showAttachmentDialog = true
                            }
                        }
                    )


                    // SEARCH REPORT
                    NavigationDrawerItem(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 2.dp
                        ),
                        icon = {
                            Icon(
                                Icons.Default.ContentPasteSearch,
                                contentDescription = null,
                                tint = Color(0xFF7B1FA2)
                            )
                        },
                        label = {
                            Text(
                                text = "Search Report",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        selected = false,
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedTextColor = Color(0xFF333333),
                            unselectedIconColor = Color(0xFF7B1FA2)
                        ),
                        onClick = {

                            drawerScope.launch {
                                drawerState.close()
                            }

                            showReportDialog2 = true
                        }
                    )

                    // PRINT REPORT
                    NavigationDrawerItem(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 2.dp
                        ),
                        icon = {
                            Icon(
                                Icons.Default.Print,
                                contentDescription = null,
                                tint = Color(0xFF388E3C)
                            )
                        },
                        label = {
                            Text(
                                text = "Print Report",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        selected = false,
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedTextColor = Color(0xFF333333),
                            unselectedIconColor = Color(0xFF388E3C)
                        ),
                        onClick = {
                            onPrintReportClick()
                        }
                    )


                    // REFRESH
                    NavigationDrawerItem(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 2.dp
                        ),
                        icon = {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color(0xFFE65100)
                            )
                        },
                        label = {
                            Text(
                                text = "Refresh",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        selected = false,
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedTextColor = Color(0xFF333333),
                            unselectedIconColor = Color(0xFFE65100)
                        ),
                        onClick = {

                            drawerScope.launch {
                                drawerState.close()
                            }

                            val trackingNo =
                                viewModel.lastScannedTrackNo.value

                            if (trackingNo.isNotBlank()) {
                                viewModel.getItemsByTrackingNo(trackingNo)
                            }
                        }
                    )


                    Spacer(
                        modifier = Modifier.weight(1f)
                    )


                    HorizontalDivider(
                        color = Color.LightGray
                    )


                    Text(
                        text = "Returns Management",
                        modifier = Modifier.padding(
                            horizontal = 24.dp,
                            vertical = 16.dp
                        ),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        ) {

            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            // Action here
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(
                                x = (-20).dp,
                                y = (-100).dp
                            ),
                        shape = CircleShape,
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    ) {
                        Text(
                            text = waybillCount.toString(),
                            fontSize = 32.sp
                        )
                    }
                },
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        ), title = {

                            BoxWithConstraints {


                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = "$lastScannedTrackNo - ",
                                        color = Color(0xFFFFEB3B),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = if (isSmallScreen) 12.sp
                                        else 14.sp,

                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, false)
                                    )

                                    Text(
                                        text = viewModel.rtvNo,
                                        color = Color.White,
                                        fontWeight = FontWeight.Light,
                                        fontSize = if (isSmallScreen) 12.sp
                                        else 14.sp,

                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, false)
                                    )


                                    val cancelTypeCode = when (viewModel.cancelType) {
                                        "NOT DELIVERED" -> "CNCLD"
                                        "DELIVERED" -> "RTS"
                                        "AWAITING RETURN" -> "RVRS"
                                        else -> viewModel.cancelType
                                    }
                                    Text(
                                        text = " - $cancelTypeCode",
                                        color = Color.Red,
                                        fontWeight = FontWeight.Light,
                                        fontSize = if (isSmallScreen) 12.sp
                                        else 14.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(0.5f, false)
                                    )
                                }
                            }
                        }, navigationIcon = {


                            if (hasCamera) {
                                if (!cameraLoading) {
                                    IconButton(
                                        onClick = {
                                            viewModel.clear()
                                            onBack()
                                            //clear the printerData
                                            viewModel.clearGeneratedResponses()
                                        }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null
                                        )
                                    }
                                }
                            } else {
                                IconButton(
                                    onClick = {
                                        viewModel.clear()
                                        onBack()
                                        //clear the printerData
                                        viewModel.clearGeneratedResponses()
                                    }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null
                                    )
                                }
                            }
                        },
                        // ✅ NEW BUTTON
                        actions = {
                            IconButton(
                                onClick = {
                                    drawerScope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Open Menu"
                                )
                            }
                        })
                },
            ) { padding ->

                when (returnState) {
                    ReturnsViewModel.ReturnState.SessionExpired -> {
                        AlertDialog(onDismissRequest = {}, title = {
                            Text("Session Expired")
                        }, text = {
                            Text("Server restarted. Please login again.")
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

                if (showReportDialog) {
                    GenerateReportDialog(
                        context = context,
                        fromDate = fromDate,
                        toDate = toDate,
                        customerCode = customerCode,
                        customerBranch = customerBranch,
                        returnType = returnType,
                        returnTypes = returnTypes,
                        expanded = expanded,
                        selectedPc = selectedPc,
                        ipList = ipList,
                        onFromDateChange = { fromDate = it },
                        onToDateChange = { toDate = it },
                        onCustomerCodeChange = { customerCode = it },
                        onCustomerBranchChange = { customerBranch = it },
                        onReturnTypeChange = { returnType = it },
                        onExpandedChange = { expanded = it },
                        onPcSelected = { selectedPc = it },
                        onGenerate = {
                            previewPcName = selectedPc.name
                            showPreviewDialog = true

                            coroutineScope.launch {
                                val printRefNo = viewModel.saveToPrintedReturns(
                                    fromDate = fromDate,
                                    toDate = toDate,
                                    custCd = customerCode,
                                    custBr = customerBranch,
                                    printedBy = sessionManager.getUsername(),
                                    returnType = returnType
                                )

                                if (!printRefNo.isNullOrBlank()) {
                                    viewModel.generateReport(
                                        selectedPc = selectedPc,
                                        sessionManager = sessionManager,
                                        printRefNo = printRefNo
                                    )
                                }

                                showReportDialog = false
                            }
                        },

                        onDismiss = {
                            showReportDialog = false
                        })
                }


                if (showReportDialog2) {

                    PrintReportDialog(
                        printRef = printRef, onPrintRefChange = {
                            printRef = it
                        },

                        expanded = expanded, onExpandedChange = {
                            expanded = it
                        },

                        selectedPc = selectedPc, ipList = ipList, onPcSelected = {
                            selectedPc = it
                        },

                        report = report,

                        onCheck = {

                            coroutineScope.launch {

                                viewModel.getReport(printRef)

                            }
                        },

                        onGenerate = {

                            previewPcName = selectedPc.name
                            showPreviewDialog = true

                            coroutineScope.launch {

                                if (printRef.isNotBlank()) {

                                    viewModel.generateReport(
                                        selectedPc = selectedPc,
                                        sessionManager = sessionManager,
                                        printRefNo = printRef
                                    )
                                }

                                showReportDialog2 = false
                            }
                        },

                        onDismiss = {
                            showReportDialog2 = false
                        })
                }


                if (showPreviewDialog) {
                    ReportPreviewDialog(
                        previewPcName = previewPcName,
                        reportStatusText = reportStatusText,
                        onDismiss = {
                            showPreviewDialog = false
                        })
                }

                if (hasCamera) {
                    if (cameraLoading) {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                                ), contentAlignment = Alignment.Center
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                CircularProgressIndicator()

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    "Opening camera...", style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                if (showAttachmentDialog) {
                    AttachmentDialog(
                        attachmentList = attachmentList,
                        hasCamera = hasCamera,
                        okHttpClient = okHttpClient,
                        context = context,

                        onDismiss = {
                            showAttachmentDialog = false
                        },

                        onCheckedChange = { url, checked ->
                            showAttachmentDialog = false
                            viewModel.updateAttachmentChecked(url, checked)
                        },

                        onAddPhoto = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                            showAttachmentDialog = false
                        },
                        images = addPhotoImages
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

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

                                viewModel.setLastScannedTrackNo(result)
                                trackNoInput = result
    //                            playSound(R.raw.beep)
                                viewModel.getItemsByTrackingNo(result)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .92f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                        ) {
                            if (hasCamera) {
                                OutlinedTextField(
                                    value = trackNoInput,
    //                                value = "JT0019892501843",
    //                                value = "P2423EWQ6JUAZ",
                                    onValueChange = { trackNoInput = it },
                                    label = {
                                        Text(
                                            text = "Enter Tracking Number",
                                            fontSize = if (isSmallScreen) 12.sp else 14.sp
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (trackNoInput.isNotBlank()) {
                                                val dataInput = trackNoInput.trim()
                                                viewModel.setLastScannedTrackNo(dataInput)
    //                                            playSound(R.raw.beep)

                                                if (isEnabled) {
                                                    viewModel.getItemsByOrderId(dataInput)
                                                } else {
                                                    viewModel.getItemsByTrackingNo(dataInput)
                                                }
                                            }
                                        })
                                )
                            } else {
                                OutlinedTextField(
                                    value = trackNoInput,
    //                                value = "JT0019800930249",
    //                                value = "P2423EWQ6JUAZ",
                                    onValueChange = {
                                        Log.d("SCANNER", "Received: [$it]")
                                        if (it.contains("\n")) {
                                            val scannedCode = it.trim()
                                            Log.d("SCANNER", scannedCode)
                                            viewModel.setLastScannedTrackNo(scannedCode)
//                                            playSound(R.raw.beep)

                                            if (isEnabled) {
                                                viewModel.getItemsByOrderId(scannedCode)
                                            } else {
                                                viewModel.getItemsByTrackingNo(scannedCode)
                                            }
                                            // Clear the field
                                            trackNoInput = ""

                                        } else {
                                            trackNoInput = it
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = "Enter Tracking Number",
                                            fontSize = if (isSmallScreen) 12.sp else 14.sp
                                        )
                                    },
                                    singleLine = false,
                                    modifier = Modifier
                                        .weight(1f)
                                        .focusRequester(focusRequester),
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = allSelected,
                                    enabled = selectableItems.isNotEmpty(),
                                    onCheckedChange = { checked ->
                                        approvedSet = if (checked) {
                                            approvedSet + selectableItems.map {
                                                ForReturnItem(it.rtvNo, it.itemNo)
                                            }
                                        } else {
                                            approvedSet - selectableItems.map {
                                                ForReturnItem(it.rtvNo, it.itemNo)
                                            }.toSet()
                                        }
                                    })
//                                Text(
//                                    text = "Select All", maxLines = 1, fontSize = 14.sp
//                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(
                            1.5.dp, Color(0xFF4CAF50)
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = viewModel.custName + " - " + viewModel.branchName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }








                    Spacer(modifier = Modifier.height(8.dp))


                    // ---------------- LOADING STATE ----------------
                    val items = scannedState.rtvItemModelList

                    if (returnState is ReturnsViewModel.ReturnState.Loading) {
                        CircularProgressIndicator()
                    } else if (items.isEmpty()) {

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "No items found for this QR",
                                    color = Color.Gray.copy(alpha = 0.9f)
                                )
                            }
                        }
                    } else {

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                        ) {

                            // ---------------- LIST ----------------
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                            ) {
                                items(
                                    scannedState.rtvItemModelList,
                                    key = { "${it.rtvNo}_${it.itemNo}" }) { product ->

                                    val key = ForReturnItem(
                                        rtvNo = product.rtvNo, itemNo = product.itemNo
                                    )

                                    val isSelected = key in approvedSet

                                    ItemCard(
                                        item = product,
                                        isReturned = isSelected,
                                        onClicked = { checked ->
                                            approvedSet = if (checked) {
                                                Log.e("$product.rtvNo $product.itemNo", "checked")
                                                approvedSet + key
                                            } else {
                                                Log.e("$product.rtvNo $product.itemNo", "unchecked")
                                                approvedSet - key
                                            }
                                        },
                                        onDisapproveChange = {},
                                        okHttpClient = okHttpClient,
                                    )
                                }
                            }
                        }
                    }



                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Button(
                            shape = RoundedCornerShape(8.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp
                            ),
                            onClick = {
                                val trackingNo = viewModel.lastScannedTrackNo.value

                                val multipartFile = capturedImageFile?.let {
                                    createMultipartFromFile(it)
                                }

                                viewModel.createSretByRtvServlet(
                                    trackingNo = trackingNo,
                                    items = approvedSet.toList(),
                                    file = multipartFile,
                                    null
                                ) { success ->
                                    if (success) {
                                        approvedSet = emptySet()
                                        capturedImageFile = null
                                        viewModel.getItemsByTrackingNo(trackingNo)
                                    }
                                }
                            },
                            enabled = approvedSet.isNotEmpty() &&
                                    (attachmentList.any { it.checked } ||
                                            capturedImageFile != null),
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                                .padding(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White,
                                disabledContainerColor = Color.Gray.copy(alpha = 0.4f),
                                disabledContentColor = Color.White.copy(alpha = 0.7f)
                            )
                        ) {
                            Text("Save")
                        }

                        Column(
                            modifier = Modifier
                                .height(80.dp)
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isEnabled) "Order Id" else "Track No",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                color = if (!isEnabled)
                                    Color(0xFFA9A9A9)
                                else
                                    Color(0xFF1CB3FF)
                            )

                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { isEnabled = it }
                            )
                        }


//                        Button(
//                            onClick = {
//                                showReportDialog2 = true
//                            }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(
//                                containerColor = Color(0xFF159B1B), contentColor = Color.White
//                            ), elevation = ButtonDefaults.buttonElevation(
//                                defaultElevation = 8.dp
//                            ), modifier = Modifier.padding(4.dp)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.Print, contentDescription = "Print"
//                            )
//                        }
//
//                        Button(
//                            onClick = {
//                                showReportDialog = true
//                            },
//                            shape = RoundedCornerShape(8.dp),
//                            elevation = ButtonDefaults.buttonElevation(
//                                defaultElevation = 8.dp
//                            ),
//                            modifier = Modifier.padding(4.dp)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.Print, contentDescription = "Print"
//                            )
//
//                            Spacer(modifier = Modifier.width(4.dp))
//
//                            Text(
//                                text = viewModel.waybillCount.collectAsState().value.toString(),
//                                color = Color.White,
//                                fontSize = 20.sp
//                            )
//                        }
                    }

                }
            }
        }
    }
    if (returnState is ReturnsViewModel.ReturnState.Loading) {
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



}


@Composable
fun RowScope.TableCell(
    text: String?,
    weight: Float,
    bold: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {

    if (text != null) {

        Text(
            text = text,

            modifier = Modifier
                .weight(weight)
                .padding(horizontal = 4.dp),

            fontSize = 12.sp,

            fontWeight = if (bold) FontWeight.SemiBold
            else FontWeight.Normal,

            color = color,

            maxLines = 1,

            overflow = TextOverflow.Ellipsis
        )
    }
}

fun openFile(
    context: Context,
    fileUrl: String,
) {

    val intent = Intent(
        Intent.ACTION_VIEW, fileUrl.toUri()
    )

    context.startActivity(intent)
}

fun createMultipartFromFile(
    file: File,
): MultipartBody.Part {

    val mimeType = when {
        file.extension.equals("jpg", ignoreCase = true) ||
                file.extension.equals("jpeg", ignoreCase = true) ->
            "image/jpeg"

        file.extension.equals("png", ignoreCase = true) ->
            "image/png"

        file.extension.equals("webp", ignoreCase = true) ->
            "image/webp"

        file.extension.equals("mp4", ignoreCase = true) ->
            "video/mp4"

        file.extension.equals("3gp", ignoreCase = true) ->
            "video/3gpp"

        else ->
            "application/octet-stream"
    }

    val requestFile = file.asRequestBody(
        mimeType.toMediaType()
    )

    return MultipartBody.Part.createFormData(
        "medias", file.name, requestFile
    )
}

suspend fun downloadPdfToCache(
    context: Context,
    pdfUrl: String,
    okHttpClient: OkHttpClient,
): File? = withContext(Dispatchers.IO) {

    try {


        val request = Request.Builder().url(pdfUrl).build()

        val response = okHttpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            return@withContext null
        }

        val file = File(
            context.cacheDir, "pdf_${pdfUrl.hashCode()}.pdf"
        )

        response.body?.byteStream()?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        file

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun generatePdfThumbnail(
    pdfFile: File,
): Bitmap? {
    return try {
        ParcelFileDescriptor.open(
            pdfFile, ParcelFileDescriptor.MODE_READ_ONLY
        ).use { descriptor ->

            PdfRenderer(descriptor).use { renderer ->

                val page = renderer.openPage(0)

                val targetWidth = 512
                val scale = 512f / page.width
                val targetHeight = (page.height * scale).toInt()
                val bitmap = createBitmap(targetWidth, targetHeight)

                page.render(
                    bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )

                page.close()

                bitmap
            }
        }
    } catch (_: Exception) {
        null
    }
}
