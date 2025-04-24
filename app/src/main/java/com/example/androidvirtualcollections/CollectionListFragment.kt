package com.example.androidvirtualcollections

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CollectionListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CollectionAdapter
    private val collections = mutableListOf<Collection>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection_list, container, false)
        recyclerView = view.findViewById(R.id.collectionsRecyclerView)

        // Добавляем FAB
        val fab = FloatingActionButton(requireContext())
        fab.id = View.generateViewId()
        fab.setImageResource(android.R.drawable.ic_input_add)
        val params = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 16, 16)
        fab.layoutParams = params
        (view as ViewGroup).addView(fab)

        fab.setOnClickListener {
            addNewCollection()
        }

        loadCollections()
        setupRecyclerView()

        return view
    }

    private fun loadCollections() {
        val savedCollections = FileUtils.loadCollections(requireContext())
        collections.clear()

        if (savedCollections.isNotEmpty()) {
            collections.addAll(savedCollections)
        } else {
            // Демо-коллекции
            collections.add(Collection(
                id = 1,
                title = "Моя коллекция монет",
                description = "Нумизматическая коллекция",
                items = mutableListOf(
                    MediaItem(1, "Монета 1", "Серебряная монета", R.drawable.ic_catalog),
                    MediaItem(2, "Монета 2", "Золотая монета", R.drawable.ic_catalog)
                )
            ))
            FileUtils.saveCollections(requireContext(), collections)
        }
    }

    private fun setupRecyclerView() {
        adapter = CollectionAdapter(collections)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun addNewCollection() {
        val newId = if (collections.isEmpty()) 1 else collections.maxByOrNull { it.id }!!.id + 1
        val newCollection = Collection(
            id = newId,
            title = "Новая коллекция ${newId}",
            description = "Описание коллекции"
        )

        collections.add(newCollection)
        FileUtils.saveCollections(requireContext(), collections)
        adapter.notifyItemInserted(collections.size - 1)
    }
}