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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.adapters.CollectionAdapter
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.databinding.FragmentMyCollectionsBinding
import com.example.finalproject.models.Collection
import com.example.finalproject.models.responce.PagedResponse
import com.example.finalproject.utils.AuthTokenProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCollectionsFragment : Fragment() {

    private var _binding: FragmentMyCollectionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var collectionAdapter: CollectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCollectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!AuthTokenProvider.isAuthenticated()) {
            showNotAuthenticatedState()
            return
        }

        setupRecyclerView()
        fetchMyCollections()

        binding.fabCreateCollection.setOnClickListener {
            try {
                // Используем ID из nav_graph, так как CreateCollectionFragment не принимает аргументов
                // и у него может не быть сгенерированного action в MyCollectionsFragmentDirections
                // Если action есть, то лучше:
                // findNavController().navigate(MyCollectionsFragmentDirections.actionMyCollectionsFragmentToCreateCollectionFragment())
                findNavController().navigate(R.id.createCollectionFragment)
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.error_cannot_create_collection) + ": ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("MyCollectionsFragment", "Navigation to createCollectionFragment failed", e)
            }
        }
    }

    private fun showNotAuthenticatedState() {
        Log.w("MyCollectionsFragment", "User not authenticated. Cannot load collections.")
        binding.myCollectionsEmptyViewText.text = getString(R.string.please_login_to_see_collections)
        binding.myCollectionsEmptyViewText.isVisible = true
        binding.myCollectionsRecyclerView.isVisible = false
        binding.myCollectionsProgressBar.isVisible = false
        binding.fabCreateCollection.isEnabled = false // Деактивируем кнопку создания
        Toast.makeText(context, getString(R.string.please_login_to_see_collections), Toast.LENGTH_LONG).show()
    }

    private fun setupRecyclerView() {
        collectionAdapter = CollectionAdapter { collection ->
            Log.d("MyCollectionsFragment", "Clicked collection ID: ${collection.collectionId}")
            try {
                val action = MyCollectionsFragmentDirections.actionMyCollectionsFragmentToCollectionDetailsFragment(collection.collectionId)
                findNavController().navigate(action)
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.error_cannot_open_collection_details) + ": ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("MyCollectionsFragment", "Navigation to CollectionDetailsFragment failed", e)
            }
        }
        binding.myCollectionsRecyclerView.apply {
            adapter = collectionAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun fetchMyCollections() {
        setLoading(true)
        binding.myCollectionsEmptyViewText.isVisible = false

        RetrofitClient.instance.getMyCollections(size = 20) // Загружаем до 20 коллекций на страницу
            .enqueue(object : Callback<PagedResponse<Collection>> {
                override fun onResponse(call: Call<PagedResponse<Collection>>, response: Response<PagedResponse<Collection>>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        val pagedResponse = response.body()
                        if (pagedResponse != null) {
                            val collections = pagedResponse.content
                            if (collections.isNotEmpty()) {
                                collectionAdapter.submitList(collections)
                                binding.myCollectionsRecyclerView.isVisible = true
                            } else {
                                binding.myCollectionsRecyclerView.isVisible = false
                                binding.myCollectionsEmptyViewText.text = getString(R.string.no_my_collections_found)
                                binding.myCollectionsEmptyViewText.isVisible = true
                            }
                        } else {
                            handleApiError(getString(R.string.error_loading_my_collections) + " (пустой ответ)")
                        }
                    } else {
                        if (response.code() == 401) { // Unauthorized
                            Toast.makeText(context, "Сессия истекла. Пожалуйста, войдите снова.", Toast.LENGTH_LONG).show()
                            AuthTokenProvider.clearAuthData()
                            // Можно добавить автоматический переход на экран логина
                            // (activity as? MainActivity)?.navController?.navigate(R.id.login)
                            showNotAuthenticatedState() // Показать состояние "не авторизован"
                        } else {
                            handleApiError("Ошибка ${response.code()}: ${response.message()}")
                        }
                    }
                }

                override fun onFailure(call: Call<PagedResponse<Collection>>, t: Throwable) {
                    setLoading(false)
                    handleApiError(getString(R.string.network_error) + ": ${t.localizedMessage}")
                    Log.e("MyCollectionsFragment", "API Failure", t)
                }
            })
    }

    private fun handleApiError(errorMessage: String) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        binding.myCollectionsRecyclerView.isVisible = false
        if (binding.myCollectionsEmptyViewText.text.toString() == getString(R.string.no_my_collections_found) ||
            binding.myCollectionsEmptyViewText.text.toString() == getString(R.string.please_login_to_see_collections)) {
            // Не перезаписываем сообщение о пустом списке или необходимости логина, если это была API ошибка
        } else {
            binding.myCollectionsEmptyViewText.text = errorMessage
        }
        binding.myCollectionsEmptyViewText.isVisible = true
    }

    private fun setLoading(isLoading: Boolean) {
        binding.myCollectionsProgressBar.isVisible = isLoading
        if (!isLoading) {
            // Показываем RecyclerView только если есть элементы и загрузка завершена
            binding.myCollectionsRecyclerView.isVisible = collectionAdapter.itemCount > 0
        } else {
            binding.myCollectionsRecyclerView.isVisible = false
        }
        binding.fabCreateCollection.isEnabled = !isLoading
    }

    override fun onResume() {
        super.onResume()
        // Если пользователь мог разлогиниться или залогиниться на другом экране,
        // и мы возвращаемся сюда, стоит проверить аутентификацию и перезагрузить данные.
        if (AuthTokenProvider.isAuthenticated() && collectionAdapter.itemCount == 0) {
            // Если авторизован, но список пуст (например, после возврата с экрана логина)
            fetchMyCollections()
        } else if (!AuthTokenProvider.isAuthenticated()) {
            showNotAuthenticatedState()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.myCollectionsRecyclerView.adapter = null // Очистка адаптера
        _binding = null
    }
}