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
import com.example.finalproject.R
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.databinding.FragmentCreateCollectionBinding
import com.example.finalproject.models.Collection
import com.example.finalproject.models.request.CreateCollectionRequest
import com.example.finalproject.utils.AuthTokenProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditCollectionFragment : Fragment() {

    private var _binding: FragmentCreateCollectionBinding? = null
    private val binding get() = _binding!!

    // TODO: Add argument for collectionId if implementing edit mode
    // private val args: EditCollectionFragmentArgs by navArgs()
    // private var isEditMode = false
    // private var existingCollectionId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Check for edit mode arguments
        // arguments?.let {
        //     existingCollectionId = EditCollectionFragmentArgs.fromBundle(it).collectionId (if -1, then create mode)
        //     if (existingCollectionId != -1 && existingCollectionId != 0) { // Assuming -1 or 0 means not provided for edit
        //         isEditMode = true
        //         setupEditMode()
        //         fetchCollectionDetails(existingCollectionId!!)
        //     } else {
        //         setupCreateMode()
        //     }
        // } ?: setupCreateMode()
        setupCreateMode()

        binding.createCollectionButton.setOnClickListener {
            if (AuthTokenProvider.isAuthenticated()) {
                handleCreateCollection()
            } else {
                Toast.makeText(context, R.string.please_login_to_see_collections, Toast.LENGTH_SHORT).show()
                // Optionally navigate to login
                // findNavController().navigate(R.id.login)
            }
        }
    }

    private fun setupCreateMode() {
        binding.createCollectionHeaderTitle.text = getString(R.string.create_collection_title_toolbar)
        binding.createCollectionButton.text = getString(R.string.button_create_collection)
    }

    // private fun setupEditMode() {
    //     binding.createCollectionHeaderTitle.text = getString(R.string.edit_collection_title_toolbar)
    //     binding.createCollectionButton.text = getString(R.string.button_save_changes)
    // }

    // private fun fetchCollectionDetails(collectionId: Int) {
    //     setLoading(true)
    //
    // }

    private fun handleCreateCollection() {
        val title = binding.titleEditText.text.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim().ifEmpty { null }
        val coverImageUrl = binding.coverUrlEditText.text.toString().trim().ifEmpty { null }
        val isPublic = binding.isPublicSwitch.isChecked

        if (title.isEmpty()) {
            binding.titleTextInputLayout.error = getString(R.string.error_collection_title_required)
            return
        } else {
            binding.titleTextInputLayout.error = null
        }

        setLoading(true)

        val createRequest = CreateCollectionRequest(title, description, coverImageUrl, isPublic)
        RetrofitClient.instance.createCollection(createRequest)
            .enqueue(object : Callback<Collection> {
                override fun onResponse(call: Call<Collection>, response: Response<Collection>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        val newCollection = response.body()
                        Toast.makeText(context, getString(R.string.collection_created_successfully), Toast.LENGTH_SHORT).show()
                        Log.d("EditCollectionFragment", "Collection created: ${newCollection?.title}")
                        findNavController().popBackStack()
                        // newCollection?.collectionId?.let {
                        //     val action = EditCollectionFragmentDirections.actionCreateCollectionFragmentToCollectionDetailsFragment(it)
                        //     findNavController().navigate(action)
                        // }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: getString(R.string.error_creating_collection)
                        Toast.makeText(context, "${getString(R.string.error_creating_collection)}: ${response.code()} $errorMsg", Toast.LENGTH_LONG).show()
                        Log.e("EditCollectionFragment", "API Error ${response.code()}: $errorMsg")
                    }
                }

                override fun onFailure(call: Call<Collection>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(context, "${getString(R.string.network_error)}: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("EditCollectionFragment", "Network Failure", t)
                }
            })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.createCollectionProgressBar.isVisible = isLoading
        binding.titleEditText.isEnabled = !isLoading
        binding.descriptionEditText.isEnabled = !isLoading
        binding.coverUrlEditText.isEnabled = !isLoading
        binding.isPublicSwitch.isEnabled = !isLoading
        binding.createCollectionButton.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}