package com.example.finalproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.databinding.ItemCollectionItemEntryBinding
import com.example.finalproject.models.CollectionItemEntry

class CollectionItemAdapter(private val onItemClicked: (CollectionItemEntry) -> Unit) :
    ListAdapter<CollectionItemEntry, CollectionItemAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCollectionItemEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(
        private val binding: ItemCollectionItemEntryBinding,
        private val onItemClicked: (CollectionItemEntry) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: CollectionItemEntry) {
            val mediaItem = entry.mediaItem
            binding.itemTitle.text = mediaItem.title
            binding.itemCreator.text = mediaItem.creator ?: itemView.context.getString(R.string.unknown_author) // или другой текст

            mediaItem.contentType?.typeName?.let {
                binding.itemType.text = it
                binding.itemType.visibility = View.VISIBLE
            } ?: run {
                binding.itemType.visibility = View.GONE
            }

            entry.notes?.let {
                binding.itemNotes.text = it
                binding.itemNotes.visibility = View.VISIBLE
            } ?: run {
                binding.itemNotes.visibility = View.GONE
            }

            Glide.with(itemView.context)
                .load(mediaItem.thumbnailUrl)
                .placeholder(R.drawable.ic_image_placeholder_24)
                .error(R.drawable.ic_image_placeholder_24)
                .into(binding.itemThumbnail)

            itemView.setOnClickListener {
                onItemClicked(entry)
            }
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<CollectionItemEntry>() {
        override fun areItemsTheSame(oldItem: CollectionItemEntry, newItem: CollectionItemEntry): Boolean {
            return oldItem.mediaItem.itemId == newItem.mediaItem.itemId && oldItem.collectionId == newItem.collectionId
        }

        override fun areContentsTheSame(oldItem: CollectionItemEntry, newItem: CollectionItemEntry): Boolean {
            return oldItem == newItem
        }
    }
}