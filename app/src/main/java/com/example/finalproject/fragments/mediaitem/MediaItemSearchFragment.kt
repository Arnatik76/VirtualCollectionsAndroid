package com.example.finalproject.fragments.mediaitem

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.adapters.MediaItemAdapter
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.databinding.FragmentMediaItemSearchBinding
import com.example.finalproject.models.MediaItem
import com.example.finalproject.models.responce.PagedResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MediaItemSearchFragment : Fragment() {

    private var _binding: FragmentMediaItemSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var mediaItemAdapter: MediaItemAdapter

    companion object {
        const val MEDIA_ITEM_SELECTION_REQUEST_KEY = "mediaItemSelectionRequestKey"
        const val SELECTED_MEDIA_ITEM_ID_KEY = "selectedMediaItemId"
        const val SELECTED_MEDIA_ITEM_TITLE_KEY = "selectedMediaItemTitle"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaItemSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        mediaItemAdapter = MediaItemAdapter { mediaItem ->
            onMediaItemSelected(mediaItem)
        }
        binding.searchResultsRecyclerView.apply {
            adapter = mediaItemAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun onMediaItemSelected(mediaItem: MediaItem) {
        setFragmentResult(MEDIA_ITEM_SELECTION_REQUEST_KEY, bundleOf(
            SELECTED_MEDIA_ITEM_ID_KEY to mediaItem.itemId,
            SELECTED_MEDIA_ITEM_TITLE_KEY to mediaItem.title
        ))
        findNavController().popBackStack()
    }

    private fun setupSearchView() {
        binding.mediaItemSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.length >= 2) {
                        performSearch(it)
                    } else {
                        Toast.makeText(context, "Введите минимум 2 символа для поиска", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        setLoading(true)
        binding.searchEmptyTextView.isVisible = false
        binding.searchResultsRecyclerView.isVisible = false

        RetrofitClient.instance.searchMediaItems(query = query)
            .enqueue(object : Callback<PagedResponse<MediaItem>> {
                override fun onResponse(
                    call: Call<PagedResponse<MediaItem>>,
                    response: Response<PagedResponse<MediaItem>>
                ) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        val pagedResponse = response.body()
                        val items = pagedResponse?.content
                        if (!items.isNullOrEmpty()) {
                            mediaItemAdapter.submitList(items)
                            binding.searchResultsRecyclerView.isVisible = true
                        } else {
                            binding.searchEmptyTextView.text = "По запросу \"$query\" ничего не найдено."
                            binding.searchEmptyTextView.isVisible = true
                        }
                    } else {
                        Toast.makeText(context, "Ошибка поиска: ${response.code()}", Toast.LENGTH_SHORT).show()
                        Log.e("MediaItemSearch", "API Error: ${response.code()} - ${response.message()}")
                        binding.searchEmptyTextView.text = "Ошибка поиска."
                        binding.searchEmptyTextView.isVisible = true
                    }
                }

                override fun onFailure(call: Call<PagedResponse<MediaItem>>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(context, "Сетевая ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("MediaItemSearch", "Network Failure", t)
                    binding.searchEmptyTextView.text = "Сетевая ошибка."
                    binding.searchEmptyTextView.isVisible = true
                }
            })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.searchProgressBar.isVisible = isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.searchResultsRecyclerView.adapter = null
        _binding = null
    }
}