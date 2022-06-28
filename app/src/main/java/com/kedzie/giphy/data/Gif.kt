package com.kedzie.giphy.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson


@JsonClass(generateAdapter = true)
data class GiphySearchResponse(
    val data: List<Gif>,
    val pagination: Pagination,
    val meta: Meta
)

@JsonClass(generateAdapter = true)
data class GiphyDetailResponse(
    val data: Gif,
    val meta: Meta
)

@JsonClass(generateAdapter = true)
data class Gif(
    val id: String,
    val rating: Rating,
    val images: Images
)

@JsonClass(generateAdapter = true)
data class Images(
    val fixed_height: Image,
    val downsized_medium: Image
)

@JsonClass(generateAdapter = true)
data class Image(
    val url: String,
    val width: Int,
    val height: Int
)

@JsonClass(generateAdapter = true)
data class Pagination(
    val offset: Int,
    val total_count: Int,
    val count: Int
)

@JsonClass(generateAdapter = true)
data class Meta(
    val status: Int,
    val msg: String,
    val response_id: String
)

enum class Rating(val requestParam: String) {
    G("g"),
    PG("pg"),
    PG13("pg-13"),
    R("r");
}

class RatingAdapter() {

    @ToJson
    fun toJson(rating: Rating) : String = when(rating) {
        Rating.G -> "g"
        Rating.PG -> "pg"
        Rating.PG13 -> "pg-13"
        Rating.R -> "r"
    }

    @FromJson
    fun fromJason(json: String) : Rating = when(json) {
        "pg" -> Rating.PG
        "pg-13" -> Rating.PG13
        "r" -> Rating.R
        else -> Rating.G
    }
}