package com.kedzie.giphy.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import com.kedzie.giphy.GiphyListViewModel

@Composable
fun GiphyListScreen(viewModel: GiphyListViewModel) {

    val queryState = produceState(viewModel.query.value) {
        viewModel.query.collect {
            value= it
        }
    }

    Row(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = queryState.value,
            onValueChange = { viewModel.query.value = it },
            label = { Text("Query") }
        )
    }
}
