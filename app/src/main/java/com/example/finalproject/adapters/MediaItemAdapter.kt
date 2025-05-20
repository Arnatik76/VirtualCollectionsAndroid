package com.example.finalproject.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.databinding.ItemMediaItemSelectableBinding
import com.example.finalproject.models.MediaItem

class MediaItemAdapter(private val onItemClicked: (MediaItem) -> Unit) :
    ListAdapter<MediaItem, MediaItemAdapter.MediaItemViewHolder>(MediaItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaItemViewHolder {
        val binding = ItemMediaItemSelectableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaItemViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: MediaItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MediaItemViewHolder(
        private val binding: ItemMediaItemSelectableBinding,
        private val onItemClicked: (MediaItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mediaItem: MediaItem) {
            binding.mediaItemTitle.text = mediaItem.title
            binding.mediaItemCreator.text = mediaItem.creator ?: itemView.context.getString(R.string.unknown_author)
            binding.mediaItemType.text = mediaItem.contentType?.typeName ?: "N/A"

            Glide.with(itemView.context)
                .load(mediaItem.thumbnailUrl)
                .placeholder(R.drawable.ic_image_placeholder_24)
                .error(R.drawable.ic_image_placeholder_24)
                .into(binding.mediaItemThumbnail)

            itemView.setOnClickListener {
                onItemClicked(mediaItem)
            }
        }
    }

    class MediaItemDiffCallback : DiffUtil.ItemCallback<MediaItem>() {
        override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
            return oldItem == newItem
        }
    }
}