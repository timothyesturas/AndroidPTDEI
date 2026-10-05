package com.it.ptdei.global

import android.content.Context
import coil3.Uri
import java.io.File

fun createImageFile(context: Context): File {

    return File(
        context.cacheDir,
        "IMG_${System.currentTimeMillis()}.jpg"
    )
}

fun createVideoFile(context: Context): File {

    return File(
        context.cacheDir,
        "VID_${System.currentTimeMillis()}.mp4"
    )
}
