package com.example.finalproject.fragments.collection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.adapters.CollectionAdapter
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.databinding.FragmentHomeBinding
import com.example.finalproject.models.Collection
import com.example.finalproject.models.responce.PagedResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var collectionAdapter: CollectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchPublicCollections()
    }

    private fun setupRecyclerView() {
        collectionAdapter = CollectionAdapter { collection ->
            Toast.makeText(context, "Переход к коллекции: ${collection.title}", Toast.LENGTH_SHORT).show()
            Log.d("HomeFragment", "Clicked collection ID: ${collection.collectionId}")
            val action = HomeFragmentDirections.actionHomeFragmentToCollectionDetailsFragment(collection.collectionId)
            findNavController().navigate(action)
        }
        binding.collectionsRecyclerView.apply {
            adapter = collectionAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun fetchPublicCollections() {
        setLoading(true)
        binding.emptyViewText.isVisible = false

        RetrofitClient.instance.getPublicCollections(size = 10)
            .enqueue(object : Callback<PagedResponse<Collection>> {
                override fun onResponse(call: Call<PagedResponse<Collection>>, response: Response<PagedResponse<Collection>>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        val pagedResponse = response.body()
                        if (pagedResponse != null) {
                            val collections = pagedResponse.content
                            if (collections.isNotEmpty()) {
                                collectionAdapter.submitList(collections)
                                binding.collectionsRecyclerView.isVisible = true
                                Log.d("HomeFragment", "Loaded page ${pagedResponse.number + 1} of ${pagedResponse.totalPages}")
                            } else {
                                binding.collectionsRecyclerView.isVisible = false
                                binding.emptyViewText.text = getString(R.string.no_collections_found)
                                binding.emptyViewText.isVisible = true
                            }
                        } else {
                            handleApiError(getString(R.string.error_loading_collections) + " (пустой ответ)")
                        }
                    } else {
                        handleApiError("Ошибка ${response.code()}: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PagedResponse<Collection>>, t: Throwable) {
                    setLoading(false)
                    handleApiError(getString(R.string.network_error) + ": ${t.localizedMessage}")
                    Log.e("HomeFragment", "API Failure", t)
                }
            })
    }

    private fun handleApiError(errorMessage: String) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        binding.collectionsRecyclerView.isVisible = false
        binding.emptyViewText.text = errorMessage
        binding.emptyViewText.isVisible = true
    }


    private fun setLoading(isLoading: Boolean) {
        binding.homeProgressBar.isVisible = isLoading
        // Можно задизейблить RecyclerView или SwipeRefreshLayout если он есть
        binding.collectionsRecyclerView.isVisible = !isLoading && binding.collectionsRecyclerView.adapter?.itemCount ?: 0 > 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Важно очищать ссылку на binding в onDestroyView для фрагментов
        // чтобы избежать утечек памяти
        binding.collectionsRecyclerView.adapter = null // Очистка адаптера
        _binding = null
    }
}