package com.it.ptdei.global

import android.content.Context
import android.util.Log
import com.it.ptdei.menu.salesapproval.features.getOrders.CachedUploadsFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class FileCacheManager(
    context: Context,
    private val client: OkHttpClient,
) {

    private val cacheDir = File(context.filesDir, "file_cache").apply {
        if (!exists()) mkdirs()
    }

    private fun getMimeTypeFromFile(file: File): String {
        return when (file.extension.lowercase()) {
            "pdf" -> "application/pdf"
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "bmp" -> "image/bmp"
            else -> "application/octet-stream"
        }
    }

    suspend fun getOrDownloadFile(
        url: String,
    ): CachedUploadsFile = withContext(Dispatchers.IO) {

        Log.d("url", url)

        // Download first to determine mime type
        val request = Request.Builder().url(url).build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code}")
        }

        val mimeType = response.header("Content-Type") ?: "application/octet-stream"

        Log.d("mimeType", mimeType)

        val extension = when {
            mimeType.contains("pdf") -> ".pdf"
            mimeType.contains("png") -> ".png"
            mimeType.contains("jpeg") -> ".jpg"
            mimeType.contains("jpg") -> ".jpg"
            mimeType.contains("bmp") -> ".bmp"
            else -> ""
        }

        val file = File(
            cacheDir, "${url.hashCode()}$extension"
        )

        // CACHE HIT
        if (file.exists() && file.length() > 0) {

            Log.d(
                "CACHE", "Using cached file: ${file.name}"
            )

            return@withContext CachedUploadsFile(
                file = file, mimeType = getMimeTypeFromFile(file)
            )
        }

        // DOWNLOAD
        response.body?.byteStream()?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        Log.d(
            "CACHE", "Downloaded file: ${file.name}"
        )

        response.close()

        return@withContext CachedUploadsFile(
            file = file, mimeType = mimeType
        )
    }


}