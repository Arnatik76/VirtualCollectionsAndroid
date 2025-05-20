package com.example.finalproject.fragments.collection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.finalproject.R
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.databinding.FragmentAddItemBinding
import com.example.finalproject.models.CollectionItemEntry
import com.example.finalproject.models.request.AddItemToCollectionRequest
import com.example.finalproject.utils.AuthTokenProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val args: AddItemFragmentArgs by navArgs()
    private var collectionId: Long = -1L

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

        binding.addItemButton.setOnClickListener {
            if (!AuthTokenProvider.isAuthenticated()) {
                Toast.makeText(context, R.string.please_login_to_see_collections, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (collectionId != -1L) {
                handleAddItemToCollection()
            } else {
                Toast.makeText(context, getString(R.string.collection_id_missing), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleAddItemToCollection() {
        val mediaItemIdString = binding.mediaItemIdEditText.text.toString().trim()
        val notes = binding.itemNotesEditText.text.toString().trim().ifEmpty { null }

        if (mediaItemIdString.isEmpty()) {
            binding.mediaItemIdTextInputLayout.error = getString(R.string.error_media_item_id_required)
            return
        } else {
            binding.mediaItemIdTextInputLayout.error = null
        }

        val mediaItemId = mediaItemIdString.toLongOrNull()
        if (mediaItemId == null) {
            binding.mediaItemIdTextInputLayout.error = "ID должен быть числом"
            return
        }

        setLoading(true)

        val addItemRequest = AddItemToCollectionRequest(mediaItemId = mediaItemId, notes = notes)
        RetrofitClient.instance.addItemToCollection(collectionId, addItemRequest)
            .enqueue(object : Callback<CollectionItemEntry> {
                override fun onResponse(call: Call<CollectionItemEntry>, response: Response<CollectionItemEntry>) {
                    setLoading(false)
                    if (response.isSuccessful && response.code() == 201) {
                        val newItemEntry = response.body()
                        Toast.makeText(context, getString(R.string.item_added_successfully), Toast.LENGTH_SHORT).show()
                        Log.d("AddItemFragment", "Item added: ${newItemEntry?.mediaItem?.title} to collection $collectionId")
                        // TODO: Опционально: передать результат обратно в CollectionDetailsFragment, чтобы он обновил список
                        findNavController().popBackStack()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: getString(R.string.error_adding_item)
                        val displayError = "${getString(R.string.error_adding_item)}: ${response.code()} $errorMsg"
                        Toast.makeText(context, displayError, Toast.LENGTH_LONG).show()
                        Log.e("AddItemFragment", "API Error ${response.code()}: $errorMsg")
                    }
                }

                override fun onFailure(call: Call<CollectionItemEntry>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(context, "${getString(R.string.network_error)}: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("AddItemFragment", "Network Failure", t)
                }
            })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.addItemProgressBar.isVisible = isLoading
        binding.mediaItemIdEditText.isEnabled = !isLoading
        binding.itemNotesEditText.isEnabled = !isLoading
        binding.addItemButton.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}