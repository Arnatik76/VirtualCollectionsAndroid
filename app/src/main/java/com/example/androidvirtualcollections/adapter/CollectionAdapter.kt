package com.example.androidvirtualcollections.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidvirtualcollections.R
import com.example.androidvirtualcollections.model.Collection

class CollectionAdapter(
    private val collections: List<Collection>,
    private val onItemClick: (Collection) -> Unit
) : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.collectionImage)
        val title: TextView = itemView.findViewById(R.id.collectionTitle)
        val itemCount: TextView = itemView.findViewById(R.id.collectionItemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val collection = collections[position]
        holder.title.text = collection.title
        holder.itemCount.text = "${collection.items?.size ?: 0} предметов"
        holder.image.setImageResource(collection.imageResId)

        holder.itemView.setOnClickListener { onItemClick(collection) }
    }

    override fun getItemCount() = collections.size
}