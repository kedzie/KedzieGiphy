package com.kedzie.giphy.data

import com.kedzie.giphy.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyService {

    @GET("gifs/trending")
    suspend fun random(@Query("apiKey") apiKey: String = BuildConfig.GIPHY_APP_ID,
                       @Query("lang") lang: String = "en",
                       @Query("rating") rating: String = Rating.G.requestParam,
                       @Query("offset") offset: Int = 0,
                       @Query("limit") limit: Int = 25
    ): GiphySearchResponse

    @GET("gifs/search")
    suspend fun search(@Query("apiKey") apiKey: String = BuildConfig.GIPHY_APP_ID,
                       @Query("lang") lang: String = "en",
                       @Query("rating") rating: String = Rating.G.requestParam,
                       @Query("offset") offset: Int = 0,
                       @Query("limit") limit: Int = 25,
                       @Query("q") query: String = ""
    ): GiphySearchResponse

}