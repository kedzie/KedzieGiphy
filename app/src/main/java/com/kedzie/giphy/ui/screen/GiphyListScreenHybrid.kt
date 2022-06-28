package com.kedzie.giphy.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.AndroidViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.kedzie.giphy.GiphyListViewModel
import com.kedzie.giphy.data.Gif
import com.kedzie.giphy.legacy.GifAdapter
import com.kedzie.giphy.legacy.GifLoadStateAdapter
import com.kedzie.giphy.ui.theme.KedzieGiphyTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun GiphyListScreenHybrid(viewModel: GiphyListViewModel, onClick: (Gif) -> Unit) {

    val queryState = produceState(viewModel.query.value) {
        viewModel.query.collect {
            value= it
        }
    }

    val pagerAdapter = remember(Unit) {
        println("created adapter")
        GifAdapter(onClick)
    }

        LaunchedEffect(Unit) {
            viewModel.gifPager.collect { pagingData ->
                println("SubmitData ${pagingData}")
                pagerAdapter.submitData(pagingData)
            }
        }
    val coroutineContext = rememberCoroutineScope()
    val pagerState = produceState<PagingData<Gif>?>(null) {
        viewModel.gifPager.collect {
            println("Collected pager state")
            value = it
        }
    }

    Column {
        OutlinedTextField(
            value = queryState.value,
            onValueChange = { viewModel.query.value = it },
            label = { Text("Query") }
        )

        AndroidView(modifier = Modifier.height(Dp(200f)).fillMaxWidth(),
            factory = { context ->
                RecyclerView(context).apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = pagerAdapter.withLoadStateHeaderAndFooter(GifLoadStateAdapter(pagerAdapter::retry),
                                            GifLoadStateAdapter(pagerAdapter::retry))
                    println("created $this")
                }
            }, update = {
            })

        val lazyPagingItems = viewModel.gifPager.collectAsLazyPagingItems()

        LazyColumn {

            lazyPagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { LoadingView(modifier = Modifier.fillParentMaxSize()) }
                    }
                    loadState.refresh is LoadState.Error -> {
                        val e = lazyPagingItems.loadState.refresh as LoadState.Error
                        item {
                            ErrorItem(
                                message = e.error.localizedMessage!!,
                                modifier = Modifier.fillParentMaxSize(),
                                onClickRetry = { retry() }
                            )
                        }
                    }
                    loadState.prepend is LoadState.Loading -> {
                        item { LoadingItem() }
                    }
                    loadState.prepend is LoadState.Error -> {
                        val e = lazyPagingItems.loadState.append as LoadState.Error
                        item {
                            ErrorItem(
                                message = e.error.localizedMessage!!,
                                onClickRetry = { retry() }
                            )
                        }
                    }
                }
            }

            items(lazyPagingItems) {
                it?.let { gif ->
                    GiphyItem(gif, modifier = Modifier.clickable { onClick(gif) })
                } ?: LoadingItem().also { println("placeholder")}
            }

            lazyPagingItems.apply {
                when {
                    loadState.append is LoadState.Loading -> {
                        item { LoadingItem() }
                    }
                    loadState.append is LoadState.Error -> {
                        val e = lazyPagingItems.loadState.append as LoadState.Error
                        item {
                            ErrorItem(
                                message = e.error.localizedMessage!!,
                                onClickRetry = { retry() }
                            )
                        }
                    }
                }
            }
        }
    }
}
