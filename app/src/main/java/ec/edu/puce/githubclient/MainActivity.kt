package ec.edu.puce.githubclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ec.edu.puce.githubclient.models.Repository
import ec.edu.puce.githubclient.ui.theme.GithubClientTheme
import ec.edu.puce.githubclient.ui.screens.RepoList
import ec.edu.puce.githubclient.ui.screens.RepoFrom
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.puce.githubclient.viewmodels.RepoListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: RepoListViewModel = viewModel()
            var currentScreen by remember { mutableStateOf("repolist") }
            var selectedRepo by remember { mutableStateOf<Repository?>(null) }

            GithubClientTheme {
                when(currentScreen) {
                    "repolist" -> RepoList(
                        viewModel = viewModel,
                        onNavigateToFrom = { repo ->
                            selectedRepo = repo
                            currentScreen = "repoFrom"
                        }
                    )
                    "repoFrom" -> RepoFrom(
                        repository = selectedRepo,
                        onBackClick = { 
                            currentScreen = "repolist"
                        },
                        onSaveSuccess = {
                            viewModel.fetchRepos() // Refrescar lista tras guardar
                            currentScreen = "repolist"
                        }
                    )
                }
            }
        }
    }
}
