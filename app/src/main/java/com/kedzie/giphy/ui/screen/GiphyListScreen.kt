package com.kedzie.giphy.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
import coil.ImageLoader
import com.kedzie.giphy.GiphyListViewModel
import com.kedzie.giphy.data.Gif
import com.kedzie.giphy.ui.theme.KedzieGiphyTheme
import kotlinx.coroutines.flow.collect

@Composable
fun GiphyListScreen(viewModel: GiphyListViewModel, imageLoader: ImageLoader, onClick: (Gif) -> Unit) {

    val queryState = produceState(viewModel.query.value) {
        viewModel.query.collect {
            value= it
        }
    }

    Column {
        OutlinedTextField(
            value = queryState.value,
            onValueChange = { viewModel.query.value = it },
            label = { Text("Query") }
        )

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
                    GiphyItem(gif, imageLoader, modifier = Modifier.clickable { onClick(gif) })
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
