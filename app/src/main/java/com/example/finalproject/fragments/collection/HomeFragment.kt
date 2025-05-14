package com.example.finalproject.fragments.collection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.finalproject.R
import com.example.finalproject.api.RetrofitClient // Добавьте этот импорт
import com.example.finalproject.models.Collection // Добавьте этот импорт
import retrofit2.Call // Добавьте этот импорт
import retrofit2.Callback // Добавьте этот импорт
import retrofit2.Response // Добавьте этот импорт

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Вызовите вашу тестовую функцию здесь
        testGetSpecificCollection()
    }

    // Ваша тестовая функция
    fun testGetSpecificCollection() {
        // 1. Получите токен аутентификации (если эндпоинт требует авторизации)
        // ЗАМЕНИТЕ "your_actual_auth_token" НА РЕАЛЬНЫЙ ТОКЕН, если нужно
        // ... (остальной код функции testGetSpecificCollection как в вашем примере)

        Log.d("ApiService", "Запрос коллекции с ID 200...")

        // 2. Выполните API-запрос
        // Убедитесь, что BASE_URL в RetrofitClient.kt корректен для эмулятора:
        // "http://10.0.2.2:8080/" если ваш сервер на localhost:8080
        RetrofitClient.instance.getCollections(200)
            .enqueue(object : Callback<Collection> {
                override fun onResponse(call: Call<Collection>, response: Response<Collection>) {
                    if (response.isSuccessful) {
                        val collection = response.body()
                        Log.d("ApiService", "Коллекция успешно получена: ${collection?.title}")
                        Log.d("ApiService", "ID коллекции: ${collection?.collectionId}")
                    } else {
                        Log.e("ApiService", "Ошибка получения коллекции: ${response.code()}")
                        Log.e("ApiService", "Тело ошибки: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Collection>, t: Throwable) {
                    Log.e("ApiService", "Сбой при получении коллекции", t)
                }
            })
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}