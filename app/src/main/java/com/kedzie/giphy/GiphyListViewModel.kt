package com.kedzie.giphy

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.kedzie.giphy.data.GiphyPagingSourceFactory
import com.kedzie.giphy.data.Rating
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class GiphyListViewModel @Inject constructor(val giphyPagerFactory: GiphyPagingSourceFactory): ViewModel() {

    val query = MutableStateFlow("")

    val rating = MutableStateFlow(Rating.G)

    val gifPager = query.combine(rating) { q, r -> q to r }
        .debounce(1000)
        .flatMapLatest { (q, r) ->
            println("Combining $q, $r")
            Pager(PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5, enablePlaceholders = false, maxSize = 100)) {
                giphyPagerFactory.create(q, r, "en")
            }.flow
        }.onStart { isLoading.value = true }
        .onEach { isLoading.value = false }
        .cachedIn(viewModelScope)


    val isLoading = mutableStateOf(true)
}