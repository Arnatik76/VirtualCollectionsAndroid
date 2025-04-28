package com.example.androidvirtualcollections.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidvirtualcollections.R
import com.example.androidvirtualcollections.adapter.CollectionAdapter
import com.example.androidvirtualcollections.model.Collection
import com.example.androidvirtualcollections.model.MediaItem
import androidx.navigation.fragment.findNavController

class CatalogFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CollectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)
        recyclerView = view.findViewById(R.id.catalogRecyclerView)

        val catalogCollections = listOf(
            Collection(
                id = 1,
                title = "Нумизматика",
                description = "Коллекционирование монет",
                imageResId = R.drawable.ic_catalog,
                items = mutableListOf(
                    MediaItem(1, "Редкие монеты", "Коллекция античных монет", R.drawable.ic_catalog),
                    MediaItem(2, "Современные монеты", "Монеты XX-XXI веков", R.drawable.ic_catalog)
                )
            ),
            Collection(
                id = 2,
                title = "Филателия",
                description = "Коллекционирование марок",
                imageResId = R.drawable.ic_catalog,
                items = mutableListOf(
                    MediaItem(3, "Марки мира", "Филателистическая коллекция", R.drawable.ic_catalog)
                )
            ),
            Collection(
                id = 3,
                title = "Фотографии",
                description = "Коллекционирование фотографий",
                imageResId = R.drawable.ic_catalog,
                items = mutableListOf(
                    MediaItem(4, "Старые фотографии", "Винтажные фотографии 19-20 веков", R.drawable.ic_catalog)
                )
            ),
            Collection(
                id = 4,
                title = "Литература",
                description = "Коллекционирование книг",
                imageResId = R.drawable.ic_catalog,
                items = mutableListOf(
                    MediaItem(5, "Книжная полка", "Редкие издания классиков", R.drawable.ic_catalog)
                )
            )
        )

        adapter = CollectionAdapter(catalogCollections) { collection ->
            // Переход к детальному фрагменту с передачей выбранной коллекции (категории)
            val action = CatalogFragmentDirections.actionCatalogFragmentToCollectionDetailFragment(collection)
            findNavController().navigate(action)
            // Toast.makeText(requireContext(), "Выбрана категория: ${collection.title}", Toast.LENGTH_SHORT).show() // Можно убрать Toast
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        return view
    }
}