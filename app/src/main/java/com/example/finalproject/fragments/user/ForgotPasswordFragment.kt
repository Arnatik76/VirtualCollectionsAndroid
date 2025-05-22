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
import com.example.finalproject.databinding.FragmentForgotPasswordBinding
import com.example.finalproject.models.request.ForgotPasswordRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sendButton.setOnClickListener {
            handleForgotPasswordRequest()
        }
    }

    private fun handleForgotPasswordRequest() {
        val email = binding.emailEditText.text.toString().trim()

        if (!isValidEmail(email)) {
            binding.emailTextInputLayout.error = getString(R.string.error_invalid_email)
            return
        } else {
            binding.emailTextInputLayout.error = null
        }

        setLoading(true)

        val forgotPasswordRequest = ForgotPasswordRequest(email)
        RetrofitClient.instance.forgotPassword(forgotPasswordRequest)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Инструкции по сбросу пароля отправлены на вашу почту.", Toast.LENGTH_LONG).show()
                        findNavController().popBackStack()
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Неизвестная ошибка сервера"
                        Toast.makeText(context, "Ошибка: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                        Log.e("ForgetPassword", "API Error: ${response.code()} - $errorBody")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(context, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("ForgetPassword", "Network failure", t)
                }
            })
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.sendButton.isEnabled = !isLoading
        binding.emailEditText.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}