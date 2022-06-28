package com.kedzie.giphy.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Handles paging for Giphy list responses. trending/search.
 * Uses assisted inject for dependencies as it has passed in arguments for
 * query parameters.
 */
class GiphyPagingSource @AssistedInject constructor(private val giphyService: GiphyService,
                                                    @Assisted("query") val query: String,
                                                    @Assisted("rating") val rating: Rating,
                                                    @Assisted("lang") val lang: String):
    PagingSource<Int, Gif>() {

    override fun getRefreshKey(state: PagingState<Int, Gif>): Int?
        = state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.also { anchorPage ->
                println("getRefreshKey anchorPosition: ${state.anchorPosition}, anchorPage: $anchorPage")
            }?.itemsBefore
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Gif>
        = try {
            val nextItem = params.key ?: 0

            val response = if(query.isEmpty()) {
                println("loading trending.  offset: $nextItem.  count: ${params.loadSize}")
                //load random if no query
                giphyService.random(rating = rating.requestParam,
                                lang = lang,
                                offset = nextItem,
                                limit = params.loadSize)
            } else {
                println("loading search.  offset: $nextItem.  count: ${params.loadSize}")
                //search with query
                giphyService.search(rating = rating.requestParam,
                    lang = lang,
                    query = query,
                    offset = nextItem,
                    limit = params.loadSize)
            }

            if (response.meta.status == 200) {
                println("First item ${response.data.first().id}")
                val isLastPage = response.pagination.offset + params.loadSize > response.pagination.total_count
                LoadResult.Page(
                    data = response.data,
                    prevKey = if (nextItem == 0) null else nextItem - params.loadSize,
                    nextKey = if (isLastPage) null
                                else response.pagination.offset + params.loadSize,
                    itemsBefore = nextItem,
                    itemsAfter = if(isLastPage) 0 else response.pagination.total_count-(response.pagination.offset + response.pagination.count)
                ).also {
                    println("Got page.  nextKey: ${it.nextKey}.  itemsAfter: ${it.itemsAfter}.  prevKey: ${it.prevKey}.  itemsBefore ${it.itemsBefore}")
                }
            } else {
                println("Error loading paged data ${response.meta.msg}")
                LoadResult.Error(Exception(response.meta.msg))
            }
        } catch (e: Exception) {
            Log.e("GiphyPagingSource", "error", e)
            LoadResult.Error(e)
        }
}

@AssistedFactory
interface GiphyPagingSourceFactory {
    fun create(@Assisted("query") query: String,
               @Assisted("rating") rating: Rating,
               @Assisted("lang") lang: String) : GiphyPagingSource
}