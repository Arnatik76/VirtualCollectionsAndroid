package com.example.androidvirtualcollections

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class CollectionAdapter(private val collections: List<Collection>) : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.collectionImage)
        val title: TextView = itemView.findViewById(R.id.collectionTitle)
        val itemCount: TextView = itemView.findViewById(R.id.collectionItemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_collection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val collection = collections[position]
        holder.title.text = collection.title
        holder.itemCount.text = "${collection.itemCout} предметов"
        holder.image.setImageResource(collection.imageUrl)
    }

    override fun getItemCount() = collections.size
}