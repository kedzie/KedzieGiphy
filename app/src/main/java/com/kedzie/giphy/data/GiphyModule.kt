package com.kedzie.giphy.data

import android.content.Context
import android.content.res.Resources
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@InstallIn(SingletonComponent::class)
@Module
class GiphyModule {

    @Provides
    fun getGiphyService(): GiphyService
        = Retrofit.Builder()
            .baseUrl("https://api.giphy.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder()
                .add(RatingAdapter())
                .build()))
            .build()
            .let {
                it.create(GiphyService::class.java)
            }

    @Provides
    fun getResources(@ApplicationContext appContext: Context) : Resources
        = appContext.resources
}