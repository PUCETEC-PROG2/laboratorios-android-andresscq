package ec.edu.puce.githubclient.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.githubclient.models.Repository
import ec.edu.puce.githubclient.models.RepositoryPayload
import ec.edu.puce.githubclient.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RepoListViewModel : ViewModel() {

    private val _repos = MutableStateFlow<List<Repository>>(emptyList())
    val repos: StateFlow<List<Repository>> = _repos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg.asStateFlow()

    init {
        fetchRepos()
    }

    fun fetchRepos() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            try {
                // Se añade un parámetro de tiempo para evitar caché y ver cambios inmediatos
                _repos.value = RetrofitClient.apiService.getRepositories(t = System.currentTimeMillis().toString())
            } catch (e: Exception) {
                _errorMsg.value = "Error al cargar repositorios: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteRepo(owner: String, repoName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            try {
                val response = RetrofitClient.apiService.deleteRepository(owner, repoName)
                if (response.isSuccessful) {
                    fetchRepos() // Actualización reactiva
                } else {
                    _errorMsg.value = "Error al eliminar: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMsg.value = "Error al eliminar: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateRepo(owner: String, originalName: String, newName: String, description: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            try {
                RetrofitClient.apiService.updateRepository(
                    owner,
                    originalName,
                    RepositoryPayload(newName, description)
                )
                fetchRepos() // Actualización reactiva
            } catch (e: Exception) {
                _errorMsg.value = "Error al actualizar: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
