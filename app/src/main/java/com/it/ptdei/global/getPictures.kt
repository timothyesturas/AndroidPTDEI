package com.it.ptdei.global

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.video.VideoFrameDecoder
import okhttp3.OkHttpClient


object ImageLoaderProvider {
    fun getPictures(
        context: Context,
        okHttpClient: OkHttpClient,
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(okHttpClient))
                add(VideoFrameDecoder.Factory())
            }
            .memoryCachePolicy(CachePolicy.ENABLED)   // 👈 RAM cache
            .diskCachePolicy(CachePolicy.ENABLED)     // 👈 storage cache
            .crossfade(false)
            .build()
    }
}
