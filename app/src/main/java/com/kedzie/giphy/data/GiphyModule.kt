package com.kedzie.giphy.data

import android.content.Context
import android.content.res.Resources
import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.memory.MemoryCache
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Hilt dependency injection module
 */
@InstallIn(SingletonComponent::class)
@Module
class GiphyModule {

    /**
     * retrofit service to invoke Giphy API
     */
    @Provides
    fun getGiphyService(): GiphyService
        = Retrofit.Builder()
            .baseUrl("https://api.giphy.com/v1/")
            .client(OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BASIC) }).build())
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder()
                .add(RatingAdapter())
                .build()))
            .build()
        .create(GiphyService::class.java)

    /**
     * Coil Image loader to async load all images. Configured to support animated GIF
     */
    @Provides
    fun getImageLoader(@ApplicationContext context: Context): ImageLoader =
       ImageLoader.Builder(context)
           .crossfade(true)
           .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }.components {
               if (Build.VERSION.SDK_INT >= 28) {
                   add(ImageDecoderDecoder.Factory())
               } else {
                   add(GifDecoder.Factory())
               }
           }.build()

    /**
     * Used to get device locales in the GiphyListViewModel
     */
    @Provides
    fun getResources(@ApplicationContext appContext: Context) : Resources
        = appContext.resources
}