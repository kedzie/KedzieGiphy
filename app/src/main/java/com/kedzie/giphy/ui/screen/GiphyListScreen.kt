package com.kedzie.giphy.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.kedzie.giphy.GiphyListViewModel
import com.kedzie.giphy.data.Rating

@Composable
fun GiphyListScreen(viewModel: GiphyListViewModel) {
    val queryState = produceState(viewModel.query.value) {
        viewModel.query.collect {
            value= it
        }
    }

    Row(Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dp(4f)),
        verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = queryState.value,
            onValueChange = { viewModel.query.value = it },
            label = { Text("Query") }
        )

        var expanded = remember { mutableStateOf(false) }

        Box(modifier = Modifier.wrapContentSize()) {
            OutlinedButton(onClick = { expanded.value = true }) {
                Text(viewModel.rating.value.name)
            }
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                for (rating in Rating.values()) {
                    DropdownMenuItem(onClick = {
                        expanded.value = false
                        viewModel.rating.value = rating
                    }) {
                        Text(rating.name)
                    }
                }
            }
        }

        if (viewModel.isLoading.value) {
            CircularProgressIndicator()
        }
    }
}
