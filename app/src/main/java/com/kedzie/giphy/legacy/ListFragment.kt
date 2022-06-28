package com.kedzie.giphy.legacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedzie.giphy.GiphyListViewModel
import com.kedzie.giphy.R
import com.kedzie.giphy.data.Gif
import com.kedzie.giphy.databinding.FragmentListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

//    private val viewModel by navGraphViewModels<GiphyListViewModel>("giphyListRoute")
    private val viewModel by viewModels<GiphyListViewModel>()

    private val pagerAdapter = GifAdapter {
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment,
            Bundle().apply {
                putString("url", it.images.downsized_medium.url)
            })
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = with(FragmentListBinding.inflate(inflater, container, false)) {
            _binding = this
            root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.queryText.editText?.addTextChangedListener { viewModel.query.value = it.toString() }
            binding.giphyList.layoutManager = LinearLayoutManager(view.context)
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
}

class GifAdapter(val onClickListener: (Gif) -> Unit) : PagingDataAdapter<Gif, GifViewHolder>(GifComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder = GifViewHolder(parent, onClickListener)
    override fun onBindViewHolder(holder: GifViewHolder, position: Int) = holder.bind(getItem(position))
}

object GifComparator : DiffUtil.ItemCallback<Gif>() {
    override fun areItemsTheSame(oldItem: Gif, newItem: Gif): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Gif, newItem: Gif): Boolean = oldItem == newItem
}

class GifViewHolder(parent: ViewGroup, val onClickListener: (Gif) -> Unit): RecyclerView.ViewHolder(TextView(parent.context)) {

    fun bind(item: Gif?)  {
        (itemView as TextView).let { textView ->
            textView.text = item?.id ?: "Placeholder"
            textView.setPadding(16, 16, 16, 16)
            item?.let { gif ->
                textView.setOnClickListener { onClickListener(gif) }
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


class LoadStateViewHolder(parent: ViewGroup, private val retry: () -> Unit): RecyclerView.ViewHolder(TextView(parent.context)) {

    fun bind(loadState: LoadState)  {
        (itemView as TextView).text = when(loadState) {
            is LoadState.Error -> loadState.error.localizedMessage
            is LoadState.Loading -> "Loading..."
            else -> "Not Loading"
        }
        if (loadState is LoadState.Error) {
            itemView.setOnClickListener { retry()  }
        }
    }
}
