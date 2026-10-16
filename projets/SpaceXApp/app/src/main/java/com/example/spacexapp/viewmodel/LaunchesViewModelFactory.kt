import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spacexapp.data.PreferencesManager
import com.example.spacexapp.domain.GetFilteredLaunchesUseCase
import com.example.spacexapp.domain.ManageFavoritesJsonUseCase
import com.example.spacexapp.network.NetworkMonitor
import com.example.spacexapp.room.LaunchRepository
import com.example.spacexapp.viewmodel.LaunchesViewModel

/**
 * Factory responsible for instantiating the [LaunchesViewModel] with its required dependencies.
 *
 * Since the ViewModel requires custom parameters (Repository, Preferences, and Network Monitor)
 * that are not part of the default ViewModel constructor, this Factory pattern is used to
 * handle the Dependency Injection manually.
 *
 * @property repo The [LaunchRepository] providing access to local and remote launch data.
 * @property prefs The [PreferencesManager] handling the persistence of user settings via DataStore.
 * @property networkMonitor The utility used to observe real-time internet connectivity changes.
 */
class LaunchesViewModelFactory(private val repo: LaunchRepository,
                               private val prefs: PreferencesManager,
                               private val networkMonitor: NetworkMonitor) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the [LaunchesViewModel].
     *
     * Inside this method, the required UseCases are initialized:
     * - [GetFilteredLaunchesUseCase]: For complex list filtering and sorting logic.
     * - [ManageFavoritesJsonUseCase]: For JSON data serialization and deserialization.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A fully initialized [LaunchesViewModel].
     * @throws IllegalArgumentException If the provided [modelClass] is not assignable to [LaunchesViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LaunchesViewModel::class.java)) {

            val getFilteredLaunches = GetFilteredLaunchesUseCase()
            val manageFavoritesJsonUseCase = ManageFavoritesJsonUseCase(repo)
            @Suppress("UNCHECKED_CAST")
            return LaunchesViewModel(repo, prefs, manageFavoritesJsonUseCase, getFilteredLaunches, networkMonitor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
