package com.it.ptdei.menu.returns.ui

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.it.ptdei.global.ApiConfig
import com.it.ptdei.global.ImageLoaderProvider
import com.it.ptdei.menu.returns.TableCell
import com.it.ptdei.menu.returns.features.getItems.RTVItemModel
import okhttp3.OkHttpClient


@SuppressLint("UnusedBoxWithConstraintsScope", "ConfigurationScreenWidthHeight")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItemCard(
    item: RTVItemModel,
    isReturned: Boolean,
    onClicked: (Boolean) -> Unit,
    onDisapproveChange: (Boolean) -> Unit,
    okHttpClient: OkHttpClient,
) {

    val tableHeaderBg = Color(0xFFF1F3F4)
    val tableRowBg = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onSurface

    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    val isAlreadyReturned = !item.sretNo.isNullOrBlank()

    val isSelected = isReturned

//    val containerColor = Color(0xFFFFFAF3)

    val returnedContainerColor = MaterialTheme.colorScheme.background

    Log.d("COLOR", colors.primary.toString())

    val borderColor = when {

        isSelected -> Color(0xFF2381FC)

        isAlreadyReturned -> Color(0xFFF1C1C1)

        else -> Color.Transparent
    }

    val titleColor = when {

        isSelected -> Color.Black

        isAlreadyReturned -> Color(0xFF9A9191)

        else -> Color.Black
    }


    val subtitleColor = Color(0xFF5F6368)

    BoxWithConstraints {

        val configuration = LocalConfiguration.current

        val screenWidth = configuration.screenWidthDp.dp

        val isSmallScreen = screenWidth < 361.dp


        Box {


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable(
                        enabled = !isAlreadyReturned
                    ) {

                        val checked = !isSelected

                        onClicked(checked)

                        if (checked) {
                            onDisapproveChange(false)
                        }
                    },

                colors = (if (isSelected) {

                    CardDefaults.cardColors(
                        containerColor = Color(0xFFd9edff)
                    )

                } else if (item.sretNo.isNullOrBlank()) {
                    CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                } else {

                    CardDefaults.cardColors(
                        containerColor = returnedContainerColor
                    )
                }),

                shape = RoundedCornerShape(12.dp),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 6.dp else 2.dp
                ),

                border = BorderStroke(
                    1.5.dp, borderColor
                ),
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {

                    // =========================================================
                    // SMALL SCREEN LAYOUT
                    // =========================================================

                    if (isSmallScreen) {

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            // IMAGE
                            if (!item.imgFiles.isNullOrEmpty()) {

                                val imageLoader = remember {
                                    ImageLoaderProvider.getPictures(
                                        context, okHttpClient
                                    )
                                }

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    itemsIndexed(item.imgFiles) { index, _ ->

                                        val imageUrl = "${ApiConfig.BASE_URL}ImagePDFViewerServlet?type=product_image&prod_cd=${item.prodCd}&index=$index"

                                        val imageRequest = remember(imageUrl) {
                                            ImageRequest.Builder(context).data(imageUrl)
                                                .size(600) // 👈 key fix (downsample)
                                                .crossfade(true).build()
                                        }

                                        AsyncImage(
                                            model = imageRequest,

                                            imageLoader = imageLoader,

                                            contentDescription = null,

                                            contentScale = ContentScale.Crop,

                                            modifier = Modifier
                                                .height(150.dp)
                                                .aspectRatio(1f)
                                                .clip(
                                                    RoundedCornerShape(12.dp)
                                                )
                                        )
                                    }
                                }

                            } else {

                                Text(
                                    "No Images", color = subtitleColor, fontSize = 12.sp
                                )
                            }

                            // DETAILS
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {

                                Text(
                                    text = item.prodName ?: "-",

                                    fontSize = 15.sp,

                                    fontWeight = FontWeight.SemiBold,

                                    color = titleColor,

                                    maxLines = 3,

                                    overflow = TextOverflow.Ellipsis
                                )

                                if (!item.sretNo.isNullOrBlank()) {

                                    Text(
                                        "SRET: ${item.sretNo}",
                                        fontSize = 12.sp,
                                        color = Color(0xFFD32F2F)
                                    )
                                }

                                Text(
                                    "QTY: ${item.qty}", fontSize = 12.sp, color = subtitleColor
                                )

                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )


                                // TABLE HEADER
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(tableHeaderBg)
                                        .padding(vertical = 6.dp)
                                ) {

                                    TableCell(
                                        "Unit", 1f, true, textPrimary
                                    )

                                    TableCell(
                                        "Price", 1f, true, textPrimary
                                    )

                                    TableCell(
                                        "Discount", 1f, true, textPrimary
                                    )
                                }

                                // TABLE ROW
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(tableRowBg)
                                        .padding(vertical = 6.dp)
                                ) {

                                    TableCell(
                                        item.unitCd, 1f, false, textPrimary
                                    )

                                    TableCell(
                                        item.unitPrice.toString(), 1f, false, textPrimary
                                    )

                                    TableCell(
                                        item.unitPrice.toString(), 1f, false, textPrimary
                                    )
                                }
                            }
                        }

                    } else {

                        // =========================================================
                        // LARGE SCREEN LAYOUT
                        // =========================================================

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            // IMAGE
                            Column(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .clip(
                                        RoundedCornerShape(12.dp)
                                    )
                            ) {

                                if (!item.imgFiles.isNullOrEmpty()) {

                                    val imageLoader = remember {
                                        ImageLoaderProvider.getPictures(
                                            context, okHttpClient
                                        )
                                    }
                                    Log.d("IMG", "Count = ${item.imgFiles.size}")
                                    Log.d("IMG", "Files = ${item.imgFiles}")
                                    LazyColumn(
                                        modifier = Modifier
                                            .height(150.dp)
                                            .width(150.dp)
                                            .background(
                                                Color(0xFFF1F3F4)
                                            )
                                    ) {

                                        itemsIndexed(item.imgFiles) { index, _ ->

                                            val imageUrl = "${ApiConfig.BASE_URL}ImagePDFViewerServlet?type=product_image&prod_cd=${item.prodCd}&index=$index"

                                            val imageRequest = remember(imageUrl) {
                                                ImageRequest.Builder(context).data(imageUrl)
                                                    .crossfade(true).build()
                                            }

                                            AsyncImage(
                                                model = imageRequest,
                                                imageLoader = imageLoader,
                                                contentDescription = null,
                                                contentScale = ContentScale.FillHeight,
                                                modifier = Modifier
                                                    .fillMaxWidth()
//                                                .aspectRatio(1f)
                                                    .clip(
                                                        RoundedCornerShape(12.dp)
                                                    )
                                            )
                                        }
                                    }

                                } else {

                                    Text(
                                        "No Images", color = subtitleColor, fontSize = 12.sp
                                    )
                                }
                            }

                            // DETAILS
                            Column(
                                modifier = Modifier.weight(1.5f),

                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {

                                Text(
                                    item.prodName ?: "-",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = titleColor,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (!item.sretNo.isNullOrBlank()) {

                                    Text(
                                        "SRET: ${item.sretNo}",
                                        fontSize = 13.sp,
                                        color = Color(0xFFD32F2F)
                                    )
                                }
                                Text(
                                    "QTY: ${item.qty}", fontSize = 13.sp, color = subtitleColor
                                )

//                                modifier =
//                                    Modifier.height(6.dp)
//                            )
//                            Spacer(

                                Column {
                                    // TABLE HEADER
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                    ) {

                                        TableCell(
                                            "Unit", 1f, true, titleColor
                                        )

                                        TableCell(
                                            "Price", 1f, true, titleColor
                                        )

                                        TableCell(
                                            "Discount", 1f, true, titleColor
                                        )
                                    }

                                    // TABLE ROW
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                    ) {

                                        TableCell(
                                            item.unitCd, 1f, false, titleColor
                                        )

                                        TableCell(
                                            item.unitPrice.toString(), 1f, false, titleColor
                                        )

                                        TableCell(
                                            item.unitPrice.toString(), 1f, false, titleColor
                                        )
                                    }

                                }

                            }
                        }
                    }
                }
            }

            if (item.sretNo.isNullOrBlank()) {

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    shape = CircleShape,
                    shadowElevation = 6.dp
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckCircle
                        else Icons.Default.RadioButtonUnchecked,

                        contentDescription = null,

                        tint = if (isSelected) Color(0xFF23fc4b)
                        else Color.Gray,

                        )
                }

            }

        }
    }
}