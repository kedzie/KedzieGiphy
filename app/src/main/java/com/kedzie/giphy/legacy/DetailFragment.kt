package com.kedzie.giphy.legacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.ImageLoader
import com.kedzie.giphy.databinding.FragmentDetailBinding
import com.kedzie.giphy.ui.screen.DetailScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
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
    ): View?  = with(FragmentDetailBinding.inflate(inflater, container, false)) {
        _binding = this
        root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.composeView.setContent {
            DetailScreen(url = arguments!!.getString("url")!!, imageLoader = imageLoader)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}