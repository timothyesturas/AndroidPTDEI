package com.it.ptdei.menu.returns.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.it.ptdei.global.ImageLoaderProvider
import com.it.ptdei.menu.returns.downloadPdfToCache
import com.it.ptdei.menu.returns.features.returnItems.AttachmentItem
import com.it.ptdei.menu.returns.generatePdfThumbnail
import com.it.ptdei.menu.returns.openFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun AttachmentDialog(
    attachmentList: List<AttachmentItem>,
    hasCamera: Boolean,
    okHttpClient: OkHttpClient,
    context: Context,
    onDismiss: () -> Unit,
    onCheckedChange: (String, Boolean) -> Unit,
    onAddPhoto: () -> Unit,
    images: List<Uri>
) {


    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imageLoader = remember {
        ImageLoaderProvider.getPictures(context, okHttpClient)
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Attachments")
        },

        text = {
            Log.d("ATTACHMENTS", attachmentList.toString())
            Log.d("ATTACHMENTS", "size = ${attachmentList.size}")

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                items(attachmentList) { item ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {

                        Checkbox(
                            checked = item.checked, onCheckedChange = {
                                onCheckedChange(item.url, it)
                            })

                        Spacer(Modifier.width(8.dp))

                        if (item.type.equals("PDF", true)) {

                            Log.d("url", item.url)

                            PdfThumbnail(
                                pdfUrl = item.url,
                                okHttpClient = okHttpClient,
                                modifier = Modifier
                                    .size(200.dp)
                                    .clickable {
                                        openFile(context, item.url)
                                    })

                        } else {

                            // Reuses your custom Coil image loader.
                            val imageLoader = remember {
                                ImageLoaderProvider.getPictures(
                                    context, okHttpClient
                                )
                            }

                            Log.d("url2", item.url)

                            AsyncImage(
                                model = item.url,
                                imageLoader = imageLoader,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(200.dp)
                                    .clickable {
                                        openFile(context, item.url)
                                    })
                        }
                    }
                }
            }

            if(images.isNotEmpty()){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE5E5E5))
                        .padding(8.dp)
                ) {

                    Text(
                        text = "Captured Media", fontWeight = FontWeight.Bold, fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = screenHeight * .5f)
                    ) {

                        // Images
                        items(images) { imageUri ->

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
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

                                Icon(
                                    imageVector = Icons.Filled.Image,
                                    contentDescription = "Photo",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(6.dp)
                                )
                            }
                        }
                    }
                }
            }
        },

        confirmButton = {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(onClick = onDismiss) {
                    Text("Close")
                }

                if (hasCamera) {
                    Button(onClick = onAddPhoto) {
                        Text("Add Photo", maxLines = 1)
                    }
                } else {
                    Log.e("CAMERA", "No camera available")
                }
            }
        })
}


@Composable
fun PdfThumbnail(
    pdfUrl: String,
    okHttpClient: OkHttpClient,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val bitmap by produceState<Bitmap?>(null, pdfUrl) {

        value = withContext(Dispatchers.IO) {

            val file = downloadPdfToCache(
                context, pdfUrl, okHttpClient
            )

            file?.let {
                generatePdfThumbnail(it)
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(), contentDescription = null, modifier = modifier
        )
    } else {
        CircularProgressIndicator()
    }
}