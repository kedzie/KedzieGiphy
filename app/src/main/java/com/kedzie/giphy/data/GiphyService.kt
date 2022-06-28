package com.kedzie.giphy.data

import com.kedzie.giphy.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Giphy API service definition via Retrofit
 */
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

    //unused.  If we needed deeplink support for Giphy we could add it and have a version of detail
    //screen which takes ID as parameter instead of just the url.  could then add more advanced features
    //like sharing different sized images/showing the title.
    @GET("gifs/{id}")
    suspend fun detail(@Path("id") id: String,
                       @Query("apiKey") apiKey: String = BuildConfig.GIPHY_APP_ID): GiphyDetailResponse
}