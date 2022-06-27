package com.kedzie.giphy.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.kedzie.giphy.GiphyListViewModel
import com.kedzie.giphy.data.Gif
import com.kedzie.giphy.ui.theme.KedzieGiphyTheme

@Composable
fun GiphyListScreen(viewModel: GiphyListViewModel) {
    Column {
        Text(text = "Hello!")

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
                it?.let {
                    GiphyItem(it)
                } ?: LoadingItem().also { println("placeholder")}
            }

//            itemsIndexed(lazyPagingItems, { index, item -> index }) { index, item ->
//                GiphyItem("$index - ${item?.url ?: "null"}")
//            }

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

@Composable
fun GiphyItem(item: Gif,
              modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(16.dp),) {

        Text(text = item.id,)
    }



}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KedzieGiphyTheme {
        GiphyListScreen(hiltViewModel())
    }
}

@Composable
fun LoadingView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun LoadingItem() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

@Composable
fun ErrorItem(
    message: String,
    modifier: Modifier = Modifier,
    onClickRetry: () -> Unit
) {
    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            maxLines = 1,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.body1,
            color = Color.Red
        )
        OutlinedButton(onClick = onClickRetry) {
            Text(text = "Try again")
        }
    }
}