package com.example.finalproject.fragments.collection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.databinding.FragmentAddItemBinding // Убедитесь, что имя байндинга правильное
import com.example.finalproject.models.CollectionItemEntry
import com.example.finalproject.models.MediaItem
import com.example.finalproject.models.request.AddItemToCollectionRequest
import com.example.finalproject.models.request.CreateMediaItemRequest
import com.example.finalproject.utils.AuthTokenProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val args: AddItemFragmentArgs by navArgs()
    private var collectionId: Long = -1L

    private val releaseDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectionId = args.collectionId
        if (collectionId == -1L) {
            Toast.makeText(context, getString(R.string.collection_id_missing), Toast.LENGTH_LONG).show()
            Log.e("AddItemFragment", "Collection ID is missing.")
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.thumbnailUrlNewMediaTextField.doOnTextChanged { text, _, _, _ ->
            Glide.with(this)
                .load(text.toString().trim())
                .placeholder(R.drawable.ic_image_placeholder_24)
                .error(R.drawable.ic_image_placeholder_24)
                .into(binding.newMediaItemThumbnailPreview)
        }

        binding.addItemButton.setOnClickListener {
            if (!AuthTokenProvider.isAuthenticated()) {
                Toast.makeText(context, R.string.please_login_to_see_collections, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (collectionId != -1L) {
                handleCreateAndAddItemToCollection()
            } else {
                Toast.makeText(context, getString(R.string.collection_id_missing), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateMediaItemFields(): CreateMediaItemRequest? {
        val title = binding.titleNewMediaTextField.text.toString().trim()
        val typeIdString = binding.typeIdNewMediaTextField.text.toString().trim()

        if (title.isEmpty()) {
            binding.titleNewMediaLayout.error = getString(R.string.error_media_item_title_required)
            return null
        } else {
            binding.titleNewMediaLayout.error = null
        }

        if (typeIdString.isEmpty()) {
            binding.typeIdNewMediaLayout.error = "ID типа контента обязателен"
            return null
        }
        val typeId = typeIdString.toLongOrNull()
        if (typeId == null) {
            binding.typeIdNewMediaLayout.error = "ID типа должен быть числом"
            return null
        } else {
            binding.typeIdNewMediaLayout.error = null
        }

        val creator = binding.creatorNewMediaTextField.text.toString().trim().ifEmpty { null }
        val description = binding.descriptionNewMediaTextField.text.toString().trim().ifEmpty { null }
        val thumbnailUrl = binding.thumbnailUrlNewMediaTextField.text.toString().trim().ifEmpty { null }
        val externalUrl = binding.externalUrlNewMediaTextField.text.toString().trim().ifEmpty { null }
        val releaseDateString = binding.releaseDateNewMediaTextField.text.toString().trim().ifEmpty { null }

        if (releaseDateString != null) {
            try {
                releaseDateFormat.isLenient = false
                releaseDateFormat.parse(releaseDateString)
            } catch (e: ParseException) {
                binding.releaseDateNewMediaLayout.error = "Неверный формат даты (гггг-мм-дд)"
                return null
            }
            binding.releaseDateNewMediaLayout.error = null
        }

        return CreateMediaItemRequest(
            title = title,
            typeId = typeId,
            creator = creator,
            description = description,
            thumbnailUrl = thumbnailUrl,
            externalUrl = externalUrl,
            releaseDate = releaseDateString
        )
    }


    private fun handleCreateAndAddItemToCollection() {
        val createMediaItemRequest = validateMediaItemFields() ?: return
        val notesForItemCollection = binding.itemNotesEditText.text.toString().trim().ifEmpty { null }

        setLoading(true)

        // 1. Создаем MediaItem
        RetrofitClient.instance.createMediaItem(createMediaItemRequest)
            .enqueue(object : Callback<MediaItem> {
                override fun onResponse(call: Call<MediaItem>, response: Response<MediaItem>) {
                    if (response.isSuccessful) {
                        val createdMediaItem = response.body()
                        if (createdMediaItem != null) {
                            Log.d("AddItemFragment", "MediaItem created: ID ${createdMediaItem.itemId}")
                            // 2. Добавляем созданный MediaItem в коллекцию
                            addNewlyCreatedItemToCollection(createdMediaItem.itemId, notesForItemCollection)
                        } else {
                            setLoading(false)
                            Toast.makeText(context, "Ошибка создания медиа-элемента: пустой ответ", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        setLoading(false)
                        val errorMsg = response.errorBody()?.string() ?: "Ошибка создания медиа-элемента"
                        Toast.makeText(context, "Ошибка ${response.code()}: $errorMsg", Toast.LENGTH_LONG).show()
                        Log.e("AddItemFragment", "API Error creating MediaItem ${response.code()}: $errorMsg")
                    }
                }

                override fun onFailure(call: Call<MediaItem>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(context, "Сетевая ошибка при создании медиа-элемента: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("AddItemFragment", "Network Failure creating MediaItem", t)
                }
            })
    }

    private fun addNewlyCreatedItemToCollection(mediaItemId: Long, notes: String?) {
        val addItemToCollectionRequest = AddItemToCollectionRequest(mediaItemId = mediaItemId, notes = notes)

        RetrofitClient.instance.addItemToCollection(collectionId, addItemToCollectionRequest)
            .enqueue(object : Callback<CollectionItemEntry> {
                override fun onResponse(call: Call<CollectionItemEntry>, response: Response<CollectionItemEntry>) {
                    setLoading(false) // Завершаем общую загрузку здесь
                    if (response.isSuccessful && response.code() == 201) {
                        Toast.makeText(context, getString(R.string.item_added_successfully), Toast.LENGTH_SHORT).show()
                        Log.d("AddItemFragment", "Item ID $mediaItemId added to collection $collectionId")
                        findNavController().popBackStack()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: getString(R.string.error_adding_item)
                        Toast.makeText(context, "Ошибка добавления в коллекцию ${response.code()}: $errorMsg", Toast.LENGTH_LONG).show()
                        Log.e("AddItemFragment", "API Error adding item to collection ${response.code()}: $errorMsg")
                        // Важно: MediaItem мог быть создан, но не добавлен в коллекцию. Нужна логика отката или информирования.
                    }
                }

                override fun onFailure(call: Call<CollectionItemEntry>, t: Throwable) {
                    setLoading(false) // Завершаем общую загрузку здесь
                    Toast.makeText(context, "Сетевая ошибка при добавлении в коллекцию: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("AddItemFragment", "Network Failure adding item to collection", t)
                }
            })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.addItemProgressBar.isVisible = isLoading
        // Блокируем все поля ввода
        binding.titleNewMediaTextField.isEnabled = !isLoading
        binding.typeIdNewMediaTextField.isEnabled = !isLoading
        binding.creatorNewMediaTextField.isEnabled = !isLoading
        binding.descriptionNewMediaTextField.isEnabled = !isLoading
        binding.thumbnailUrlNewMediaTextField.isEnabled = !isLoading
        binding.externalUrlNewMediaTextField.isEnabled = !isLoading
        binding.releaseDateNewMediaTextField.isEnabled = !isLoading
        binding.itemNotesEditText.isEnabled = !isLoading
        binding.addItemButton.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}