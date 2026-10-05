package com.it.ptdei.menu.salesapproval.features.getOrders

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.it.ptdei.R
import com.it.ptdei.global.ApiConfig
import com.it.ptdei.global.FileCacheManager
import com.it.ptdei.global.ImageLoaderProvider
import com.it.ptdei.menu.salesapproval.OrdersDataModel
import com.it.ptdei.menu.salesapproval.SalesApprovalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient


@SuppressLint("UnusedBoxWithConstraintsScope", "ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    viewModel: SalesApprovalViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    okHttpClient: OkHttpClient,
) {

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isSmallScreen = screenWidth < 361.dp


    var uploadsPopUp by remember { mutableStateOf(false) }
    var productsPopUp by remember { mutableStateOf(false) }


    val productsResponse by viewModel.products.collectAsState()

    var selectedOrder by remember { mutableStateOf<OrdersDataModel?>(null) }
    var selectedOrderNo by remember { mutableStateOf("") }

    val cacheManager = remember {
        FileCacheManager(context, okHttpClient)
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

        Scaffold(
            containerColor = Color.Transparent, topBar = {
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
                                    text = "Sales Approval - Orders",

                                    color = Color(0xFFFFFFFF),

                                    fontWeight = FontWeight.Bold,

                                    fontSize = if (isSmallScreen) 12.sp
                                    else 18.sp,

                                    maxLines = 2,

                                    overflow = TextOverflow.Ellipsis,

                                    modifier = Modifier.weight(1f, false)
                                )
                            }
                        }
                    }, navigationIcon = {
                        IconButton(
                            onClick = {
                                onBack()
                            }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null
                            )
                        }
                    })
            }) { padding ->


            val ordersResponse by viewModel.orders.collectAsState()

            val orderState by viewModel.orderState.collectAsState()


            val currentPage by viewModel.currentPage.collectAsState()

            val totalPages by viewModel.totalPages.collectAsState()
            LaunchedEffect(Unit) {

                viewModel.getOrders()
            }

            when (orderState) {
                SalesApprovalViewModel.OrdersState.SessionExpired -> {

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

                SalesApprovalViewModel.OrdersState.Loading -> {

                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is SalesApprovalViewModel.OrdersState.Error -> {

                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (orderState as SalesApprovalViewModel.OrdersState.Error).message
                        )
                    }
                }

                else -> {

                    val searchText by viewModel.searchText.collectAsState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {

                            OutlinedTextField(
                                value = searchText, onValueChange = {
                                    viewModel.onSearchTextChanged(it)
                                }, modifier = Modifier.weight(1f), singleLine = true
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Button(
                                onClick = {
                                    viewModel.getOrders(1)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Search")
                            }
                        }

                        val pendingOrderApprovals by viewModel.pendingOrderApprovals.collectAsState()

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(12.dp)
                        ) {
                            items(
                                items = ordersResponse.orders, key = { it.orderNo }) { order ->

                                val state = pendingOrderApprovals[order.orderNo]

                                OrderCard(
                                    order = order,

                                    isApproved = state == SalesApprovalViewModel.ApprovalState.APPROVED,
                                    isDisapproved = state == SalesApprovalViewModel.ApprovalState.DISAPPROVED,

                                    onApproveChange = {
                                        viewModel.setOrderApproval(
                                            order.orderNo,
                                            SalesApprovalViewModel.ApprovalState.APPROVED
                                        )
                                        viewModel.printPendingOrderApprovals()

                                    },

                                    onDisapproveChange = {
                                        viewModel.setOrderApproval(
                                            order.orderNo,
                                            SalesApprovalViewModel.ApprovalState.DISAPPROVED
                                        )
                                        viewModel.printPendingOrderApprovals()

                                    },

                                    onViewUploadsClick = { order ->
                                        selectedOrder = order
                                        uploadsPopUp = true
                                    },

                                    onClick = {
                                        selectedOrderNo = order.orderNo
                                        productsPopUp = true
                                    },

                                    isSmallScreen = isSmallScreen)

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.submitOrderApprovals()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            enabled = !pendingOrderApprovals.isEmpty()
                        ) {
                            Text("Save Approvals")
                        }

                        PaginationBar(
                            currentPage = currentPage,
                            totalPages = totalPages,
                            onPrevious = {
                                viewModel.previousPage()
                            },
                            onNext = {
                                viewModel.nextPage()
                            })
                    }
                }
            }
        }
    }


    if (uploadsPopUp && selectedOrder != null) {

        selectedOrder?.let { order ->

            Dialog(
                onDismissRequest = {
                    selectedOrder = null
                    uploadsPopUp = false
                }) {

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .heightIn(max = 650.dp),
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 6.dp,
                    shadowElevation = 12.dp
                ) {

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        // ───────────────── HEADER ─────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Uploads",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = {
                                    selectedOrder = null
                                    uploadsPopUp = false
                                }) {
                                Icon(
                                    imageVector = Icons.Default.Close, contentDescription = "Close"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val imageLoader = remember {
                            ImageLoaderProvider.getPictures(context, okHttpClient)
                        }

                        val scope = rememberCoroutineScope()

                        // ───────────────── GRID ─────────────────
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {

                            itemsIndexed(order.imgFiles) { index, _ ->

                                val imageUrl =
                                    "${ApiConfig.BASE_URL}ImagePDFViewerServlet?type=s_order_po_image&po_no=${selectedOrder!!.poNo}&index=$index"

                                var isLoading by remember { mutableStateOf(true) }

                                Box(
                                    modifier = Modifier
                                        .aspectRatio(0.8f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.LightGray.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {

                                    AsyncImage(
                                        model = imageUrl,
                                        imageLoader = imageLoader,
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {

                                                Log.d("CLICK", "IMAGE CLICKED")

                                                scope.launch(Dispatchers.IO) {
                                                    try {
                                                        val cachedFile =
                                                            cacheManager.getOrDownloadFile(imageUrl)

                                                        withContext(Dispatchers.Main) {
                                                            openFile(context, cachedFile)
                                                        }

                                                    } catch (e: Exception) {
                                                        Log.e("CACHE", e.message ?: "error")
                                                    }
                                                }
                                            },

                                        error = painterResource(R.drawable.error_image),

                                        onLoading = { isLoading = true },
                                        onSuccess = { isLoading = false },
                                        onError = { isLoading = false })


                                    // ───────── LOADING OVERLAY ─────────
                                    if (isLoading) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(strokeWidth = 2.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (productsPopUp) {
        Log.d("selectedOrderNo", selectedOrderNo)
        selectedOrderNo.let { product ->
            Log.d("selectedOrderNo", selectedOrderNo)
            viewModel.getProductsByOrderNo(selectedOrderNo)

            Dialog(
                onDismissRequest = {
                    productsPopUp = false
                }, properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)   // 95% of screen width
                        .heightIn(max = 650.dp),
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 6.dp,
                    shadowElevation = 12.dp
                ) {

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        // ───────────────── HEADER ─────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Products",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = {
                                    productsPopUp = false
                                }) {
                                Icon(
                                    imageVector = Icons.Default.Close, contentDescription = "Close"
                                )
                            }
                        }

                        val pendingProductApprovals by viewModel.pendingProductApprovals.collectAsState()

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(12.dp)
                        ) {
                            items(
                                items = productsResponse.productList,
                                key = { "${it.orderNo}-${it.itemNo}" }) { product ->

                                Log.d("productsResponse.products", product.prodName)
                                val state = pendingProductApprovals[ApprovalKey(
                                    product.orderNo, product.itemNo
                                )]

                                ProductCard(
                                    products = product,

                                    isApproved = state == SalesApprovalViewModel.ApprovalState.APPROVED,
                                    isDisapproved = state == SalesApprovalViewModel.ApprovalState.DISAPPROVED,

                                    onApproveChange = {
                                        viewModel.setProductApproval(
                                            product.orderNo,
                                            product.itemNo,
                                            SalesApprovalViewModel.ApprovalState.APPROVED
                                        )
                                        viewModel.printPendingOrderApprovals()

                                    },

                                    onDisapproveChange = {
                                        viewModel.setProductApproval(
                                            product.orderNo,
                                            product.itemNo,
                                            SalesApprovalViewModel.ApprovalState.DISAPPROVED
                                        )
                                        viewModel.printPendingOrderApprovals()

                                    },
                                    isSmallScreen = isSmallScreen)

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                    }
                }
            }


        }


    }


}


@Composable
fun OrderCard(
    order: OrdersDataModel,
    onClick: () -> Unit,
    isApproved: Boolean,
    isDisapproved: Boolean,
    onApproveChange: () -> Unit,
    onDisapproveChange: () -> Unit,
    onViewUploadsClick: (OrdersDataModel) -> Unit,
    isSmallScreen: Boolean
) {

    val colors = MaterialTheme.colorScheme


    val borderColor = when {
        isApproved -> Color(0xFF2E7D32)
        isDisapproved -> Color(0xFFD32F2F)
        else -> Color(0xFFF8F9FA)
    }


    val panelColor = when {
        isApproved -> Color(0xFFCAFFCC)
        isDisapproved -> Color(0xFFFFC6CB)
        else -> Color(0xFFF8F9FA)
    }

    val panelColor2 = when {
        isApproved -> Color(0xFFABFFAC)
        isDisapproved -> Color(0xFFFF969B)
        else -> Color(0xFFF1F1F1)
    }



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = panelColor
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            1.dp, borderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        onClick = {
            Log.d("CLICK", "Card clicked")
            onClick()
        }
    ) {

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(0.70f)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Customer Info
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = order.custName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = order.branchName,
                            fontSize = 13.sp,
                            color = colors.onSurfaceVariant
                        )
                    }

                    // Upload Images Button
                    FilledIconButton(
                        onClick = {
                            onViewUploadsClick(order)
                        }, colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFFE3F2FD), contentColor = Color(0xFF119BEC)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image, contentDescription = "View Images"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(
                    thickness = 1.dp, color = Color(0xFFEAEAEA)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if(isSmallScreen){
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {


                        Column{

                            Text(
                                text = "ORDER NO",
                                color = Color.Gray,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = order.orderNo,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = colors.onSurface
                            )


                            Text(
                                text = "PO NO",
                                color = Color.Gray,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = order.poNo,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = colors.onSurface
                            )
                        }

                        Spacer(Modifier.height(4.dp))
                        // Date Information
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(colors.surfaceContainerHighest)
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {

                            order.orderDate?.let {
                                Text(
                                    text = "📅 Order: $it",
                                    fontSize = 12.sp,
                                    color = colors.onSurfaceVariant
                                )
                            }

                            order.delDate?.let {
                                Text(
                                    text = "🚚 Delivery: $it",
                                    fontSize = 12.sp,
                                    color = colors.onSurfaceVariant
                                )
                            }

                            order.cancelDate?.let {
                                Text(
                                    text = "❌ Cancel: $it",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }


                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {


                        Column{

                            Text(
                                text = "ORDER NO",
                                color = Color.Gray,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = order.orderNo,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = colors.onSurface
                            )


                            Text(
                                text = "PO NO",
                                color = Color.Gray,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = order.poNo,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = colors.onSurface
                            )
                        }


                        // Date Information
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.surfaceContainerHighest)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {

                            order.orderDate?.let {
                                Text(
                                    text = "📅 Order: $it",
                                    fontSize = 12.sp,
                                    color = colors.onSurfaceVariant
                                )
                            }

                            order.delDate?.let {
                                Text(
                                    text = "🚚 Delivery: $it",
                                    fontSize = 12.sp,
                                    color = colors.onSurfaceVariant
                                )
                            }

                            order.cancelDate?.let {
                                Text(
                                    text = "❌ Cancel: $it",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }


                    }
                }


            }


            // RIGHT SIDE
            Column(
                modifier = Modifier
                    .weight(0.30f)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(panelColor2)
                    .padding(8.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top
            ) {


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {


                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when {
                            isApproved -> "APPROVED"
                            isDisapproved -> "REJECTED"
                            else -> "PENDING"
                        },
                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = when {
                            isApproved -> Color(0xFF2E7D32)
                            isDisapproved -> Color(0xFFD32F2F)
                            else -> Color.Gray
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        onClick = onApproveChange,
                        shape = RoundedCornerShape(12.dp),
                        color = if (isApproved) Color(0xFF2E7D32)
                        else Color.White,
                        border = BorderStroke(
                            1.dp, Color(0xFF2E7D32)
                        ),
                        modifier = Modifier.fillMaxSize(0.98f)
                    ) {

                        Row(
                            modifier = Modifier.padding(
                                horizontal = 1.dp,
                                vertical = 8.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {

                            RadioButton(
                                selected = isApproved, onClick = onApproveChange
                            )
                            Text(
                                if(!isSmallScreen) "Approve" else "A",
                                color = if (isApproved) Color.White else Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        onClick = onDisapproveChange,
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDisapproved) Color(0xFFD32F2F)
                        else Color.White,
                        border = BorderStroke(
                            1.dp, Color(0xFFD32F2F)
                        ),
                        modifier = Modifier.fillMaxSize(0.98f)
                    ) {

                        Row(
                            modifier = Modifier.padding(
                                horizontal = 1.dp,
                                vertical = 8.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {

                            RadioButton(
                                selected = isDisapproved, onClick = onDisapproveChange
                            )
                            Text(
                                if(!isSmallScreen) "Reject" else "R",
                                color = if (isDisapproved) Color.White else Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                        }
                    }

                }

            }

        }

    }
}


@Composable
fun ProductCard(
    products: ProductsDataModel,
    isApproved: Boolean,
    isDisapproved: Boolean,
    onApproveChange: () -> Unit,
    onDisapproveChange: () -> Unit,
    isSmallScreen: Boolean
) {

    val colors = MaterialTheme.colorScheme

    val borderColor = when {
        isApproved -> Color(0xFF2E7D32)
        isDisapproved -> Color(0xFFD32F2F)
        else -> Color(0xFFE0E0E0)
    }

    val panelColor = when {
        isApproved -> Color(0xFFF1FFF3)
        isDisapproved -> Color(0xFFFFF4F4)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp, borderColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = panelColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            // LEFT SIDE
            Column(
                modifier = Modifier
                    .weight(0.70f)
                    .padding(14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = products.prodName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 20.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = colors.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "SKU: ${products.sku}", fontSize = 12.sp, color = Color.Gray
                        )
                        Text(
                            text = "QTY: ${products.qty}", fontSize = 12.sp, color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE8E8E8).copy(alpha = 0.8f))
                            .padding(
                                horizontal = 4.dp, vertical = 2.dp
                            )
                    ) {

                        Text(
                            text = "Item No. " + products.itemNo.toString(),
                            fontSize = 11.sp,
                            color = Color.Black
                        )


                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider(
                    thickness = 1.dp, color = Color(0xFFE5E7EB)
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {


                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF9B9908).copy(alpha = 0.8f))
                            .padding(
                                horizontal = 8.dp, vertical = 4.dp
                            )
                    ) {

                        Text(
                            text = "Unit Price:", fontSize = 11.sp, color = Color.White
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "₱${products.unitPrice}",
                            fontSize = if(isSmallScreen) 10.sp else 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )


                    }


                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF23A80C).copy(alpha = 0.8f))
                            .padding(
                                horizontal = 8.dp, vertical = 4.dp
                            )
                    ) {

                        Text(
                            text = "Existing Price:", fontSize = 11.sp, color = Color.White
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "₱${products.unitPrice}",
                            fontSize = if(isSmallScreen) 10.sp else 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )


                    }



                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFEC1811).copy(alpha = 0.8f))
                            .padding(
                                horizontal = 8.dp, vertical = 4.dp
                            )
                    ) {

                        Text(
                            text = "Effective Date:", fontSize = 11.sp, color = Color.White
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = products.effectiveDate.toString(),
                            fontSize = if(isSmallScreen) 10.sp else 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )


                    }

                }
            }

            // RIGHT SIDE
            Column(
                modifier = Modifier
                    .weight(0.30f)
                    .padding(
                        top = 10.dp, end = 10.dp, bottom = 10.dp
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Color.White.copy(alpha = 0.55f)
                    )
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {


                Text(
                    text = when {
                        isApproved -> "APPROVED"
                        isDisapproved -> "REJECTED"
                        else -> "PENDING"
                    },
                    fontSize = 11.sp, fontWeight = FontWeight.Bold, color = when {
                        isApproved -> Color(0xFF2E7D32)
                        isDisapproved -> Color(0xFFD32F2F)
                        else -> Color.Gray
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Surface(
                        modifier = Modifier.fillMaxWidth(0.98f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isApproved) Color(0xFF2E7D32)
                        else Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(
                            1.dp, Color(0xFF2E7D32)
                        )
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {

                            RadioButton(
                                selected = isApproved, onClick = onApproveChange
                            )

                            Text(
                                text = if(!isSmallScreen) "Approve" else "A",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isApproved) Color.White
                                else Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(0.98f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDisapproved) Color(0xFFD32F2F)
                        else Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(
                            1.dp, Color(0xFFD32F2F)
                        )
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {

                            RadioButton(
                                selected = isDisapproved, onClick = onDisapproveChange
                            )

                            Text(
                                text = if(!isSmallScreen) "Reject" else "R",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDisapproved) Color.White
                                else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),

        horizontalArrangement = Arrangement.SpaceBetween,

        verticalAlignment = Alignment.CenterVertically
    ) {

        Button(
            enabled = currentPage > 1, onClick = onPrevious,
            shape = RoundedCornerShape(6.dp)
        ) {
            Text("Previous")
        }

        Text(
            text = "Page $currentPage of $totalPages", fontWeight = FontWeight.Bold
        )

        Button(
            enabled = currentPage < totalPages, onClick = onNext,
            shape = RoundedCornerShape(6.dp)
        ) {
            Text("Next")
        }


    }
}


fun openFile(
    context: Context,
    fileUrl: CachedUploadsFile,
) {

    Log.d("fileUrl", fileUrl.toString())

    val uri = FileProvider.getUriForFile(
        context, "${context.packageName}.provider", fileUrl.file
    )


    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, fileUrl.mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }


    context.startActivity(intent)
}



