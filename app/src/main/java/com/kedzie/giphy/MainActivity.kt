package com.kedzie.giphy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import com.kedzie.giphy.ui.screen.DetailScreen
import com.kedzie.giphy.ui.screen.GiphyListScreen
import com.kedzie.giphy.ui.theme.KedzieGiphyTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.net.URLEncoder
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KedzieGiphyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    @Composable
    fun MainScreen() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "list") {
            composable("list") { backStackEntry -> GiphyListScreen(hiltViewModel(backStackEntry), imageLoader) { gif -> navController.navigate("detail/${URLEncoder.encode(gif.images.downsized_medium.url, "utf-8")}") } }

            composable("detail/{url}") { backStackEntry -> DetailScreen(url = URLDecoder.decode(backStackEntry.arguments?.getString("url")!!, "utf-8"), imageLoader)}
        }
    }
}



