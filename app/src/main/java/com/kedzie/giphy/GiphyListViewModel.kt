package com.kedzie.giphy

import android.content.res.Resources
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.kedzie.giphy.data.GiphyPagingSourceFactory
import com.kedzie.giphy.data.Rating
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class GiphyListViewModel @Inject constructor(private val giphyPagerFactory: GiphyPagingSourceFactory,
                                             resources: Resources): ViewModel() {

    val languages = List<String>(resources.configuration.locales.size()) {
        resources.configuration.locales[it].language
    }

    val lang = MutableStateFlow(resources.configuration.locales[0].language)

    val query = MutableStateFlow("")

    val rating = MutableStateFlow(Rating.G)

    val gifPager = combine(query, rating, lang) { q, r, l -> Triple(q, r, l) }
        .debounce(1000)
        .onEach {
            println("Loading = true")
            isLoading.value = true
        }
        .flatMapLatest { (q, r, l) ->
            println("Combining $q, $r, $l")
            Pager(PagingConfig(pageSize = 40, prefetchDistance = 0, enablePlaceholders = true, maxSize = 200)) {
                giphyPagerFactory.create(q, r, l)
            }.flow
        }
        .onEach {
            isLoading.value = false
            println("loading = false")
        }
        .cachedIn(viewModelScope)

    val isLoading = mutableStateOf(true)
}