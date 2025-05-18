package com.example.finalproject.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.databinding.ItemCollectionCardBinding
import com.example.finalproject.models.Collection

class CollectionAdapter(private val onItemClicked: (Collection) -> Unit) :
    ListAdapter<Collection, CollectionAdapter.CollectionViewHolder>(CollectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding = ItemCollectionCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = getItem(position)
        holder.bind(collection)
    }

    class CollectionViewHolder(
        private val binding: ItemCollectionCardBinding,
        private val onItemClicked: (Collection) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(collection: Collection) {
            binding.collectionTitleTextView.text = collection.title

            val authorName = collection.owner?.displayName ?: collection.owner?.username ?: itemView.context.getString(R.string.unknown_author)
            binding.collectionAuthorTextView.text = itemView.context.getString(R.string.author_prefix, authorName)

            binding.collectionLikesCountTextView.text = formatCount(collection.likeCount ?: 0)
            binding.collectionViewCountTextView.text = formatCount(collection.viewCount ?: 0) // viewCount у тебя не nullable

            Glide.with(itemView.context)
                .load(collection.coverImageUrl) // Убедись, что URL полный и рабочий
                .placeholder(R.drawable.ic_launcher_background) // Стандартная заглушка
                .error(R.drawable.ic_image_placeholder_24) // Заглушка при ошибке (создай ее)
                .into(binding.collectionCoverImageView)

            itemView.setOnClickListener {
                onItemClicked(collection)
            }
        }

        // Простая функция для форматирования чисел (1200 -> 1.2K)
        private fun formatCount(count: Int): String {
            return when {
                count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
                count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
                else -> count.toString()
            }
        }
    }

    class CollectionDiffCallback : DiffUtil.ItemCallback<Collection>() {
        override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return oldItem.collectionId == newItem.collectionId
        }

        override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            // Сравнивай по нужным полям, если объект Collection может меняться без смены ID
            return oldItem == newItem
        }
    }
}