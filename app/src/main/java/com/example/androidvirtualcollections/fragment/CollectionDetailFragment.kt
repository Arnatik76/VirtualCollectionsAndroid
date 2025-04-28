package com.example.androidvirtualcollections.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.androidvirtualcollections.R
import com.example.androidvirtualcollections.adapter.CatalogAdapter // Используем CatalogAdapter
import com.example.androidvirtualcollections.model.MediaItem

class CollectionDetailFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CatalogAdapter
    private val args: CollectionDetailFragmentArgs by navArgs() // Получаем аргументы

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection_detail, container, false)
        recyclerView = view.findViewById(R.id.collectionItemsRecyclerView)

        val collection = args.collection // Получаем переданную коллекцию
        setupRecyclerView(collection.items)

        // Устанавливаем заголовок ActionBar (если используется)
        activity?.title = collection.title

        return view
    }

    private fun setupRecyclerView(items: List<MediaItem>) {
        adapter = CatalogAdapter(items) { mediaItem ->
            // Обработка нажатия на элемент внутри коллекции (например, показать детали)
            Toast.makeText(requireContext(), "Выбран предмет: ${mediaItem.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
        // Можно использовать LinearLayoutManager или GridLayoutManager по желанию
        // recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }
}