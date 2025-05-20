package com.example.finalproject.fragments.user

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
import com.example.finalproject.databinding.FragmentEditProfileBinding
import com.example.finalproject.models.User
import com.example.finalproject.models.request.UpdateProfileRequest
import com.example.finalproject.utils.AuthTokenProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val args: EditProfileFragmentArgs by navArgs()
    private var userIdToEdit: Long = -1L
    private var currentUserData: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userIdToEdit = args.userId

        if (userIdToEdit == -1L || userIdToEdit != AuthTokenProvider.getCurrentUser()?.userId) {
            Toast.makeText(context, "Ошибка: Невозможно редактировать этот профиль.", Toast.LENGTH_LONG).show()
            Log.e("EditProfileFragment", "Attempted to edit profile with invalid userId: $userIdToEdit")
            findNavController().popBackStack()
            return
        }

        loadCurrentProfileData()

        binding.saveProfileButton.setOnClickListener {
            handleUpdateProfile()
        }

        binding.avatarUrlEditTextField.doOnTextChanged { text, _, _, _ ->
            Glide.with(this)
                .load(text.toString())
                .placeholder(R.drawable.ic_profile_24)
                .error(R.drawable.ic_profile_24)
                .circleCrop()
                .into(binding.editProfileAvatarPreview)
        }
    }

    private fun loadCurrentProfileData() {
        setLoading(true)
        currentUserData = AuthTokenProvider.getCurrentUser()

        if (currentUserData == null) {
            setLoading(false)
            Toast.makeText(context, getString(R.string.error_loading_data_for_edit), Toast.LENGTH_LONG).show()
            Log.e("EditProfileFragment", "Could not load current user data from AuthTokenProvider.")
            findNavController().popBackStack()
            return
        }

        populateFields(currentUserData!!)
        setLoading(false)
    }

    private fun populateFields(user: User) {
        binding.displayNameEditTextField.setText(user.displayName ?: "")
        binding.bioEditTextField.setText(user.bio ?: "")
        binding.avatarUrlEditTextField.setText(user.avatarUrl ?: "")

        Glide.with(this)
            .load(user.avatarUrl)
            .placeholder(R.drawable.ic_profile_24)
            .error(R.drawable.ic_profile_24)
            .circleCrop()
            .into(binding.editProfileAvatarPreview)
    }

    private fun handleUpdateProfile() {
        val displayName = binding.displayNameEditTextField.text.toString().trim().ifEmpty { null }
        val bio = binding.bioEditTextField.text.toString().trim().ifEmpty { null }
        val avatarUrl = binding.avatarUrlEditTextField.text.toString().trim().ifEmpty { null }

        if (displayName == currentUserData?.displayName &&
            bio == currentUserData?.bio &&
            avatarUrl == currentUserData?.avatarUrl) {
            Toast.makeText(context, "Нет изменений для сохранения.", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        val updateRequest = UpdateProfileRequest(displayName, bio, avatarUrl)

        RetrofitClient.instance.updateMyProfile(updateRequest)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        response.body()?.let { updatedUser ->
                            AuthTokenProvider.saveUser(updatedUser)
                            Toast.makeText(context, getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
                            Log.d("EditProfileFragment", "Profile updated for user: ${updatedUser.username}")
                            findNavController().popBackStack()
                        } ?: run {
                            Toast.makeText(context, getString(R.string.error_updating_profile) + " (пустой ответ)", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: getString(R.string.error_updating_profile)
                        Toast.makeText(context, "${getString(R.string.error_updating_profile)}: ${response.code()} $errorMsg", Toast.LENGTH_LONG).show()
                        Log.e("EditProfileFragment", "API Error ${response.code()}: $errorMsg")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(context, "${getString(R.string.network_error)}: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("EditProfileFragment", "Network Failure", t)
                }
            })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.editProfileProgressBar.isVisible = isLoading
        binding.displayNameEditTextField.isEnabled = !isLoading
        binding.bioEditTextField.isEnabled = !isLoading
        binding.avatarUrlEditTextField.isEnabled = !isLoading
        binding.saveProfileButton.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        currentUserData = null
    }
}