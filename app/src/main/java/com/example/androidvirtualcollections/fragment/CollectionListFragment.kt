package com.example.androidvirtualcollections.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidvirtualcollections.R
import com.example.androidvirtualcollections.adapter.CollectionAdapter
import com.example.androidvirtualcollections.model.Collection
import com.example.androidvirtualcollections.model.MediaItem
import com.example.androidvirtualcollections.service.CollectionService
import com.example.androidvirtualcollections.util.FileUtils
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

        view.findViewById<FloatingActionButton>(R.id.fabAddCollection).setOnClickListener {
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
            collections.add(
                Collection(
                id = 1,
                title = "Моя коллекция монет",
                description = "Нумизматическая коллекция",
                items = mutableListOf(
                    MediaItem(1, "Монета 1", "Серебряная монета",
                        R.drawable.ic_catalog),
                    MediaItem(2, "Монета 2", "Золотая монета",
                        R.drawable.ic_catalog)
                )
            )
            )
            FileUtils.saveCollections(requireContext(), collections)
        }
    }

    private fun setupRecyclerView() {
        adapter = CollectionAdapter(collections)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun createBackup() {
        val intent = Intent(requireContext(), CollectionService::class.java).apply {
            action = CollectionService.ACTION_BACKUP_COLLECTIONS
        }
        requireContext().startService(intent)
        Toast.makeText(context, "Создание резервной копии...", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collection_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_backup -> {
                createBackup()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun addNewCollection() {
        val newId = if (collections.isEmpty()) 1 else collections.maxByOrNull { it.id }!!.id + 1
        val newCollection = Collection(
            id = newId,
            title = "Новая коллекция ${newId}",
            description = "Описание коллекции",
            items = mutableListOf()
        )

        collections.add(newCollection)
        FileUtils.saveCollections(requireContext(), collections)
        adapter.notifyItemInserted(collections.size - 1)
    }
}