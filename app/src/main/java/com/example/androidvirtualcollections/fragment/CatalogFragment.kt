package com.example.androidvirtualcollections.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidvirtualcollections.R
import com.example.androidvirtualcollections.adapter.CatalogAdapter
import com.example.androidvirtualcollections.model.MediaItem

class CatalogFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CatalogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)
        recyclerView = view.findViewById(R.id.catalogRecyclerView)

        // Статичные данные каталога (коллекции других пользователей)
        val catalogItems = listOf(
            MediaItem(1, "Редкие монеты", "Коллекция античных монет", R.drawable.ic_catalog),
            MediaItem(2, "Марки мира", "Филателистическая коллекция", R.drawable.ic_catalog),
            MediaItem(3, "Старые фотографии", "Винтажные фотографии 19-20 веков",
                R.drawable.ic_catalog
            ),
            MediaItem(4, "Книжная полка", "Редкие издания классиков", R.drawable.ic_catalog)
        )

        adapter = CatalogAdapter(catalogItems)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        return view
    }
}