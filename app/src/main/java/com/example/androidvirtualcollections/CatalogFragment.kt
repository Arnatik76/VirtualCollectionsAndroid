package com.example.androidvirtualcollections

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CatalogFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CatalogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)
        recyclerView = view.findViewById(R.id.catalogRecyclerView)
        setupRecyclerView()
        return view
    }

    private fun setupRecyclerView() {
        val items = listOf(
            CatalogItem(1, "Название предмета 1", "Описание предмета 1", R.drawable.ic_catalog),
            CatalogItem(2, "Название предмета 2", "Описание предмета 2", R.drawable.ic_catalog),
            CatalogItem(3, "Название предмета 3", "Описание предмета 3", R.drawable.ic_catalog))
        )

        adapter = CatalogAdapter(items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }
}