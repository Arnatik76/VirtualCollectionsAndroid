package com.example.finalproject.fragments.collection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.adapters.CollectionItemAdapter
import com.example.finalproject.adapters.CommentAdapter
import com.example.finalproject.adapters.CollaboratorAdapter
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.databinding.FragmentCollectionDetailsBinding
import com.example.finalproject.models.Collection
import com.example.finalproject.models.CollectionComment
import com.example.finalproject.models.CollectionItemEntry
import com.example.finalproject.models.request.CreateCommentRequest
import com.example.finalproject.models.responce.PagedResponse
import com.example.finalproject.models.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectionDetailsFragment : Fragment() {

    private var _binding: FragmentCollectionDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: CollectionDetailsFragmentArgs by navArgs()

    private lateinit var itemsAdapter: CollectionItemAdapter
    private lateinit var commentsAdapter: CommentAdapter
    private lateinit var collaboratorAdapter: CollaboratorAdapter

    private var currentCollection: Collection? = null
    private var isLikedByCurrentUser: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        fetchCollectionDetails(args.collectionId)

        binding.fabLikeCollection.setOnClickListener {
            handleLikeClicked()
        }
        binding.postCommentButton.setOnClickListener {
            postComment()
        }
        binding.fabAddItemToCollection.setOnClickListener {
            currentCollection?.collectionId?.let { collId ->
                val action = CollectionDetailsFragmentDirections.actionCollectionDetailsFragmentToAddItemFragment(collId)
                findNavController().navigate(action)
            }
        }
    }

    private fun setupAdapters() {
        itemsAdapter = CollectionItemAdapter { itemEntry ->
            val action = CollectionDetailsFragmentDirections.actionCollectionDetailsFragmentToMediaItemDetailsFragment(itemEntry.mediaItem.itemId)
            findNavController().navigate(action)
        }
        binding.collectionItemsRecyclerView.apply {
            adapter = itemsAdapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }

        commentsAdapter = CommentAdapter { userId ->
            userId?.let {
                val action = CollectionDetailsFragmentDirections.actionCollectionDetailsFragmentToUserProfileFragment(it.toLong())
                findNavController().navigate(action)
            }
        }
        binding.commentsRecyclerView.apply {
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }

        collaboratorAdapter = CollaboratorAdapter { user ->
            user.userId?.let { collaboratorUserId ->
                val action = CollectionDetailsFragmentDirections.actionCollectionDetailsFragmentToUserProfileFragment(collaboratorUserId)
                findNavController().navigate(action)
            }
        }
        binding.collaboratorsRecyclerView.apply {
            adapter = collaboratorAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            isNestedScrollingEnabled = false
        }
    }

    private fun fetchCollectionDetails(collectionId: Long) {
        setLoading(true)
        RetrofitClient.instance.getCollectionById(collectionId)
            .enqueue(object : Callback<Collection> {
                override fun onResponse(call: Call<Collection>, response: Response<Collection>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        response.body()?.let { collection ->
                            currentCollection = collection
                            displayCollectionDetails(collection)
                            updateLikeButtonState()
                            fetchAdditionalData(collection)
                        } ?: handleApiError(getString(R.string.error_loading_collection_details) + " (пустой ответ)")
                    } else {
                        handleApiError("Ошибка ${response.code()}: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Collection>, t: Throwable) {
                    setLoading(false)
                    handleApiError(getString(R.string.network_error) + ": ${t.localizedMessage}")
                    Log.e("CollectionDetails", "API Failure", t)
                }
            })
    }

    private fun fetchAdditionalData(collection: Collection) {
        if (collection.items.isNullOrEmpty()) {
            fetchItems(collection.collectionId)
        } else {
            itemsAdapter.submitList(collection.items)
        }
        fetchComments(collection.collectionId)

        if (!collection.collaborators.isNullOrEmpty()) {
            binding.collaboratorsTitle.isVisible = true
            binding.collaboratorsRecyclerView.isVisible = true
            binding.divider3.isVisible = true
            collaboratorAdapter.submitList(collection.collaborators)
        } else {
            binding.collaboratorsTitle.isVisible = false
            binding.collaboratorsRecyclerView.isVisible = false
            binding.divider3.isVisible = false
        }
    }


    private fun fetchItems(collectionId: Long) {
        RetrofitClient.instance.getCollectionItems(collectionId)
            .enqueue(object : Callback<PagedResponse<CollectionItemEntry>> {
                override fun onResponse(call: Call<PagedResponse<CollectionItemEntry>>, response: Response<PagedResponse<CollectionItemEntry>>) {
                    if (response.isSuccessful) {
                        response.body()?.content?.let { itemsAdapter.submitList(it) }
                    } else {
                        Log.e("CollectionDetails", "Error fetching items: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<PagedResponse<CollectionItemEntry>>, t: Throwable) {
                    Log.e("CollectionDetails", "Failure fetching items", t)
                }
            })
    }

    private fun fetchComments(collectionId: Long) {
        RetrofitClient.instance.getCollectionComments(collectionId)
            .enqueue(object : Callback<PagedResponse<CollectionComment>> {
                override fun onResponse(call: Call<PagedResponse<CollectionComment>>, response: Response<PagedResponse<CollectionComment>>) {
                    if (response.isSuccessful) {
                        response.body()?.content?.let { commentsAdapter.submitList(it) }
                    } else {
                        Log.e("CollectionDetails", "Error fetching comments: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<PagedResponse<CollectionComment>>, t: Throwable) {
                    Log.e("CollectionDetails", "Failure fetching comments", t)
                }
            })
    }


    private fun displayCollectionDetails(collection: Collection) {
        binding.collectionDetailTitle.text = collection.title
        val authorName = collection.owner?.displayName ?: collection.owner?.username ?: getString(R.string.unknown_author)
        binding.collectionDetailAuthor.text = getString(R.string.author_prefix, authorName)
        binding.collectionDetailAuthor.setOnClickListener {
            collection.owner?.userId?.let { userId ->
                Toast.makeText(context, "Открыть профиль автора ID: $userId", Toast.LENGTH_SHORT).show()
                val action = CollectionDetailsFragmentDirections.actionCollectionDetailsFragmentToUserProfileFragment(userId)
                findNavController().navigate(action)
            }
        }

        binding.collectionDetailDescription.text = collection.description
        binding.collectionDetailDescription.isVisible = !collection.description.isNullOrEmpty()

        binding.collectionDetailLikesCount.text = formatCount(collection.likeCount ?: 0)
        binding.collectionDetailViewsCount.text = formatCount(collection.viewCount ?: 0)
        binding.collectionDetailCommentsCount.text = (collection.commentCount ?: 0).toString()

        Glide.with(this)
            .load(collection.coverImageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_image_placeholder_24)
            .into(binding.collectionDetailCoverImage)

        collection.items?.let { itemsAdapter.submitList(it) }
    }

    private fun handleLikeClicked() {
        val collectionId = currentCollection?.collectionId ?: return
        setLikeButtonLoading(true)

        val call = if (isLikedByCurrentUser) {
            RetrofitClient.instance.unlikeCollection(collectionId)
        } else {
            RetrofitClient.instance.likeCollection(collectionId)
        }

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                setLikeButtonLoading(false)
                if (response.isSuccessful) {
                    isLikedByCurrentUser = !isLikedByCurrentUser
                    updateLikeButtonState()
                    val currentLikes = currentCollection?.likeCount ?: 0
                    val newLikes = if (isLikedByCurrentUser) currentLikes + 1 else maxOf(0, currentLikes - 1)
                    binding.collectionDetailLikesCount.text = formatCount(newLikes)
                    currentCollection = currentCollection?.copy(likeCount = newLikes)
                } else {
                    Toast.makeText(context, "Ошибка при ${if(isLikedByCurrentUser) "снятии" else "установке"} лайка", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                setLikeButtonLoading(false)
                Toast.makeText(context, "Сетевая ошибка при лайке", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun postComment() {
        val commentText = binding.commentEditText.text.toString().trim()
        if (commentText.isEmpty()) {
            binding.commentInputLayout.error = "Комментарий не может быть пустым"
            return
        }
        binding.commentInputLayout.error = null
        val collectionId = currentCollection?.collectionId ?: return

        setCommentButtonLoading(true)
        val request = CreateCommentRequest(commentText)
        RetrofitClient.instance.addCollectionComment(collectionId, request)
            .enqueue(object : Callback<CollectionComment>{
                override fun onResponse(call: Call<CollectionComment>, response: Response<CollectionComment>) {
                    setCommentButtonLoading(false)
                    if (response.isSuccessful) {
                        response.body()?.let { newComment ->
                            val currentComments = commentsAdapter.currentList.toMutableList()
                            currentComments.add(0, newComment)
                            commentsAdapter.submitList(currentComments)
                            binding.commentsRecyclerView.scrollToPosition(0)
                            binding.commentEditText.text = null
                            Toast.makeText(context, getString(R.string.comment_posted_successfully), Toast.LENGTH_SHORT).show()
                            val currentCommentCount = currentCollection?.commentCount ?: 0
                            binding.collectionDetailCommentsCount.text = (currentCommentCount + 1).toString()
                            currentCollection = currentCollection?.copy(commentCount = currentCommentCount + 1)
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.error_posting_comment) + ": ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<CollectionComment>, t: Throwable) {
                    setCommentButtonLoading(false)
                    Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateLikeButtonState() {
        if (isLikedByCurrentUser) {
            binding.fabLikeCollection.setImageResource(R.drawable.ic_like_filled_24)
            binding.fabLikeCollection.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.color_liked)
        } else {
            binding.fabLikeCollection.setImageResource(R.drawable.ic_like_outline_24)
            binding.fabLikeCollection.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
    }
    private fun setLikeButtonLoading(isLoading: Boolean) {
        binding.fabLikeCollection.isEnabled = !isLoading
    }
    private fun setCommentButtonLoading(isLoading: Boolean) {
        binding.postCommentButton.isEnabled = !isLoading
        binding.commentEditText.isEnabled = !isLoading
    }

    private fun setLoading(isLoading: Boolean) {
        binding.collectionDetailProgressBar.isVisible = isLoading
    }

    private fun handleApiError(errorMessage: String) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
            count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
            else -> count.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.collectionItemsRecyclerView.adapter = null
        binding.commentsRecyclerView.adapter = null
        binding.collaboratorsRecyclerView.adapter = null
        _binding = null
    }
}