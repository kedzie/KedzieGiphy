package com.kedzie.giphy.legacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.kedzie.giphy.GiphyListViewModel
import com.kedzie.giphy.R
import com.kedzie.giphy.data.Gif
import com.kedzie.giphy.databinding.FragmentListBinding
import com.kedzie.giphy.ui.screen.ErrorItem
import com.kedzie.giphy.ui.screen.GiphyItem
import com.kedzie.giphy.ui.screen.GiphyListScreen
import com.kedzie.giphy.ui.screen.LoadingItem
import com.kedzie.giphy.ui.theme.KedzieGiphyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<GiphyListViewModel>()

    @Inject
    lateinit var imageLoader : ImageLoader

    private val pagerAdapter = GifAdapter() {
        findNavController().navigate(R.id.showDetails,
            Bundle().apply {
                putString("url", it.images.downsized_medium.url)
            })
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View = with(FragmentListBinding.inflate(inflater, container, false)) {
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
                        GiphyListScreen(viewModel)
                    }
                }
            }

            /*
              Used native views here as compose-paging doesn't support grid views yet,
              and the list view is buggy for infinite lists
             */
            binding.giphyList.layoutManager = GridLayoutManager(view.context, 3)
            binding.giphyList.adapter = pagerAdapter.withLoadStateHeaderAndFooter(
                GifLoadStateAdapter(pagerAdapter::retry),
                GifLoadStateAdapter(pagerAdapter::retry))

            viewLifecycleOwner.lifecycleScope.launch {
                // repeatOnLifecycle launches the block in a new coroutine every time the
                // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.gifPager.collect {
                        pagerAdapter.submitData(it)
                    }
                }
            }

        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

    inner class GifAdapter(val onClickListener: (Gif) -> Unit) : PagingDataAdapter<Gif, GifViewHolder>(GifComparator) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder = GifViewHolder(parent, onClickListener)
        override fun onBindViewHolder(holder: GifViewHolder, position: Int) = holder.bind(getItem(position))
    }

    object GifComparator : DiffUtil.ItemCallback<Gif>() {
        override fun areItemsTheSame(oldItem: Gif, newItem: Gif): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Gif, newItem: Gif): Boolean = oldItem == newItem
    }

    inner class GifViewHolder(parent: ViewGroup, val onClick: (Gif) -> Unit): RecyclerView.ViewHolder(ComposeView(parent.context)) {

        fun bind(item: Gif?)  {
            (itemView as ComposeView).let { composeView ->
                composeView.setContent {
                    item?.let { gif ->
                        GiphyItem(gif, imageLoader, modifier = Modifier.clickable { onClick(gif) })
                    } ?: LoadingItem()
                }
            }
        }
    }

    class GifLoadStateAdapter(
        private val retry: () -> Unit
    ) : LoadStateAdapter<LoadStateViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) = LoadStateViewHolder(parent, retry)
        override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) = holder.bind(loadState)
    }


    class LoadStateViewHolder(parent: ViewGroup, private val retry: () -> Unit): RecyclerView.ViewHolder(ComposeView(parent.context)) {

        fun bind(loadState: LoadState)  {
            (itemView as ComposeView).setContent {
                when(loadState) {
                    is LoadState.Error -> ErrorItem(loadState.error.localizedMessage ?: "Error") { retry() }
                    is LoadState.Loading -> LoadingItem()
                    else -> {}
                }
            }
        }
    }
}

