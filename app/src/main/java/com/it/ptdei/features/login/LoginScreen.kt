package com.it.ptdei.features.login

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AnimatedCircleBackground() {

    val transition = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        transition.animateTo(
            targetValue = 1f, animationSpec = tween(
                durationMillis = 2000, easing = androidx.compose.animation.core.FastOutSlowInEasing
            )
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {

        fun lerp(start: Offset, end: Offset, t: Float): Offset {
            return Offset(
                x = start.x + (end.x - start.x) * t, y = start.y + (end.y - start.y) * t
            )
        }

        data class CircleAnim(
            val start: Offset,
            val end: Offset,
            val radius: Float,
            val color: Color,
        )

        val circles = listOf(
            CircleAnim(
                start = Offset(-200f, -200f),
                end = Offset(size.width * 0.25f, size.height * 0.25f),
                radius = 200f,
                color = Color(0xFF2962FF).copy(alpha = 1f)
            ), CircleAnim(
                start = Offset(size.width + 200f, -200f),
                end = Offset(size.width * 0.85f, size.height * 0.25f),
                radius = 150f,
                color = Color(0xFF13C03A).copy(alpha = 1f)
            ), CircleAnim(
                start = Offset(-200f, size.height + 200f),
                end = Offset(size.width * 0.15f, size.height * 0.75f),
                radius = 270f,
                color = Color(0xFFFF4757).copy(alpha = 1f)
            ), CircleAnim(
                start = Offset(size.width + 200f, size.height + 200f),
                end = Offset(size.width * 0.80f, size.height * 0.70f),
                radius = 180f,
                color = Color(0xFFFFF647).copy(alpha = 1f)
            )
        )

        circles.forEach { circle ->

            val currentCenter = lerp(
                circle.start, circle.end, transition.value
            )

            drawCircle(
                color = circle.color, radius = circle.radius * transition.value, // grows in
                center = currentCenter
            )
        }

        // optional subtle dots (fade in)
        drawCircle(
            color = Color.Gray.copy(alpha = 1f * transition.value),
            radius = 2f,
            center = Offset(size.width * 0.5f, size.height * 0.5f)
        )
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit,
) {


//    var username by remember { mutableStateOf("kdd2026145") }
//    var password by remember { mutableStateOf("k") }
    var username by remember { mutableStateOf("tae2025365") }
    var password by remember { mutableStateOf("tae2025365") }
//    var username by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedBusinessUnit by remember {
        mutableStateOf("HQ")
    }

    val businessUnits = listOf("HQ")

    val state by viewModel.loginState.collectAsState()

    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        visible = true
    }

    LaunchedEffect(state) {
        if (state is LoginViewModel.LoginState.LoginSuccess) {
            onLoginSuccess()
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
    ) {

        AnimatedVisibility(
            visible = visible, enter = fadeIn(
                animationSpec = tween(700)
            ) + scaleIn(
                initialScale = 0.97f, animationSpec = tween(700)
            )

        ) {


            AnimatedCircleBackground()

            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {


                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(.80f)
                ) {


                    val configuration = LocalConfiguration.current

                    val screenWidth = configuration.screenWidthDp.dp

                    val isSmallScreen = screenWidth < 361.dp

                    val isTablet = maxWidth > 600.dp

                    val cardWidth = when {
                        isTablet -> 420.dp
                        else -> Dp.Unspecified
                    }

                    val horizontalPadding = when {
                        isSmallScreen -> 16.dp
                        else -> 24.dp
                    }

                    val verticalPadding = when {
                        isSmallScreen -> 20.dp
                        else -> 32.dp
                    }

                    val titleFont = when {
                        isSmallScreen -> 28.sp
                        else -> 34.sp
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),

                        verticalArrangement = Arrangement.Center,

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Card(
                            modifier = Modifier
                                .then(
                                    if (isTablet) Modifier.width(cardWidth)
                                    else Modifier.fillMaxWidth()
                                )
                                .padding(
                                    horizontal = if (isSmallScreen) 16.dp
                                    else 24.dp,

                                    vertical = if (isSmallScreen) 24.dp
                                    else 70.dp
                                ),

                            shape = RoundedCornerShape(16.dp),

                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 3.dp
                            ),

                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                            )
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = horizontalPadding, vertical = verticalPadding
                                    )
                            ) {

                                // ---------------- TITLE ----------------

                                Text(
                                    text = "PTDEI",

                                    fontSize = titleFont,

                                    fontWeight = FontWeight.Bold,

                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Sign in to continue",

                                    fontSize = if (isSmallScreen) 13.sp
                                    else 14.sp,

                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(
                                    modifier = Modifier.height(
                                        if (isSmallScreen) 24.dp
                                        else 32.dp
                                    )
                                )

                                // ---------------- USERNAME ----------------

                                OutlinedTextField(
                                    value = username,

                                    onValueChange = {
                                        username = it
                                    },

                                    singleLine = true,

                                    modifier = Modifier.fillMaxWidth(),

                                    label = {
                                        Text("Username")
                                    },

                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null
                                        )
                                    },

                                    shape = RoundedCornerShape(12.dp),

                                    textStyle = TextStyle(
                                        fontSize = if (isSmallScreen) 14.sp
                                        else 16.sp
                                    ),

                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,

                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(
                                            alpha = 0.5f
                                        )
                                    )
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                // ---------------- PASSWORD ----------------

                                OutlinedTextField(
                                    value = password,

                                    onValueChange = {
                                        password = it
                                    },

                                    singleLine = true,

                                    modifier = Modifier.fillMaxWidth(),

                                    label = {
                                        Text("Password")
                                    },

                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null
                                        )
                                    },

                                    visualTransformation = if (passwordVisible) VisualTransformation.None
                                    else PasswordVisualTransformation(),

                                    trailingIcon = {

                                        IconButton(
                                            onClick = {
                                                passwordVisible = !passwordVisible
                                            }) {

                                            Icon(
                                                imageVector = if (passwordVisible) Icons.Default.Visibility
                                                else Icons.Default.VisibilityOff,

                                                contentDescription = null
                                            )
                                        }
                                    },

                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done
                                    ),

                                    keyboardActions = KeyboardActions(
                                        onDone = {

                                            viewModel.login(
                                                username = username,
                                                password = password,
                                                busUnit = selectedBusinessUnit
                                            )
                                        }),

                                    shape = RoundedCornerShape(12.dp),

                                    textStyle = TextStyle(
                                        fontSize = if (isSmallScreen) 14.sp
                                        else 16.sp
                                    ),

                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,

                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(
                                            alpha = 0.5f
                                        )
                                    ))

                                Spacer(modifier = Modifier.height(18.dp))

                                // ---------------- BUSINESS UNIT ----------------

                                ExposedDropdownMenuBox(
                                    expanded = expanded,

                                    onExpandedChange = {
                                        expanded = !expanded
                                    }) {

                                    OutlinedTextField(
                                        value = selectedBusinessUnit,

                                        onValueChange = {},

                                        readOnly = true,

                                        modifier = Modifier
                                            .menuAnchor(
                                                type = MenuAnchorType.PrimaryNotEditable,
                                                enabled = true
                                            )
                                            .fillMaxWidth(),

                                        label = {
                                            Text("Business Unit")
                                        },

                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expanded
                                            )
                                        },

                                        shape = RoundedCornerShape(12.dp),

                                        textStyle = TextStyle(
                                            fontSize = if (isSmallScreen) 14.sp
                                            else 16.sp
                                        ),

                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,

                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(
                                                alpha = 0.5f
                                            )
                                        )
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expanded,

                                        onDismissRequest = {
                                            expanded = false
                                        }) {

                                        businessUnits.forEach { unit ->

                                            DropdownMenuItem(
                                                text = {
                                                    Text(unit)
                                                },

                                                onClick = {
                                                    selectedBusinessUnit = unit
                                                    expanded = false
                                                })
                                        }
                                    }
                                }

                                Spacer(
                                    modifier = Modifier.height(
                                        if (isSmallScreen) 22.dp
                                        else 28.dp
                                    )
                                )

                                // ---------------- LOGIN BUTTON ----------------
                                Button(
                                    onClick = {

                                        viewModel.login(
                                            username = username.trim(),
                                            password = password.trim(),
                                            busUnit = selectedBusinessUnit
                                        )
                                    },

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(
                                            if (isSmallScreen) 48.dp
                                            else 52.dp
                                        ),

                                    shape = RoundedCornerShape(12.dp),

                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50),
                                        contentColor = Color.White
                                    ),

                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 0.dp
                                    ),

                                    enabled = state !is LoginViewModel.LoginState.Loading
                                ) {

                                    if (state is LoginViewModel.LoginState.Loading) {

                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),

                                            strokeWidth = 2.dp,

                                            color = MaterialTheme.colorScheme.onPrimary
                                        )

                                    } else {

                                        Text(
                                            text = "Login",

                                            fontSize = if (isSmallScreen) 14.sp
                                            else 15.sp,

                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                // ---------------- ERROR ----------------

                                val errorState = state as? LoginViewModel.LoginState.Error

                                AnimatedVisibility(
                                    visible = errorState != null
                                ) {
                                    Column {
                                        Spacer(modifier = Modifier.height(14.dp))

                                        Text(
                                            text = errorState?.message.orEmpty(),
                                            color = MaterialTheme.colorScheme.error,
                                            fontSize = if (isSmallScreen) 12.sp else 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ---------------- LOADING OVERLAY ----------------

        if (state is LoginViewModel.LoginState.Loading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.45f)
                    ), contentAlignment = Alignment.Center
            ) {

                Card(
                    shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(
                            horizontal = 32.dp, vertical = 24.dp
                        ), horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator()

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Signing in...", style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}