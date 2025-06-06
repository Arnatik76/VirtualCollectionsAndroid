package com.example.finalproject.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.databinding.ItemCommentBinding
import com.example.finalproject.models.CollectionComment

class CommentAdapter(private val onAuthorClicked: (Long?) -> Unit) :
    ListAdapter<CollectionComment, CommentAdapter.CommentViewHolder>(CommentDiffCallback()) {

    // private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding, onAuthorClicked)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CommentViewHolder(
        private val binding: ItemCommentBinding,
        private val onAuthorClicked: (Long?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CollectionComment) {
            binding.commentText.text = comment.commentText
            binding.commentAuthorName.text = comment.user?.displayName ?: comment.user?.username ?: itemView.context.getString(R.string.unknown_author)
            binding.commentDate.text = " "

            Glide.with(itemView.context)
                .load(comment.user?.avatarUrl)
                .placeholder(R.drawable.ic_profile_24)
                .error(R.drawable.ic_profile_24)
                .circleCrop()
                .into(binding.commentAuthorAvatar)

            binding.commentAuthorAvatar.setOnClickListener { onAuthorClicked(comment.userId) }
            binding.commentAuthorName.setOnClickListener { onAuthorClicked(comment.userId) }
        }
    }

    class CommentDiffCallback : DiffUtil.ItemCallback<CollectionComment>() {
        override fun areItemsTheSame(oldItem: CollectionComment, newItem: CollectionComment): Boolean {
            return oldItem.commentId == newItem.commentId
        }
        override fun areContentsTheSame(oldItem: CollectionComment, newItem: CollectionComment): Boolean {
            return oldItem == newItem
        }
    }
}