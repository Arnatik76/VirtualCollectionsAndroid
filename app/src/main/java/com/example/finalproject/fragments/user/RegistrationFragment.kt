package com.example.finalproject.fragments.user

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finalproject.R
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.databinding.FragmentRegistrationBinding
import com.example.finalproject.models.AuthResponse
import com.example.finalproject.models.RegistrationRequest
// import com.example.finalproject.utils.AuthTokenProvider // Не нужен здесь, т.к. после регистрации обычно перенаправляем на логин
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener {
            handleRegistration()
        }

        binding.loginNowTextView.setOnClickListener {
            // Переход на экран логина, если пользователь уже имеет аккаунт
            findNavController().navigate(R.id.action_registration_to_login)
        }
    }

    private fun handleRegistration() {
        val username = binding.usernameEditText.text.toString().trim()
        val email = binding.emailEditTextRegister.text.toString().trim()
        val displayName = binding.displayNameEditText.text.toString().trim().let { if (it.isEmpty()) null else it }
        val password = binding.passwordEditTextRegister.text.toString() // Не trim, чтобы не обрезать пробелы в пароле
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        var isValid = true

        // Валидация имени пользователя
        if (username.isEmpty()) {
            binding.usernameTextInputLayout.error = getString(R.string.error_empty_username)
            isValid = false
        } else {
            binding.usernameTextInputLayout.error = null
        }

        // Валидация email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailTextInputLayoutRegister.error = getString(R.string.error_invalid_email)
            isValid = false
        } else {
            binding.emailTextInputLayoutRegister.error = null
        }

        // Валидация пароля
        if (password.isEmpty()) {
            binding.passwordTextInputLayoutRegister.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (password.length < 6) {
            binding.passwordTextInputLayoutRegister.error = getString(R.string.error_password_too_short)
            isValid = false
        } else {
            binding.passwordTextInputLayoutRegister.error = null
        }

        // Валидация подтверждения пароля
        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordTextInputLayout.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (password != confirmPassword) {
            binding.confirmPasswordTextInputLayout.error = getString(R.string.error_passwords_do_not_match)
            isValid = false
        } else {
            binding.confirmPasswordTextInputLayout.error = null
        }

        if (!isValid) return

        setLoading(true)

        val registrationRequest = RegistrationRequest(username, email, password, displayName)

        RetrofitClient.instance.registerUser(registrationRequest)
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        // ВАЖНО: Сервер возвращает AuthResponse, но мы не сохраняем токен здесь.
                        // Вместо этого перенаправляем пользователя на экран входа.
                        // Либо, если API спроектировано так, что регистрация сразу логинит,
                        // то нужно сохранить токен и пользователя, как в LoginFragment.
                        // Сейчас предполагаем, что после регистрации нужно залогиниться отдельно.
                        val authResponse = response.body()
                        Log.d("RegistrationFragment", "Registration successful: ${authResponse?.user?.username}")
                        Toast.makeText(context, getString(R.string.registration_successful), Toast.LENGTH_LONG).show()

                        // Переход на экран логина
                        findNavController().navigate(R.id.action_registration_to_login)

                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Неизвестная ошибка сервера"
                        Toast.makeText(context, "${getString(R.string.registration_failed)}: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                        Log.e("RegistrationFragment", "Registration failed: ${response.code()} - $errorBody")
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(context, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("RegistrationFragment", "Network error", t)
                }
            })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarRegister.isVisible = isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.usernameEditText.isEnabled = !isLoading
        binding.emailEditTextRegister.isEnabled = !isLoading
        binding.displayNameEditText.isEnabled = !isLoading
        binding.passwordEditTextRegister.isEnabled = !isLoading
        binding.confirmPasswordEditText.isEnabled = !isLoading
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}