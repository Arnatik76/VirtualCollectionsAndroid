package com.example.finalproject.fragments.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.databinding.FragmentUserProfileBinding
import com.example.finalproject.models.User
import com.example.finalproject.utils.AuthTokenProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val args: UserProfileFragmentArgs by navArgs()
    private var profileUserIdToLoad: Long? = null
    private var currentlyDisplayedUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileUserIdToLoad = if (args.userId == -1L) {
            AuthTokenProvider.getCurrentUser()?.userId
        } else {
            args.userId
        }

        loadUserProfileData()

        binding.editProfileButton.setOnClickListener {
            if (isCurrentUserProfileOwner()) {
                currentlyDisplayedUser?.userId?.let { userId ->
                    val action = UserProfileFragmentDirections.actionUserProfileFragmentToEditProfile(userId)
                    findNavController().navigate(action)
                } ?: Toast.makeText(context, "Ошибка: ID пользователя не найден", Toast.LENGTH_SHORT).show()
            }
        }

        binding.logoutButton.setOnClickListener {
            if (isCurrentUserProfileOwner()) {
                showLogoutConfirmationDialog()
            }
        }
    }

    private fun isCurrentUserProfileOwner(): Boolean {
        val loggedInUserId = AuthTokenProvider.getCurrentUser()?.userId
        return loggedInUserId != null && currentlyDisplayedUser?.userId == loggedInUserId
    }

    private fun loadUserProfileData() {
        setLoading(true)
        binding.profileErrorText.isVisible = false
        binding.profileContentGroup.isVisible = false

        if (profileUserIdToLoad == null && args.userId == -1L) {
            val currentUser = AuthTokenProvider.getCurrentUser()
            if (currentUser != null) {
                profileUserIdToLoad = currentUser.userId
                displayProfileData(currentUser)
                setLoading(false)
            } else {
                showError(getString(R.string.user_not_logged_in))
                setLoading(false)
                updateButtonVisibility()
            }
            return
        }

        if (profileUserIdToLoad == null) {
            showError(getString(R.string.user_not_found) + " (ID не указан)")
            setLoading(false)
            updateButtonVisibility()
            return
        }

        RetrofitClient.instance.getUserProfileById(profileUserIdToLoad!!)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            displayProfileData(user)
                        } ?: showError(getString(R.string.user_not_found))
                    } else {
                        showError("${getString(R.string.error_loading_profile)}: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    setLoading(false)
                    showError("${getString(R.string.network_error)}: ${t.localizedMessage}")
                    Log.e("UserProfileFragment", "Failed to load profile", t)
                }
            })
    }

    private fun displayProfileData(user: User) {
        currentlyDisplayedUser = user

        binding.usernameTextView.text = user.username
        binding.displayNameTextView.text = user.displayName ?: getString(R.string.default_display_name)
        binding.emailTextView.text = user.email
        binding.bioTextView.text = user.bio ?: getString(R.string.default_bio)

        binding.joinedDateTextView.text = formatDate(user.createdAt)
        binding.lastLoginDateTextView.text = user.lastLogin?.let { formatDate(it) } ?: getString(R.string.placeholder_date)

        Glide.with(this)
            .load(user.avatarUrl)
            .placeholder(R.drawable.ic_profile_24)
            .error(R.drawable.ic_profile_24)
            .circleCrop()
            .into(binding.profileAvatarImageView)

        binding.profileContentGroup.isVisible = true
        updateButtonVisibility()
    }

    private fun updateButtonVisibility() {
        val isOwner = isCurrentUserProfileOwner()
        binding.editProfileButton.isVisible = isOwner
        binding.logoutButton.isVisible = isOwner
    }


    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirm_logout_title))
            .setMessage(getString(R.string.confirm_logout_message))
            .setPositiveButton(getString(R.string.action_yes)) { dialog, _ ->
                performLogout()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.action_no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performLogout() {
        AuthTokenProvider.clearAuthData()
        Toast.makeText(context, getString(R.string.logged_out_successfully), Toast.LENGTH_SHORT).show()
        try {
            findNavController().navigate(R.id.login, null, androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build())
        } catch (e: Exception) {
            Log.e("UserProfileFragment", "Navigation to login after logout failed", e)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.profileProgressBar.isVisible = isLoading
        if (!isLoading) {
            if (!binding.profileErrorText.isVisible) binding.profileContentGroup.isVisible = true
        } else {
            binding.profileContentGroup.isVisible = false
        }
    }

    private fun showError(message: String) {
        binding.profileErrorText.text = message
        binding.profileErrorText.isVisible = true
        binding.profileContentGroup.isVisible = false
        updateButtonVisibility()
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return getString(R.string.placeholder_date)
        val inputFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        )
        val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())

        for (format in inputFormats) {
            if (!dateString.contains("Z") && format.toPattern().endsWith("'Z'")) {
            } else {
                format.timeZone = TimeZone.getTimeZone("UTC")
            }
            try {
                val date = format.parse(dateString)
                if (date != null) {
                    outputFormat.timeZone = TimeZone.getDefault()
                    return outputFormat.format(date)
                }
            } catch (e: ParseException) {
            }
        }
        Log.w("UserProfileFragment", "Could not parse date: $dateString, returning as is.")
        return dateString
    }

    override fun onResume() {
        super.onResume()
        val currentLoggedInUserId = AuthTokenProvider.getCurrentUser()?.userId
        val targetUserIdForDisplay = if (args.userId == -1L) currentLoggedInUserId else args.userId

        if (profileUserIdToLoad != targetUserIdForDisplay || currentlyDisplayedUser == null) {
            profileUserIdToLoad = targetUserIdForDisplay
            loadUserProfileData()
        } else {
            if (isCurrentUserProfileOwner()) {
                AuthTokenProvider.getCurrentUser()?.let {
                    if (it != currentlyDisplayedUser) {
                        displayProfileData(it)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        currentlyDisplayedUser = null
    }
}