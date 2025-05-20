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
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentUserProfileBinding
import com.example.finalproject.models.User
import com.example.finalproject.utils.AuthTokenProvider
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    // TODO: Если планируется просмотр чужих профилей, добавить аргумент userId и API для его загрузки
    // private val args: UserProfileFragmentArgs by navArgs()
    // private var profileUserId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Определить, чей профиль смотрим (текущего пользователя или другого)
        // profileUserId = args.userId.let { if (it == -1) null else it }

        // Пока что всегда отображаем профиль текущего авторизованного пользователя
        loadCurrentUserProfile()

        binding.editProfileButton.setOnClickListener {
            // TODO: Проверить, является ли текущий пользователь владельцем этого профиля
            // if (profileUserId == null || profileUserId == AuthTokenProvider.getCurrentUser()?.userId) {
            if (AuthTokenProvider.isAuthenticated()) {
                try {
                    findNavController().navigate(R.id.editProfile) // Убедитесь, что ID верный в nav_graph
                } catch (e: Exception) {
                    Toast.makeText(context, "Не удалось перейти к редактированию: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    Log.e("UserProfileFragment", "Navigation to EditProfile failed", e)
                }
            } else {
                Toast.makeText(context, getString(R.string.user_not_logged_in), Toast.LENGTH_SHORT).show()
            }
        }

        binding.logoutButton.setOnClickListener {
            // TODO: Кнопка выхода должна быть видна/активна только если это профиль текущего пользователя
            showLogoutConfirmationDialog()
        }
    }

    private fun loadCurrentUserProfile() {
        setLoading(true)
        val currentUser = AuthTokenProvider.getCurrentUser()

        if (currentUser != null) {
            displayProfileData(currentUser)
            setLoading(false)
            binding.profileErrorText.isVisible = false
            binding.profileContentGroup.isVisible = true
        } else {
            Log.w("UserProfileFragment", "Current user data not found in AuthTokenProvider.")
            setLoading(false)
            binding.profileErrorText.text = getString(R.string.user_not_logged_in)
            binding.profileErrorText.isVisible = true
            binding.profileContentGroup.isVisible = false
            // Скрыть кнопки, если пользователь не авторизован
            binding.editProfileButton.isEnabled = false
            binding.logoutButton.isEnabled = false
        }
        // Если бы мы загружали профиль по ID с сервера:
        // RetrofitClient.instance.getUserProfile(profileUserId!!).enqueue(...)
    }

    private fun displayProfileData(user: User) {
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

        // TODO: Показываем/скрываем кнопку редактирования в зависимости от того,
        // является ли текущий пользователь владельцем просматриваемого профиля.
        // В данном случае, мы всегда смотрим свой профиль, так что кнопка активна.
        binding.editProfileButton.isEnabled = true
        binding.logoutButton.isEnabled = true
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

        // Переход на экран входа
        // Важно: убедитесь, что у вас есть глобальное действие или правильный путь к экрану логина
        // из любого места графа навигации, если UserProfileFragment доступен из разных мест.
        // Самый простой способ - это перейти к стартовому экрану графа навигации,
        // который затем перенаправит на логин, если пользователь не аутентифицирован.
        try {
            // Переход на LoginFragment и очистка бэкстека до него (если LoginFragment не стартовый)
            // Либо переход на стартовый фрагмент, который решит, куда направить дальше
            findNavController().navigate(R.id.login, null, androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true) // Очистить весь backstack до nav_graph
                .build())

            // Если MainActivity управляет видимостью BottomNav, нужно ей сообщить
            // (activity as? MainActivity)?.showBottomNavigation(false) // Пример
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка навигации после выхода: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            Log.e("UserProfileFragment", "Navigation to login after logout failed", e)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.profileProgressBar.isVisible = isLoading
        binding.profileContentGroup.isVisible = !isLoading && !binding.profileErrorText.isVisible
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return getString(R.string.placeholder_date)
        // Пример входного формата: "2024-05-20T10:30:00Z" или "2024-05-20T10:30:00.123456"
        val inputFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        )
        val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())

        for (format in inputFormats) {
            format.timeZone = TimeZone.getTimeZone("UTC") // Если даты с сервера в UTC
            try {
                val date = format.parse(dateString)
                if (date != null) {
                    outputFormat.timeZone = TimeZone.getDefault() // Форматируем в локальную зону
                    return outputFormat.format(date)
                }
            } catch (e: ParseException) {
                // Продолжаем попытки с другими форматами
            }
        }
        Log.w("UserProfileFragment", "Could not parse date: $dateString")
        return dateString // Возвращаем исходную строку, если не удалось распарсить
    }


    override fun onResume() {
        super.onResume()
        // Если пользователь мог обновить данные профиля на экране EditProfile,
        // и мы возвращаемся сюда, стоит перезагрузить данные.
        loadCurrentUserProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}