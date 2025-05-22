package com.example.finalproject.fragments.mediaitem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.databinding.FragmentMediaItemDetailsBinding
import com.example.finalproject.models.MediaItem
import com.example.finalproject.utils.AuthTokenProvider
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class MediaItemDetailsFragment : Fragment() {

    private var _binding: FragmentMediaItemDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: MediaItemDetailsFragmentArgs by navArgs()
    private var currentMediaItem: MediaItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        fetchMediaItemDetails(args.mediaItemId)

        binding.fabEditMediaItem.setOnClickListener {
            currentMediaItem?.let {
                if (AuthTokenProvider.isAuthenticated()) {
                    val action = MediaItemDetailsFragmentDirections.actionMediaItemDetailsFragmentToEditMediaItemFragment(it.itemId)
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(context, "Только авторизованные пользователи могут редактировать", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarMediaDetails)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarMediaDetails.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun fetchMediaItemDetails(itemId: Long) {
        setLoading(true)
        RetrofitClient.instance.getMediaItemById(itemId).enqueue(object : Callback<MediaItem> {
            override fun onResponse(call: Call<MediaItem>, response: Response<MediaItem>) {
                setLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let {
                        currentMediaItem = it
                        displayMediaItemDetails(it)
                    } ?: showError(getString(R.string.media_item_not_found))
                } else {
                    showError("${getString(R.string.error_loading_media_item_details)}: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MediaItem>, t: Throwable) {
                setLoading(false)
                showError("${getString(R.string.network_error)}: ${t.message}")
                Log.e("MediaItemDetails", "API Failure", t)
            }
        })
    }

    private fun displayMediaItemDetails(item: MediaItem) {
        binding.collapsingToolbarLayoutMediaDetails.title = item.title
        binding.toolbarMediaDetails.title = item.title

        binding.mediaItemDetailTitleText.text = item.title
        binding.mediaItemDetailCreatorText.text = item.creator ?: getString(R.string.unknown_author)
        binding.mediaItemDetailTypeText.text = item.contentType?.typeName ?: "N/A"
        binding.mediaItemDetailDescriptionText.text = item.description ?: "Описание отсутствует."

        binding.mediaItemDetailReleaseDateText.text = formatDate(item.releaseDate, "yyyy-MM-dd") ?: "Не указана"
        binding.mediaItemDetailAddedAtText.text = formatDate(item.addedAt) ?: "Неизвестно"

        Glide.with(this)
            .load(item.thumbnailUrl)
            .placeholder(R.drawable.ic_image_placeholder_24)
            .error(R.drawable.ic_image_placeholder_24)
            .into(binding.mediaItemDetailThumbnail)

        if (!item.externalUrl.isNullOrBlank()) {
            binding.buttonOpenExternalUrl.isVisible = true
            binding.buttonOpenExternalUrl.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.externalUrl))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, getString(R.string.cannot_open_url), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            binding.buttonOpenExternalUrl.isVisible = false
        }

        binding.mediaItemDetailTagsChipGroup.removeAllViews()
        if (!item.tags.isNullOrEmpty()) {
            binding.mediaItemDetailTagsChipGroup.isVisible = true
            item.tags.forEach { tag ->
                val chip = Chip(context)
                chip.text = tag.tagName
                binding.mediaItemDetailTagsChipGroup.addView(chip)
            }
        } else {
            binding.mediaItemDetailTagsChipGroup.isVisible = false
        }

        binding.fabEditMediaItem.isVisible = AuthTokenProvider.isAuthenticated()
    }

    private fun formatDate(dateString: String?, inputPattern: String = "yyyy-MM-dd'T'HH:mm:ss"): String? {
        if (dateString.isNullOrEmpty()) return null
        val defaultLocale = Locale.getDefault()

        val inputFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", defaultLocale),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", defaultLocale),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", defaultLocale),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", defaultLocale),
            SimpleDateFormat(inputPattern, defaultLocale)
        )
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", defaultLocale)

        for (format in inputFormats) {
            if (!dateString.contains("Z") && format.toPattern().endsWith("Z")) {
                if(!format.toPattern().contains("'Z'")) continue
            }
            if (format.toPattern().contains("T") && (format.toPattern().endsWith("Z") || dateString.endsWith("Z"))) {
                format.timeZone = TimeZone.getTimeZone("UTC")
            } else if (format.toPattern().contains("T")) {
                format.timeZone = TimeZone.getTimeZone("UTC")
            }


            try {
                val date = format.parse(dateString)
                if (date != null) {
                    outputFormat.timeZone = TimeZone.getDefault()
                    return outputFormat.format(date)
                }
            } catch (e: ParseException) {
                Log.v("MediaItemDetails", "Date parse failed for $dateString with pattern ${format.toPattern()}")
            }
        }
        Log.w("MediaItemDetails", "Could not parse date: $dateString with known formats.")
        return dateString
    }


    private fun setLoading(isLoading: Boolean) {
        binding.mediaItemDetailProgressBar.isVisible = isLoading
        binding.mediaItemDetailErrorText.isVisible = false
    }

    private fun showError(message: String) {
        binding.mediaItemDetailErrorText.text = message
        binding.mediaItemDetailErrorText.isVisible = true
        binding.mediaItemDetailProgressBar.isVisible = false
        binding.collapsingToolbarLayoutMediaDetails.isVisible = false
        binding.fabEditMediaItem.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}