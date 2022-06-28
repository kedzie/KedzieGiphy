package com.kedzie.giphy.legacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import coil.ImageLoader
import com.kedzie.giphy.databinding.FragmentDetailBinding
import com.kedzie.giphy.ui.screen.DetailScreen
import com.kedzie.giphy.ui.theme.KedzieGiphyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment showing full screen GIPHY image.  takes URL as parameter.  Could expand this to take
 * ID as parameter and fetch the GIPHY for deeplink support where the list data isn't available.
 * Could also display more information like the GIPHY title, links, etc..
 */
@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  = with(FragmentDetailBinding.inflate(inflater, container, false)) {
        _binding = this
        root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.composeView.setContent {
            KedzieGiphyTheme {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colors.background
                ) {
                    DetailScreen(url = arguments!!.getString("url")!!, imageLoader = imageLoader)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}