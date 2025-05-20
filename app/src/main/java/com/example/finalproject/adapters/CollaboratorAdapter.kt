package com.example.finalproject.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.databinding.ItemCollaboratorBinding
import com.example.finalproject.models.User // Предполагаем, что collaborators это List<User>

class CollaboratorAdapter(private val onCollaboratorClicked: (User) -> Unit) :
    ListAdapter<User, CollaboratorAdapter.CollaboratorViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollaboratorViewHolder {
        val binding = ItemCollaboratorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollaboratorViewHolder(binding, onCollaboratorClicked)
    }

    override fun onBindViewHolder(holder: CollaboratorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CollaboratorViewHolder(
        private val binding: ItemCollaboratorBinding,
        private val onCollaboratorClicked: (User) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.collaboratorName.text = user.displayName ?: user.username
            Glide.with(itemView.context)
                .load(user.avatarUrl)
                .placeholder(R.drawable.ic_profile_24)
                .error(R.drawable.ic_profile_24)
                .circleCrop()
                .into(binding.collaboratorAvatar)
            itemView.setOnClickListener { onCollaboratorClicked(user) }
        }
    }

    // Можно использовать общий UserDiffCallback, если он уже есть
    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.userId == newItem.userId
        }
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}