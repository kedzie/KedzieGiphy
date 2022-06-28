package com.kedzie.giphy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.kedzie.giphy.GiphyListViewModel

@Composable
fun GiphyListScreen(viewModel: GiphyListViewModel) {
    val queryState = produceState(viewModel.query.value) {
        viewModel.query.collect {
            value= it
        }
    }

    Column (Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dp(4f))) {
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = queryState.value,
                onValueChange = { viewModel.query.value = it },
                label = { Text("Query") }
            )
        }
        if (viewModel.isLoading.value) {
            CircularProgressIndicator()
        }
    }
}
