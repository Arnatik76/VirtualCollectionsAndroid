package com.example.androidvirtualcollections

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CollectionListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CollectionAdapter
    private val collections = mutableListOf<Collection>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection_list, container, false)
        recyclerView = view.findViewById(R.id.collectionsRecyclerView)
        loadCollections()
        setupRecyclerView()
        return view
    }

    private fun loadCollections() {
        val savedCollections = FileUtils.loadCollections(requireContext())
        if (savedCollections.isNotEmpty()) {
            collections.clear()
            collections.addAll(savedCollections)
        } else {
            // Демо-данные
            collections.clear()
            collections.addAll(listOf(
                Collection(1, "Моя коллекция 1", 5, R.drawable.ic_collections),
                Collection(2, "Моя коллекция 2", 3, R.drawable.ic_collections)
            ))
            FileUtils.saveCollections(requireContext(), collections)
        }
    }

    private fun setupRecyclerView() {
        adapter = CollectionAdapter(collections)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}