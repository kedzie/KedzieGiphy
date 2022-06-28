package com.kedzie.giphy.ui.screen

import androidx.compose.foundation.layout.*
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
import com.kedzie.giphy.data.Gif
import com.kedzie.giphy.data.Image
import com.kedzie.giphy.data.Images
import com.kedzie.giphy.data.Rating
import com.kedzie.giphy.ui.theme.KedzieGiphyTheme

@Composable
fun GiphyItem(item: Gif,
              modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(16.dp)) {
        Text(text = item.id,)
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
fun LoadingItem(modifier: Modifier = Modifier) {
    Row(modifier = modifier
            .padding(16.dp)) {
        CircularProgressIndicator(
            modifier = Modifier
        )
    }
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

@Preview(showBackground = true)
@Composable
fun ItemPreview() {
    KedzieGiphyTheme {
        Column {
            GiphyItem(
                Gif(
                    id = "dumbid", images = Images(
                        fixed_height = Image("http://bullshit", 200, 200),
                        downsized_medium = Image("http://bullshit", 200, 200)
                    ), rating = Rating.G
                )
            )
            LoadingItem()
        }
    }
}
