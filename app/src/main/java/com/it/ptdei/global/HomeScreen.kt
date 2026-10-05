package com.it.ptdei.global

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.it.ptdei.features.login.LoginViewModel
import com.it.ptdei.features.logout.LogoutRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sessionManager: SessionManager,
    loginViewModel: LoginViewModel,
    onLogout: () -> Unit,
    onSalesApprovalClick: () -> Unit,
    onUnitEqApprovalClick: () -> Unit,
    onReturnsClick: () -> Unit,
    onReverseClick: () -> Unit,
    onCustomerPOClick: () -> Unit,
) {

    var isNavigating by remember {mutableStateOf(false)}
    val loginState by loginViewModel.loginState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val isAdmin = sessionManager.getAdmin()
    val userAccessMenu = sessionManager.getUserAccess()
    val displayName = sessionManager.getDisplayName() ?: "User"
    val initials = displayName.trim().split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").take(2)
    var currentTime by remember { mutableStateOf(getFormattedDateTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getFormattedDateTime()
            delay(1000L.milliseconds)
        }
    }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginViewModel.LoginState.LogoutSuccess -> {
                sessionManager.clearSession()
                loginViewModel.resetState()
                onLogout()
            }
            else -> Unit
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
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val wavePath = Path().apply {
                moveTo(0f, size.height * 0.72f)
                quadraticTo(size.width * 0.20f, size.height * 0.60f, size.width * 0.45f, size.height * 0.72f)
                quadraticTo(size.width * 0.75f, size.height * 0.88f, size.width, size.height * 0.70f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            val wavePaths = Path().apply {
                moveTo(0f, size.height * 0.15f)
                cubicTo(size.width * 0.25f, size.height * 0.02f,
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
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    actions = {
                        IconButton(
                            onClick = {
                                showLogoutDialog = true
                            }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout"
                            )
                        }
                    })
            }) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(
                                    Color(0xFF4CAF50).copy(.9f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials, fontSize = 24.sp, color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Welcome back",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = displayName, style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentTime,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if ("SALESAPPRVL" in userAccessMenu || isAdmin) {
                        item {
                            DashboardCard(title = "Sales Approval", icon = Icons.Default.AssignmentTurnedIn)
                            {
                                isNavigating = true

                                coroutineScope.launch {
                                    onSalesApprovalClick()
                                    isNavigating = false
                                }
                            }
                        }
                    }


                    if ("UNITEQAPPRVL" in userAccessMenu || isAdmin) {
                        item {
                            DashboardCard(title = "Unit Eq Approval", icon = Icons.Default.Category)
                            {
                                isNavigating = true
                                coroutineScope.launch {
                                    onUnitEqApprovalClick()
                                    isNavigating = false
                                }
                            }
                        }
                    }

                    if ("RETURNS" in userAccessMenu || isAdmin) {
                        item {
                            DashboardCard(title = "Returned Orders", icon = Icons.AutoMirrored.Filled.AssignmentReturn)
                            {
                                isNavigating = true
                                coroutineScope.launch {
                                    onReturnsClick()
                                    isNavigating = false
                                }
                            }
                        }
                    }

                    if ("REVERSE" in userAccessMenu || isAdmin) {
                        item {
                            DashboardCard(title = "Reverse Orders", icon = Icons.AutoMirrored.Filled.RotateLeft)
                            {
                                isNavigating = true
                                coroutineScope.launch {
                                    onReverseClick()
                                    isNavigating = false
                                }
                            }
                        }
                    }
                    if ("CUSTPO" in userAccessMenu || isAdmin) {
                        item {
                            DashboardCard(title = "Customer PO", icon = Icons.AutoMirrored.Filled.ReceiptLong)
                            {
                                isNavigating = true
                                coroutineScope.launch {
                                    onCustomerPOClick()
                                    isNavigating = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        Log.d("session username for logout", sessionManager.getUsername())
                        loginViewModel.logout(LogoutRequest(sessionManager.getUsername()))
                        onLogout()

                    },

                    ) {
                    Text("Yes", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            })
    }

    if (isNavigating) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.65f)
                ), contentAlignment = Alignment.Center
        ) {
            Card(shape = RoundedCornerShape(24.dp))
            {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 28.dp, vertical = 24.dp
                    ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Loading module...")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedDateTime(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy • hh:mm:ss a"))
}


@SuppressLint("ConfigurationScreenWidthHeight", "UnusedBoxWithConstraintsScope")
@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    val interactionSource = remember {MutableInteractionSource()}
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedColor by animateColorAsState(
        targetValue = if (isPressed) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else MaterialTheme.colorScheme.surface, label = ""
    )
    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) 0.dp
        else 2.dp, label = ""
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f
        else 1f, label = ""
    )

    BoxWithConstraints {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val isSmallScreen = screenWidth < 361.dp

        ElevatedCard(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    if (isSmallScreen) 70.dp
                    else 100.dp
                )
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = animatedColor),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = animatedElevation)
        ) {
            if (isSmallScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),

                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        lineHeight = 15.sp,
                        maxLines = 2,
                        fontSize = 14.sp
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}