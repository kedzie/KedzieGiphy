package com.kedzie.giphy.legacy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedzie.giphy.GiphyListViewModel
import com.kedzie.giphy.R
import com.kedzie.giphy.data.Gif
import com.kedzie.giphy.databinding.FragmentFirstBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

//    private val viewModel by navGraphViewModels<GiphyListViewModel>("giphyListRoute")
    private val viewModel by viewModels<GiphyListViewModel>()

    private val pagerAdapter = GifAdapter()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = with(FragmentFirstBinding.inflate(inflater, container, false)) {
            _binding = this
            root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.buttonFirst.setOnClickListener {
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            }
            binding.giphyList.layoutManager = LinearLayoutManager(view.context)
            binding.giphyList.adapter = pagerAdapter.withLoadStateHeaderAndFooter(
                GifLoadStateAdapter(pagerAdapter::retry),
                GifLoadStateAdapter(pagerAdapter::retry))

            // Create a new coroutine in the lifecycleScope
            viewLifecycleOwner.lifecycleScope.launch {
                // repeatOnLifecycle launches the block in a new coroutine every time the
                // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    // Trigger the flow and start listening for values.
                    // This happens when lifecycle is STARTED and stops
                    // collecting when the lifecycle is STOPPED
                    viewModel.gifPager.collect {
                        // Process item
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

class GifAdapter :
    PagingDataAdapter<Gif, GifViewHolder>(GifComparator) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GifViewHolder {
        return GifViewHolder(parent)
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object GifComparator : DiffUtil.ItemCallback<Gif>() {
    override fun areItemsTheSame(oldItem: Gif, newItem: Gif): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Gif, newItem: Gif): Boolean {
        return oldItem == newItem
    }
}

class GifViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(TextView(parent.context)) {

    fun bind(item: Gif?)  {
        (itemView as TextView).text = item?.id ?: "Placeholder"
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
