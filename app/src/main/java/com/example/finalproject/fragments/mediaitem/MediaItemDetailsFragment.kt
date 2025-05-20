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
                // TODO: Проверить права пользователя на редактирование этого MediaItem
                // Например, если есть поле createdByUserId в MediaItem и оно совпадает с AuthTokenProvider.getCurrentUser()?.userId
                // Для простоты, пока разрешаем редактирование, если залогинен.
                // В реальном приложении нужна более строгая проверка прав.
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
                // chip.isClickable = true // Если хотите сделать теги кликабельными для поиска по тегу
                // chip.setOnClickListener { /* TODO: Navigate to search by tag */ }
                binding.mediaItemDetailTagsChipGroup.addView(chip)
            }
        } else {
            binding.mediaItemDetailTagsChipGroup.isVisible = false
        }

        // Показать кнопку редактирования, если есть права (здесь упрощенная проверка)
        // TODO: Реализуйте более надежную проверку прав, если это необходимо (например, на основе createdByUserId)
        binding.fabEditMediaItem.isVisible = AuthTokenProvider.isAuthenticated()
    }

    private fun formatDate(dateString: String?, inputPattern: String = "yyyy-MM-dd'T'HH:mm:ss"): String? {
        if (dateString.isNullOrEmpty()) return null
        val defaultLocale = Locale.getDefault()

        // Список возможных входных форматов, включая те, что могут приходить с OffsetDateTime
        val inputFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", defaultLocale),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", defaultLocale),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", defaultLocale),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", defaultLocale),
            SimpleDateFormat(inputPattern, defaultLocale) // Для дат типа releaseDate
        )
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", defaultLocale) // Более простой формат для отображения

        for (format in inputFormats) {
            if (!dateString.contains("Z") && format.toPattern().endsWith("Z")) {
                // Пропускаем форматы с Z, если в строке нет Z, кроме тех, что явно не содержат смещение
                if(!format.toPattern().contains("'Z'")) continue
            }
            // Даты с сервера могут быть в UTC
            if (format.toPattern().contains("T") && (format.toPattern().endsWith("Z") || dateString.endsWith("Z"))) {
                format.timeZone = TimeZone.getTimeZone("UTC")
            } else if (format.toPattern().contains("T")) { // Если время есть, но нет явного UTC, пробуем как локальное или UTC
                format.timeZone = TimeZone.getTimeZone("UTC") // Пробуем UTC как основной вариант для timestamp
            }


            try {
                val date = format.parse(dateString)
                if (date != null) {
                    outputFormat.timeZone = TimeZone.getDefault() // Форматируем в локальную зону
                    return outputFormat.format(date)
                }
            } catch (e: ParseException) {
                // Log.v("MediaItemDetails", "Date parse failed for $dateString with pattern ${format.toPattern()}")
            }
        }
        Log.w("MediaItemDetails", "Could not parse date: $dateString with known formats.")
        return dateString // Возвращаем исходную строку, если не удалось распарсить
    }


    private fun setLoading(isLoading: Boolean) {
        binding.mediaItemDetailProgressBar.isVisible = isLoading
        binding.mediaItemDetailErrorText.isVisible = false
        // Остальной контент управляется через CollapsingToolbarLayout и NestedScrollView
    }

    private fun showError(message: String) {
        binding.mediaItemDetailErrorText.text = message
        binding.mediaItemDetailErrorText.isVisible = true
        binding.mediaItemDetailProgressBar.isVisible = false
        // Скрыть другие элементы, если нужно
        binding.collapsingToolbarLayoutMediaDetails.isVisible = false
        binding.fabEditMediaItem.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}