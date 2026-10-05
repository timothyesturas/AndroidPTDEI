package com.it.ptdei.menu.returns

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.it.ptdei.R
import com.it.ptdei.global.ApiConfig
import com.it.ptdei.global.HiddenCameraQrScanner
import com.it.ptdei.global.ImageLoaderProvider
import com.it.ptdei.global.createImageFile
import com.it.ptdei.global.createVideoFile
import com.it.ptdei.menu.returns.features.returnItems.ForReturnItem
import com.it.ptdei.menu.returns.ui.AttachmentDialog
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import java.io.File


@SuppressLint("UnusedBoxWithConstraintsScope", "ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProcessScreen(
    viewModel: ReturnsViewModel,
    onBack: () -> Unit,
    okHttpClient: OkHttpClient,
) {

    val customers by viewModel.customers.collectAsState()
    val customerSuggestions = customers.values.toList()

    val couriers by viewModel.couriers.collectAsState()
    val courierSuggestions = couriers.values.toList()

    val context = LocalContext.current

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var mediaToDelete by remember {
        mutableStateOf<Uri?>(null)
    }

    var mediaTypeToDelete by remember {
        mutableStateOf("")
    }

    var currentStep by remember {
        mutableIntStateOf(0)
    }

    val attachmentList by viewModel.attachmentList.collectAsState()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val isSmallScreen = screenWidth < 361.dp
    val returnState by viewModel.returnState.collectAsState()
    val scannedState by viewModel.scannedItems.collectAsState()

    var approvedSet by remember {
        mutableStateOf(setOf<ForReturnItem>())
    }

    val items = scannedState.rtvItemModelList
    val selectableItems = items.filter { it.sretNo.isNullOrBlank() }
    val allSelected = selectableItems.isNotEmpty() && selectableItems.all {
        ForReturnItem(it.rtvNo, it.itemNo) in approvedSet
    }
    var lastScanTime by remember { mutableLongStateOf(0L) }
    var isScanningEnabled by remember { mutableStateOf(true) }

    var showAttachmentDialog by remember {
        mutableStateOf(false)
    }

    var isProcessingVideo by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(allSelected) {
        if (allSelected) {
            showAttachmentDialog = true
        }
    }

    LaunchedEffect(scannedState.rtvItemModelList) {

        val poNo = scannedState.rtvItemModelList.firstOrNull()?.poNo

        if (!poNo.isNullOrBlank()) {

            viewModel.loadAttachments(poNo)
        }
    }

    val imageLoader = remember {
        ImageLoaderProvider.getPictures(context, okHttpClient)
    }

    var capturedImageFile by remember {
        mutableStateOf<File?>(null)
    }

    var capturedAddPhoto by remember {
        mutableStateOf<File?>(null)
    }

    val uploadReverseMediaList = remember {
        mutableStateListOf<File>()
    }

    val images = remember {
        mutableStateListOf<Uri>()
    }

    val addPhotoImages = remember {
        mutableStateListOf<Uri>()
    }

    val videos = remember {
        mutableStateListOf<Uri>()
    }

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var selectedVideoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var selectedMediaUrl by remember { mutableStateOf<String?>(null) }
    var selectedMediaType by remember { mutableStateOf<String?>(null) }

    var currentVideoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    fun playSound(resId: Int) {
        val mp = MediaPlayer.create(context, resId)
        mp.setOnCompletionListener { it.release() }
        mp.start()
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


    var pendingMediaType by remember {
        mutableStateOf<String?>(null)
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

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->

        if (success) {

            capturedImageFile?.let { file ->

                Log.d(
                    "UPLOAD",
                    "Photo captured: ${file.absolutePath}, exists=${file.exists()}, size=${file.length()}"
                )

                uploadReverseMediaList.add(file)

                val imageUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

                images.add(imageUri)
            }
        }
    }

//    val videoLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.CaptureVideo()
//    ) { success ->
//        if (success) {
//
//            currentVideoUri?.let { uri ->
//
//                videos.add(uri)
//
//                Log.d(
//                    "VIDEO",
//                    "Video saved: $uri"
//                )
//            }
//
//            currentVideoUri = null
//        }
//    }

    val scope = rememberCoroutineScope()

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->

        if (success) {

            currentVideoUri?.let { uri ->

                scope.launch {

                    try {
                        isProcessingVideo = true

                        Log.d(
                            "VIDEO",
                            "Processing video..."
                        )

                        val processedFile =
                            processVideoForUpload(
                                context = context,
                                inputUri = uri
                            )

                        Log.d(
                            "VIDEO",
                            "Processed video:"
                        )

                        Log.d(
                            "VIDEO",
                            "Path: ${processedFile.absolutePath}"
                        )

                        Log.d(
                            "VIDEO",
                            "Size: ${processedFile.length() / 1024 / 1024} MB"
                        )

                        // Add processed video
                        uploadReverseMediaList.add(
                            processedFile
                        )

                        videos.add(
                            processedFile.toUri()
                        )

                        Log.d(
                            "VIDEO",
                            "Video ready for upload"
                        )

                    } catch (e: Exception) {

                        Log.e(
                            "VIDEO",
                            "Video processing failed",
                            e
                        )
                    } finally {

                        isProcessingVideo = false
                    }
                }
            }
        }

        currentVideoUri = null
    }

    val cameraAddPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedAddPhoto?.let { file ->
                val imageUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                addPhotoImages.add(imageUri)
                Log.d("cameraAddPhotoLauncher", "Image saved: ${file.absolutePath}")
            }
        }
    }
    val permissionLauncherForAddPhoto = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if(!granted){
            hasCameraPermission = false
            return@rememberLauncherForActivityResult
        }

        hasCameraPermission = true

        val file = createImageFile(context)

        capturedAddPhoto = file

        val uri = FileProvider.getUriForFile(
            context, "${context.packageName}.provider", file
        )

        cameraAddPhotoLauncher.launch(uri)

    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->

        if (!granted) {

            hasCameraPermission = false

            Log.e(
                "CAMERA",
                "Camera permission denied"
            )

            return@rememberLauncherForActivityResult
        }

        // IMPORTANT
        hasCameraPermission = true

        when (pendingMediaType) {

            "PHOTO" -> {

                try {

                    val file = createImageFile(context)

                    capturedImageFile = file


                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )

                    cameraLauncher.launch(uri)

                } catch (e: Exception) {

                    Log.e(
                        "CAMERA",
                        "Failed to open photo camera",
                        e
                    )
                }
            }

            "VIDEO" -> {

                try {

                    val videoFile = createVideoFile(context)

//                    uploadReverseMediaList.add(videoFile)

                    val videoUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        videoFile
                    )

                    currentVideoUri = videoUri

                    videoLauncher.launch(videoUri)

                } catch (e: Exception) {

                    Log.e(
                        "VIDEO",
                        "Failed to open video camera",
                        e
                    )
                }
            }
        }

        pendingMediaType = null
    }

    LaunchedEffect(hasCamera) {

        if (
            hasCamera &&
            !hasCameraPermission
        ) {

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
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



    var trackNoInput by rememberSaveable { mutableStateOf("") }
    var orderIdInput by rememberSaveable { mutableStateOf("") }
    var orderIdInput1 by rememberSaveable { mutableStateOf("") }
    var orderIdInput2 by rememberSaveable { mutableStateOf("") }

    val itemDescAndStatuses = remember { mutableStateListOf(ItemDescription()) }

    val statusSuggestions = listOf(
        "Good",
        "Damage",
        "Missing Item"
    )
    var customerName by remember {
        mutableStateOf<String?>(null)
    }
    var customerCd by remember {
        mutableStateOf<String?>(null)
    }

    var branchName by remember {
        mutableStateOf<String?>(null)
    }

    var branchCd by remember {
        mutableStateOf<String?>(null)
    }


    LaunchedEffect(Unit) {
        viewModel.getCustomers()
    }

    LaunchedEffect(customerCd) {
        customerCd?.let {
            Log.d("customerCd", customerCd.toString())
            viewModel.getCourier(it)
        }
    }

        /*
         * ==========================================
         * HEADER
         * ==========================================
         */
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
                            viewModel.clear()
                            onBack()
                            //clear the printerData
                            viewModel.clearGeneratedResponses()
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
                            text = "Reverse/Awaiting Return",
                            color = Color(0xFF000000),
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isSmallScreen) 15.sp
                            else 14.sp,

                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, false)
                        )
                    }
                })
        },
    ) {
        paddingValues ->

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

                                    viewModel.setLastScannedTrackNo(result)
                                    trackNoInput = result
//                            playSound(R.raw.beep)
//                                    viewModel.getItemsByOrderId(result)
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
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // =========================
                                    // TRACKING NUMBER SCANNER
                                    // =========================
                                    OutlinedTextField(
                                        value = trackNoInput,
                                        onValueChange = { trackNoInput = it },
                                        label = {
                                            Text(
                                                text = "Scan Tracking No.",
                                                fontSize = if (isSmallScreen) 12.sp else 14.sp
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                            .focusRequester(focusRequester)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // =========================
                                    // ORDER ID 1
                                    // =========================
                                    OutlinedTextField(
                                        value = orderIdInput,
                                        onValueChange = {
                                            orderIdInput = it
                                        },
                                        label = {
                                            Text(
                                                text = "Enter Order ID 1",
                                                fontSize = if (isSmallScreen) 12.sp else 14.sp
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // =========================
                                    // ORDER ID 2
                                    // =========================
                                    OutlinedTextField(
                                        value = orderIdInput1,
                                        onValueChange = {
                                            orderIdInput1 = it
                                        },
                                        label = {
                                            Text(
                                                text = "Enter Order ID 2",
                                                fontSize = if (isSmallScreen) 12.sp else 14.sp
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // =========================
                                    // ORDER ID 3
                                    // =========================
                                    OutlinedTextField(
                                        value = orderIdInput2,
                                        onValueChange = {
                                            orderIdInput2 = it
                                        },
                                        label = {
                                            Text(
                                                text = "Enter Order ID 3",
                                                fontSize = if (isSmallScreen) 12.sp else 14.sp
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // =========================
                                    // CUSTOMER + COURIER
                                    // =========================
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {

                                        StatusDropdownField(
                                            label = "Customer",
                                            value = customerName,
                                            suggestions = customerSuggestions,
                                            onValueChange = {it ->
                                                customerName = it
                                                customerCd = customers.entries.find { it.value == customerName }?.key
                                            },
                                            modifier = Modifier.weight(1f)
                                        )

                                        StatusDropdownField(
                                            label = "Courier",
                                            value = branchName,
                                            suggestions = courierSuggestions,
                                            onValueChange = { it ->
                                                branchName = it
                                                branchCd = couriers.entries.find { it.value == branchName }?.key
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))











                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {

                                        // Header + Add Button
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {

                                            Text(
                                                text = "Item Description",
                                                fontSize = if (isSmallScreen) 14.sp else 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            IconButton(
                                                onClick = {
                                                    itemDescAndStatuses.add(
                                                        ItemDescription()
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add Item Description"
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Item Description + Status Fields
                                        itemDescAndStatuses.forEachIndexed { index, item ->

                                            Column(
                                                modifier = Modifier.fillMaxWidth()
                                            ) {

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.Top
                                                ) {

                                                    // =========================
                                                    // ITEM DESCRIPTION
                                                    // =========================
                                                    OutlinedTextField(
                                                        value = item.description,
                                                        onValueChange = { newValue ->
                                                            itemDescAndStatuses[index] =
                                                                item.copy(description = newValue)
                                                        },
                                                        label = {
                                                            Text(
                                                                text = "Item Description ${index + 1}",
                                                                fontSize = if (isSmallScreen) 12.sp else 14.sp
                                                            )
                                                        },
                                                        singleLine = false,
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .heightIn(min = 80.dp)
                                                    )

                                                    Spacer(modifier = Modifier.width(8.dp))

                                                    // =========================
                                                    // STATUS
                                                    // =========================
                                                    StatusDropdownField(
                                                        label = "Status",
                                                        value = item.status,
                                                        suggestions = statusSuggestions,
                                                        onValueChange = { newValue ->
                                                            itemDescAndStatuses[index] =
                                                                item.copy(status = newValue ?: "")
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                    )

                                                    // =========================
                                                    // DELETE
                                                    // =========================
                                                    if (itemDescAndStatuses.size > 1) {
                                                        IconButton(
                                                            onClick = {
                                                                itemDescAndStatuses.removeAt(index)
                                                            }
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Delete,
                                                                contentDescription = "Remove Item Description"
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                        }
                                    }











                                }
                            }
                        }


                    }


                    // =====================================
                    // PROCESS 2
                    // =====================================

                    1 -> {
                        //check kung may na scan na
                        //pag meron at may na approve(sretNo) di na ilabas yung add photo na buttons
                        val product = scannedState.rtvItemModelList.firstOrNull {
                            it.itemNo == 1
                        }
                        if(product?.sretNo == null){
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 0.dp)
                                    .height(screenHeight * 0.08f),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {

                                // Photo Button
                                Button(
                                    onClick = {

                                        pendingMediaType = "PHOTO"

                                        permissionLauncher.launch(
                                            Manifest.permission.CAMERA
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1F92E0), contentColor = Color.White
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 4.dp
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add, contentDescription = "Add Photo"
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Photo", fontWeight = FontWeight.SemiBold
                                    )
                                }

                                // Video Button
                                Button(
                                    onClick = {
                                        pendingMediaType = "VIDEO"
                                        permissionLauncher.launch(
                                            Manifest.permission.CAMERA
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1F92E0), contentColor = Color.White
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 4.dp
                                    )
                                ) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Video")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Video", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }



                        if(scannedState.rtvItemModelList.isNotEmpty()){
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                                    .height(screenHeight * 0.6f)
                            ) {
                                items(
                                    scannedState.rtvItemModelList,
                                    key = { "${it.revImages}_${it.itemNo}" }) { product ->


                                    if (!product.revImages.isNullOrEmpty()) {

                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {

                                            itemsIndexed(product.revImages) { index, media ->

                                                val imageUrl =
                                                    "${ApiConfig.BASE_URL}ImagePDFViewerServlet" +
                                                            "?type=reverse_image" +
                                                            "&sret_no=${product.sretNo}" +
                                                            "&index=$index"

                                                val isVideo = media.endsWith(".mp4", ignoreCase = true) ||
                                                        media.endsWith(".mov", ignoreCase = true) ||
                                                        media.endsWith(".avi", ignoreCase = true) ||
                                                        media.endsWith(".mkv", ignoreCase = true)

                                                val imageRequest = remember(imageUrl) {
                                                    ImageRequest.Builder(context)
                                                        .data(imageUrl)
                                                        .size(600)
                                                        .crossfade(true)
                                                        .build()
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .height(150.dp)
                                                        .aspectRatio(1f)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .clickable {

                                                            selectedMediaUrl = imageUrl

                                                            selectedMediaType =
                                                                if (isVideo) "video"
                                                                else "image"
                                                        }
                                                ) {

                                                    AsyncImage(
                                                        model = imageRequest,
                                                        imageLoader = imageLoader,
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )

                                                    // Video indicator
                                                    if (isVideo) {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .background(Color.Black.copy(alpha = 0.25f)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.PlayArrow,
                                                                contentDescription = "Play video",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(48.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    } else {

                                        Text(
                                            "No Images",
                                            color = Color(0xFF5F6368),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        if(images.isNotEmpty() || videos.isNotEmpty()){
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE5E5E5))
                                    .padding(8.dp)
                            ) {

                                Text(
                                    text = "Captured Media",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(
                                            max = screenHeight * .5f
                                        )
                                ) {

                                    // =========================
                                    // IMAGES
                                    // =========================

                                    items(images) { imageUri ->

                                        Box(
                                            modifier = Modifier
                                                .aspectRatio(1f)
                                                .clip(
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .background(Color.LightGray)
                                                .clickable {

                                                    selectedImageUri = imageUri

                                                }
                                        ) {

                                            AsyncImage(
                                                model = imageUri,
                                                imageLoader = imageLoader,
                                                contentDescription = "Captured Image",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )

                                            // Image icon
                                            Icon(
                                                imageVector = Icons.Filled.Image,
                                                contentDescription = "Photo",
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(6.dp)
                                            )

                                            // DELETE BUTTON
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(3.dp)
                                                    .size(30.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        Color.Black.copy(alpha = 0.55f)
                                                    )
                                                    .clickable {
                                                        mediaToDelete = imageUri
                                                        mediaTypeToDelete = "image"
                                                        showDeleteDialog = true
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Delete,
                                                    contentDescription = "Delete image",
                                                    tint = Color.Red,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }


                                    // =========================
                                    // VIDEOS
                                    // =========================

                                    items(videos) { videoUri ->

                                        Box(
                                            modifier = Modifier
                                                .aspectRatio(1f)
                                                .clip(
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .background(Color.DarkGray)
                                                .clickable {

                                                    selectedVideoUri = videoUri

                                                }
                                        ) {

                                            AsyncImage(
                                                model = videoUri,
                                                imageLoader = imageLoader,
                                                contentDescription = "Video Thumbnail",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )

                                            // Play icon
                                            Icon(
                                                imageVector = Icons.Filled.PlayCircle,
                                                contentDescription = "Play Video",
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .align(Alignment.Center)
                                            )

                                            // Video icon
                                            Icon(
                                                imageVector = Icons.Filled.Videocam,
                                                contentDescription = "Video",
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(6.dp)
                                            )

                                            // DELETE BUTTON
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(3.dp)
                                                    .size(30.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        Color.Black.copy(alpha = 0.55f)
                                                    )
                                                    .clickable {
                                                        mediaToDelete = videoUri
                                                        mediaTypeToDelete = "video"
                                                        showDeleteDialog = true
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Delete,
                                                    contentDescription = "Delete video",
                                                    tint = Color.Red,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (showDeleteDialog) {

                                AlertDialog(
                                    onDismissRequest = {

                                        showDeleteDialog = false
                                        mediaToDelete = null
                                        mediaTypeToDelete = ""
                                    },

                                    title = {
                                        Text(
                                            text = "Delete media?"
                                        )
                                    },

                                    text = {
                                        Text(
                                            text = "Are you sure you want to delete this " +
                                                    "$mediaTypeToDelete?"
                                        )
                                    },

                                    confirmButton = {

                                        TextButton(
                                            onClick = {

                                                mediaToDelete?.let { uri ->

                                                    // =====================================
                                                    // DELETE PHYSICAL FILE
                                                    // =====================================

                                                    val deleted =
                                                        deleteMediaFile(
                                                            context = context,
                                                            uri = uri
                                                        )

                                                    Log.d(
                                                        "MEDIA_DELETE",
                                                        "File deleted: $deleted"
                                                    )


                                                    // =====================================
                                                    // REMOVE FROM UI + UPLOAD LIST
                                                    // =====================================

                                                    when (mediaTypeToDelete) {

                                                        "image" -> {

                                                            images.remove(uri)

                                                            Log.d(
                                                                "MEDIA_DELETE",
                                                                "Image removed: $uri"
                                                            )
                                                        }

                                                        "video" -> {

                                                            videos.remove(uri)

                                                            /*
                                                             * Remove the corresponding File
                                                             * from uploadReverseMediaList.
                                                             */
                                                            uploadReverseMediaList.removeAll { file ->

                                                                file.toUri() == uri

                                                            }

                                                            Log.d(
                                                                "MEDIA_DELETE",
                                                                "Video removed: $uri"
                                                            )
                                                        }
                                                    }
                                                }

                                                // Reset dialog state
                                                mediaToDelete = null
                                                mediaTypeToDelete = ""
                                                showDeleteDialog = false
                                            }
                                        ) {

                                            Text(
                                                text = "Delete"
                                            )
                                        }
                                    },

                                    dismissButton = {

                                        TextButton(
                                            onClick = {

                                                mediaToDelete = null
                                                mediaTypeToDelete = ""
                                                showDeleteDialog = false
                                            }
                                        ) {

                                            Text(
                                                text = "Cancel"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                /*
                 * BACK
                 */

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


                /*
                 * NEXT
                 */

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
                    val scope = rememberCoroutineScope()
                    Button(
                        onClick = {
                            val trackingNo = viewModel.lastScannedTrackNo.value
                            val multipartFile = capturedAddPhoto?.let {createMultipartFromFile(it)}
                            val reverseMediaList: List<MultipartBody.Part> =
                                uploadReverseMediaList.map {
                                    Log.d(
                                        "UPLOAD",
                                        "File: ${it.absolutePath}, exists=${it.exists()}, size=${it.length()}"
                                    )
                                    createMultipartFromFile(it)
                                }

                            val mediaNames = uploadReverseMediaList.map {
                                it.name
                            }

                            Log.d("ProcessScreen", multipartFile.toString())
                            Log.d("ProcessScreen", reverseMediaList.size.toString())

                            Log.d("reverseMediaList.size", reverseMediaList.size.toString())

                            scope.launch{
                                val result = viewModel.saveReturnsMonitoring(
                                    trackingNo,
                                    orderIdInput,
                                    orderIdInput1,
                                    orderIdInput2,
                                    customerCd,
                                    branchCd,
                                    itemDescAndStatuses,
                                    mediaNames,
                                    reverseMediaList
                                )
                                Log.d(
                                    "ProcessScreen",
                                    "Save result: $result"
                                )

                                if (result) {
                                    Log.d("ProcessScreen", "Successfully saved")
                                } else {
                                    Log.e("ProcessScreen", "Failed to save")
                                }
                            }

                        },
                        enabled = images.isNotEmpty() || videos.isNotEmpty() && approvedSet.isNotEmpty(),
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
                permissionLauncherForAddPhoto.launch(Manifest.permission.CAMERA)
//                showAttachmentDialog = false
            },
            images = addPhotoImages
        )
    }

    selectedImageUri?.let { imageUri ->

        Dialog(
            onDismissRequest = {
                selectedImageUri = null
            }
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {

                AsyncImage(
                    model = imageUri,
                    contentDescription = "Captured Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                Button(
                    onClick = {
                        selectedImageUri = null
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
        }
    }

    selectedVideoUri?.let { videoUri ->

        Dialog(
            onDismissRequest = {
                selectedVideoUri = null
            }
        ) {

            VideoPreviewDialog(
                videoUri = videoUri,
                onClose = {
                    selectedVideoUri = null
                }
            )
        }
    }
    selectedMediaUrl?.let { url ->

        if (selectedMediaType == "image") {

            ImagePreviewDialog(
                imageUrl = url,
                onClose = {
                    selectedMediaUrl = null
                    selectedMediaType = null
                }
            )

        } else if (selectedMediaType == "video") {

            VideoPreviewDialog(
                videoUri = url.toUri(),
                onClose = {
                    selectedMediaUrl = null
                    selectedMediaType = null
                }
            )
        }
    }

    if (isProcessingVideo) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    enabled = false,
                    onClick = {}
                ),
            contentAlignment = Alignment.Center
        ) {

            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = 32.dp,
                            vertical = 24.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator()

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "Processing video...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Please wait",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

}


@Composable
fun ProcessIndicator(
    currentStep: Int,
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
                        text = if(step == 0) {"Fill Up Form"} else {"Upload Images/Videos"},
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


@Composable
fun VideoPreviewDialog(
    videoUri: Uri,
    onClose: () -> Unit,
) {

    val context = LocalContext.current

    val exoPlayer = remember(videoUri) {

        ExoPlayer.Builder(context)
            .build()
            .apply {

                setMediaItem(
                    MediaItem.fromUri(videoUri)
                )

                prepare()
            }
    }

    DisposableEffect(exoPlayer) {

        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        AndroidView(
            factory = {

                PlayerView(context).apply {

                    player = exoPlayer

                    useController = true

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = {
                exoPlayer.pause()
                onClose()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
}

@Composable
fun ImagePreviewDialog(
    imageUrl: String,
    onClose: () -> Unit,
) {

//    val context = LocalConte000000000xt.current

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Dialog(
        onDismissRequest = onClose
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Preview",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offsetX
                        translationY = offsetY
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->

                            scale = (scale * zoom)
                                .coerceIn(1f, 5f)

                            offsetX += pan.x
                            offsetY += pan.y
                        }
                    }
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}









@Composable
fun StatusDropdownField(
    label: String,
    value: String?,
    suggestions: List<String>,
    onValueChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                showDialog = true
            }
    ) {

        OutlinedTextField(
            value = value ?: "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = label,
                    fontSize = 13.sp
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color(0xFF212121),
                disabledLabelColor = Color(0xFF757575),
                disabledBorderColor = Color(0xFFBDBDBD),
                disabledContainerColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontSize = 12.sp
            )
        )
    }

    if (showDialog) {

        StatusSelectionDialog(
            statuses = suggestions,
            selectedStatus = value,
            onStatusSelected = { selectedStatus ->

                onValueChange(selectedStatus)

                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }


}


@Composable
private fun StatusSelectionDialog(
    statuses: List<String>,
    selectedStatus: String?,
    onStatusSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Select Status",
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                items(
                    items = statuses
                ) { status ->

                    TextButton(
                        onClick = {
                            onStatusSelected(status)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {

                        Text(
                            text = status,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 8.dp
                                ),
                            fontSize = 14.sp,
                            fontWeight = if (
                                status == selectedStatus
                            ) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            color = Color(0xFF212121)
                        )
                    }
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}


private fun deleteMediaFile(
    context: Context,
    uri: Uri
): Boolean {

    try {

        Log.d(
            "MEDIA_DELETE",
            "Deleting: $uri"
        )

        /*
         * ============================================
         * FILE URI / FILEPROVIDER
         * ============================================
         */

        val path = uri.path

        if (path != null) {

            val file = File(path)

            if (file.exists()) {

                val deleted = file.delete()

                Log.d(
                    "MEDIA_DELETE",
                    "File.delete(): $deleted"
                )

                return deleted
            }
        }


        /*
         * ============================================
         * CONTENT URI
         * ============================================
         */

        val deleted =
            context.contentResolver.delete(
                uri,
                null,
                null
            )

        Log.d(
            "MEDIA_DELETE",
            "ContentResolver.delete(): $deleted"
        )

        return deleted > 0

    } catch (e: Exception) {

        Log.e(
            "MEDIA_DELETE",
            "Error deleting media: $uri",
            e
        )

        return false
    }
}