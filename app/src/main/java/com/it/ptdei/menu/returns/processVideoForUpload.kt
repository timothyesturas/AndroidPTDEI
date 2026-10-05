package com.it.ptdei.menu.returns

import androidx.media3.common.util.UnstableApi
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.effect.SpeedChangeEffect
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val TAG = "VIDEO_PROCESSOR"

private const val MAX_DURATION_MS = 30_000L

// Keep below your actual 18 MB server limit.
private const val TARGET_SIZE_BYTES = 17L * 1024L * 1024L


@androidx.annotation.OptIn(UnstableApi::class)
suspend fun processVideoForUpload(
    context: Context,
    inputUri: Uri,
): File {

    return withContext(Dispatchers.IO) {

        val outputFile = File(
            context.cacheDir,
            "processed_video_${System.currentTimeMillis()}.mp4"
        )

        val durationMs = getVideoDuration(
            context,
            inputUri
        )

        Log.d(
            TAG,
            "Original duration: ${durationMs} ms"
        )

        val speed =
            if (durationMs > MAX_DURATION_MS) {
                durationMs.toFloat() /
                        MAX_DURATION_MS.toFloat()
            } else {
                1f
            }

        Log.d(
            TAG,
            "Playback speed: ${speed}x"
        )

        val mediaItem =
            MediaItem.fromUri(inputUri)

        val editedMediaItem =
            EditedMediaItem.Builder(mediaItem)
                .setRemoveAudio(true)
                .setEffects(
                    Effects(
                        emptyList<AudioProcessor>(),
                        listOf(
                            SpeedChangeEffect(speed)
                        )
                    )
                )
                .build()

        /*
         * IMPORTANT:
         *
         * Transformer must be created and controlled
         * on the application's main thread.
         */
        val resultFile =
            withContext(Dispatchers.Main.immediate) {

                suspendCancellableCoroutine<File> { continuation ->

                    lateinit var transformer: Transformer

                    transformer =
                        Transformer.Builder(context)
                            .setVideoMimeType("video/avc")
                            .addListener(
                                object : Transformer.Listener {

                                    override fun onCompleted(
                                        composition:
                                        androidx.media3.transformer.Composition,

                                        exportResult:
                                        ExportResult,
                                    ) {

                                        Log.d(
                                            TAG,
                                            "Transformation completed"
                                        )

                                        if (!outputFile.exists()) {

                                            continuation.resumeWithException(
                                                Exception(
                                                    "Output video was not created"
                                                )
                                            )

                                            return
                                        }

                                        Log.d(
                                            TAG,
                                            "Output size: ${
                                                outputFile.length()
                                            } bytes"
                                        )

                                        continuation.resume(
                                            outputFile
                                        )
                                    }

                                    override fun onError(
                                        composition:
                                        androidx.media3.transformer.Composition,

                                        exportResult:
                                        ExportResult,

                                        exportException:
                                        ExportException,
                                    ) {

                                        Log.e(
                                            TAG,
                                            "Transformation failed",
                                            exportException
                                        )

                                        continuation.resumeWithException(
                                            exportException
                                        )
                                    }
                                }
                            )
                            .build()

                    /*
                     * Cancellation also happens on Main.
                     */
                    continuation.invokeOnCancellation {

                        // Don't call transformer.cancel()
                        // directly from the cancellation thread.
                        android.os.Handler(
                            android.os.Looper.getMainLooper()
                        ).post {

                            try {
                                transformer.cancel()
                            } catch (e: Exception) {
                                Log.e(
                                    TAG,
                                    "Failed to cancel transformer",
                                    e
                                )
                            }
                        }
                    }

                    transformer.start(
                        editedMediaItem,
                        outputFile.absolutePath
                    )
                }
            }

        /*
         * Check final file size.
         */
        val fileSize =
            resultFile.length()

        Log.d(
            TAG,
            "Final video size: $fileSize bytes"
        )

        if (fileSize >= TARGET_SIZE_BYTES) {

            resultFile.delete()

            throw Exception(
                "Processed video is still too large: " +
                        "${fileSize / 1024 / 1024} MB"
            )
        }

        resultFile
    }
}




private suspend fun getVideoDuration(
    context: Context,
    uri: Uri,
): Long = withContext(Dispatchers.IO) {

    val retriever =
        android.media.MediaMetadataRetriever()

    try {

        retriever.setDataSource(
            context,
            uri
        )

        retriever
            .extractMetadata(
                android.media.MediaMetadataRetriever
                    .METADATA_KEY_DURATION
            )
            ?.toLongOrNull()
            ?: 0L

    } finally {

        retriever.release()
    }
}