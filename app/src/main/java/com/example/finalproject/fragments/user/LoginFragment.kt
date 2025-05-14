package com.example.finalproject.fragments.user

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finalproject.R
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.databinding.FragmentLoginBinding
import com.example.finalproject.models.AuthResponse
import com.example.finalproject.models.LoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            handleLogin()
        }

        binding.forgotPasswordTextView.setOnClickListener {
             findNavController().navigate(R.id.action_login_to_forgotPasswordFragment)
        }

        binding.signUpTextView.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_registration)
        }
    }

    private fun handleLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (!isValidEmail(email)) {
            binding.emailTextInputLayout.error = getString(R.string.error_invalid_email)
            return
        } else {
            binding.emailTextInputLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordTextInputLayout.error = getString(R.string.error_empty_password)
            return
        } else {
            binding.passwordTextInputLayout.error = null
        }

        binding.loginButton.isEnabled = false

        val loginRequest = LoginRequest(email, password)
        RetrofitClient.instance.loginUser(loginRequest)
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    binding.loginButton.isEnabled = true

                    if (response.isSuccessful) {
                        val authResponse = response.body()
                        if (authResponse != null) {
                            Toast.makeText(context, "Вход успешен! Token: ${authResponse.token}", Toast.LENGTH_LONG).show()
                            // TODO: Сохранить токен и данные пользователя (например, в SharedPreferences)
                            try {
                                findNavController().navigate(R.id.action_login_to_homeFragment)
                            } catch (e: IllegalArgumentException) {
                                Toast.makeText(context, "Ошибка навигации: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                Log.e("LoginFragment", "Navigation error", e)
                            }
                        } else {
                            Toast.makeText(context, "Ошибка входа: Пустой ответ от сервера", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Неизвестная ошибка сервера"
                        Toast.makeText(context, "Ошибка входа: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                        Log.e("LoginFragment", "Login failed: ${response.code()} - $errorBody")
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    binding.loginButton.isEnabled = true
                    Toast.makeText(context, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("LoginFragment", "Network error", t)
                }
            })
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}