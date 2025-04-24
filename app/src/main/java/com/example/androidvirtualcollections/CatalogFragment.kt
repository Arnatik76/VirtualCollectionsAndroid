package com.example.androidvirtualcollections

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CatalogFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CatalogAdapter
    private val catalogItems = mutableListOf<CatalogItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)
        recyclerView = view.findViewById(R.id.catalogRecyclerView)
        loadCatalogData()
        setupRecyclerView()
        return view
    }

    private fun loadCatalogData() {
        val savedItems = FileUtils.loadCatalog(requireContext())
        if (savedItems.isNotEmpty()) {
            catalogItems.clear()
            catalogItems.addAll(savedItems)
        } else {
            // Заполняем демо-данными если файла нет
            catalogItems.clear()
            catalogItems.addAll(listOf(
                CatalogItem(1, "Название предмета 1", "Описание предмета 1", R.drawable.ic_catalog),
                CatalogItem(2, "Название предмета 2", "Описание предмета 2", R.drawable.ic_catalog),
                CatalogItem(3, "Название предмета 3", "Описание предмета 3", R.drawable.ic_catalog)
            ))
            // Сохраняем демо-данные
            FileUtils.saveCatalog(requireContext(), catalogItems)
        }
    }

    private fun setupRecyclerView() {
        adapter = CatalogAdapter(catalogItems)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }
}