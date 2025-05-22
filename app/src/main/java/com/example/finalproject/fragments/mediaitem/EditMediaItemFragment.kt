package com.example.finalproject.fragments.mediaitem

import android.net.ParseException
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.databinding.FragmentEditMediaItemBinding
import com.example.finalproject.models.MediaItem
import com.example.finalproject.models.request.UpdateMediaItemRequest
import com.example.finalproject.utils.AuthTokenProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class EditMediaItemFragment : Fragment() {

    private var _binding: FragmentEditMediaItemBinding? = null
    private val binding get() = _binding!!

    private val args: EditMediaItemFragmentArgs by navArgs()
    private var mediaItemIdToEdit: Long = -1L
    private var currentMediaItemData: MediaItem? = null

    private val releaseDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditMediaItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaItemIdToEdit = args.mediaItemId

        if (mediaItemIdToEdit == -1L) {
            Toast.makeText(context, "Ошибка: ID медиа-элемента не передан.", Toast.LENGTH_LONG).show()
            Log.e("EditMediaItemFragment", "MediaItem ID is missing.")
            findNavController().popBackStack()
            return
        }

        if (!AuthTokenProvider.isAuthenticated()) {
            Toast.makeText(context, "Необходимо авторизоваться для редактирования.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        loadMediaItemData()

        binding.saveMediaItemButton.setOnClickListener {
            handleUpdateMediaItem()
        }

        binding.thumbnailUrlEditMediaTextField.doOnTextChanged { text, _, _, _ ->
            Glide.with(this)
                .load(text.toString().trim())
                .placeholder(R.drawable.ic_image_placeholder_24)
                .error(R.drawable.ic_image_placeholder_24)
                .into(binding.editMediaItemThumbnailPreview)
        }
    }

    private fun loadMediaItemData() {
        setLoading(true)
        RetrofitClient.instance.getMediaItemById(mediaItemIdToEdit)
            .enqueue(object : Callback<MediaItem> {
                override fun onResponse(call: Call<MediaItem>, response: Response<MediaItem>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            currentMediaItemData = it
                            populateFields(it)
                        } ?: run {
                            Toast.makeText(context, getString(R.string.media_item_not_found), Toast.LENGTH_LONG).show()
                            findNavController().popBackStack()
                        }
                    } else {
                        Toast.makeText(context, "${getString(R.string.error_loading_media_item_details)}: ${response.code()}", Toast.LENGTH_LONG).show()
                        Log.e("EditMediaItemFragment", "Error loading media item ${response.code()}: ${response.message()}")
                        findNavController().popBackStack()
                    }
                }

                override fun onFailure(call: Call<MediaItem>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(context, "${getString(R.string.network_error)}: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("EditMediaItemFragment", "Network failure loading media item", t)
                    findNavController().popBackStack()
                }
            })
    }

    private fun populateFields(item: MediaItem) {
        binding.titleEditMediaTextField.setText(item.title)
        binding.creatorEditMediaTextField.setText(item.creator ?: "")
        binding.descriptionEditMediaTextField.setText(item.description ?: "")
        binding.thumbnailUrlEditMediaTextField.setText(item.thumbnailUrl ?: "")
        binding.externalUrlEditMediaTextField.setText(item.externalUrl ?: "")
        binding.releaseDateEditMediaTextField.setText(item.releaseDate ?: "")


        Glide.with(this)
            .load(item.thumbnailUrl)
            .placeholder(R.drawable.ic_image_placeholder_24)
            .error(R.drawable.ic_image_placeholder_24)
            .into(binding.editMediaItemThumbnailPreview)
    }

    private fun handleUpdateMediaItem() {
        val title = binding.titleEditMediaTextField.text.toString().trim()
        if (title.isEmpty()) {
            binding.titleEditMediaLayout.error = getString(R.string.error_media_item_title_required)
            return
        } else {
            binding.titleEditMediaLayout.error = null
        }

        val creator = binding.creatorEditMediaTextField.text.toString().trim().ifEmpty { null }
        val description = binding.descriptionEditMediaTextField.text.toString().trim().ifEmpty { null }
        val thumbnailUrl = binding.thumbnailUrlEditMediaTextField.text.toString().trim().ifEmpty { null }
        val externalUrl = binding.externalUrlEditMediaTextField.text.toString().trim().ifEmpty { null }
        var releaseDateString = binding.releaseDateEditMediaTextField.text.toString().trim().ifEmpty { null }

        if (releaseDateString != null) {
            try {
                releaseDateFormat.isLenient = false
                releaseDateFormat.parse(releaseDateString)
            } catch (e: ParseException) {
                binding.releaseDateEditMediaLayout.error = "Неверный формат даты (гггг-мм-дд)"
                return
            }
            binding.releaseDateEditMediaLayout.error = null
        }

        setLoading(true)

        val updateRequest = UpdateMediaItemRequest(
            title = title,
            creator = creator,
            description = description,
            thumbnailUrl = thumbnailUrl,
            externalUrl = externalUrl,
            releaseDate = releaseDateString
        )

        RetrofitClient.instance.updateMediaItem(mediaItemIdToEdit, updateRequest)
            .enqueue(object : Callback<MediaItem> {
                override fun onResponse(call: Call<MediaItem>, response: Response<MediaItem>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        response.body()?.let { updatedItem ->
                            Toast.makeText(context, getString(R.string.media_item_updated_successfully), Toast.LENGTH_SHORT).show()
                            Log.d("EditMediaItemFragment", "MediaItem updated: ${updatedItem.title}")
                            findNavController().popBackStack()
                        } ?: Toast.makeText(context, getString(R.string.error_updating_media_item) + " (пустой ответ)", Toast.LENGTH_LONG).show()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: getString(R.string.error_updating_media_item)
                        Toast.makeText(context, "${getString(R.string.error_updating_media_item)}: ${response.code()} $errorMsg", Toast.LENGTH_LONG).show()
                        Log.e("EditMediaItemFragment", "API Error ${response.code()}: $errorMsg")
                    }
                }

                override fun onFailure(call: Call<MediaItem>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(context, "${getString(R.string.network_error)}: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("EditMediaItemFragment", "Network Failure", t)
                }
            })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.editMediaItemProgressBar.isVisible = isLoading
        binding.titleEditMediaTextField.isEnabled = !isLoading
        binding.creatorEditMediaTextField.isEnabled = !isLoading
        binding.descriptionEditMediaTextField.isEnabled = !isLoading
        binding.thumbnailUrlEditMediaTextField.isEnabled = !isLoading
        binding.externalUrlEditMediaTextField.isEnabled = !isLoading
        binding.releaseDateEditMediaTextField.isEnabled = !isLoading
        binding.saveMediaItemButton.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}