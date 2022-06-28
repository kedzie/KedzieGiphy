package com.kedzie.giphy.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.kedzie.giphy.GiphyListViewModel
import com.kedzie.giphy.data.Rating
import com.kedzie.giphy.ui.theme.KedzieGiphyTheme

@Composable
fun GiphyListScreen(viewModel: GiphyListViewModel) {
    GiphyListScreenInner(
        queryState = produceState(viewModel.query.value) {
            viewModel.query.collect {
                value= it
            }
        },
        onQueryChange = { viewModel.query.value = it },
        ratingState = produceState(viewModel.rating.value) {
            viewModel.rating.collect {
                value= it
            }
        },
        onRatingChange = { viewModel.rating.value = it },
        isLoading = viewModel.isLoading.value)
}

@Composable
fun GiphyListScreenInner(queryState: State<String> = remember { mutableStateOf("") },
                         onQueryChange: (String)->Unit = {},
                         ratingState: State<Rating> = remember { mutableStateOf(Rating.G) },
                         onRatingChange: (Rating)->Unit = {},
                         isLoading: Boolean = false) {
    val focusManager = LocalFocusManager.current

    Column(
        Modifier
            .fillMaxWidth()
            .padding(Dp(8f)),
        verticalArrangement = Arrangement.spacedBy(Dp(4f)),
        horizontalAlignment = Alignment.Start) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dp(4f)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = queryState.value,
                onValueChange = {
                    onQueryChange(it)
                },
                label = { Text("Query") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions { focusManager.clearFocus() }
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dp(4f)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val expanded = remember { mutableStateOf(false) }

            Text("Rating:")

            Box(modifier = Modifier.wrapContentSize()) {
                OutlinedButton(onClick = { expanded.value = true }) {
                    Text(ratingState.value.name)
                }
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    for (rating in Rating.values()) {
                        DropdownMenuItem(onClick = {
                            expanded.value = false
                            onRatingChange(rating)
                        }) {
                            Text(rating.name)
                        }
                    }
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
@Preview
fun previewList() {
    KedzieGiphyTheme {
        Surface {
            GiphyListScreenInner(isLoading = true)
        }
    }
}