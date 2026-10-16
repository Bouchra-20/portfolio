package com.example.spacexapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacexapp.data.PreferencesManager
import com.example.spacexapp.domain.GetFilteredLaunchesUseCase
import com.example.spacexapp.domain.ManageFavoritesJsonUseCase
import com.example.spacexapp.model.Launch
import kotlinx.coroutines.launch
import com.example.spacexapp.room.LaunchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.spacexapp.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Represents the filtering criteria for launch mission outcomes. */
enum class StatusFilter {
    /** Show all launches regardless of outcome. */
    ALL,
    /** Show only successfully completed missions. */
    SUCCESS,
    /** Show only failed missions. */
    FAILURE
}
/**
 * Represents the chronological sorting order for the launch list.
 */
enum class DateSort {
    /** Sort from most recent to oldest dates. */
    NEWEST,
    /** Sort from oldest to most recent dates. */
    OLDEST
}

/**
 * Error state constants used to identify specific failure types during data operations.
 */
/** Indicates a successful operation with no errors. */
const val ERROR_NONE = 0

/** Triggered when the device has no internet connectivity or the host is unreachable. */
const val ERROR_NETWORK = 1

/** Triggered when the server request exceeds the pre-defined time limit. */
const val ERROR_TIMEOUT = 2

/** Triggered by 401/403 HTTP errors or invalid API credentials. */
const val ERROR_AUTH = 3

/** Indicates that the request succeeded but returned an empty list of results. */
const val ERROR_EMPTY = 4

/** Catch-all state for unexpected exceptions or generic processing failures. */
const val ERROR_UNKNOWN = 5

/**
 * Main ViewModel for the SpaceX application.
 *
 * Orchestrates the data flow between the Repository, User Preferences (DataStore),
 * and the UI. It handles network monitoring, reactive filtering, and JSON import/export.
 *
 * Key Features:
 * - **Reactive Filtering**: Uses [snapshotFlow] and [flatMapLatest] to update lists
 *   instantly when search queries or filters change.
 * - **Network Awareness**: Monitors connectivity and triggers automatic refreshes
 *   when the device comes back online.
 * - **State Management**: Exposes [StateFlow]s for UI components to collect data
 *   safely with lifecycle awareness.
 */
class LaunchesViewModel(private val repo: LaunchRepository,
                        private val prefs: PreferencesManager,
                        private val manageFavoritesJsonUseCase: ManageFavoritesJsonUseCase,
                        private val getFilteredLaunches: GetFilteredLaunchesUseCase,
                        private val networkMonitor: NetworkMonitor) : ViewModel()
{
    private val _launchesFlow = repo.getLaunchesFlow()
    val launches: Flow<List<Launch>> = _launchesFlow
    private val _isLoading = mutableStateOf(true)
    val isLoading: Boolean get() = _isLoading.value
    var searchQuery by mutableStateOf("")
    var statusFilter by mutableStateOf(StatusFilter.ALL)
    var dateSort by mutableStateOf(DateSort.NEWEST)
    var isOnline by mutableStateOf(true)
        private set
    var errorType by mutableStateOf(ERROR_NONE)
    var showOnlineSnackbar by mutableStateOf(false)

    /**
     * Initializes the ViewModel by loading saved user preferences and
     * starting network observation.
     */
    init {
        viewModelScope.launch {
            try {
                val initialPrefs = prefs.userPreferencesFlow.first()
                dateSort = initialPrefs.dateSort
                statusFilter = initialPrefs.statusFilter
            } catch (e: Exception) {
            }
            observeNetwork()
            loadLaunches(refreshRemote = isOnline)

            prefs.userPreferencesFlow.collect { preferences ->
                dateSort = preferences.dateSort
                statusFilter = preferences.statusFilter
            }
        }
    }

    /**
     * Observes the device's network connectivity status in real-time.
     *
     * This private function reacts to connectivity changes by:
     * 1. Updating the observable [isOnline] property to inform the UI of current status.
     * 2. Detecting a "reconnection" event ([gainedConnection]) when the status transitions
     *    from offline to online.
     * 3. Triggering an automatic remote data refresh and notifying the user via
     *    [showOnlineSnackbar] only upon successful reconnection.
     */
    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                val gainedConnection = !isOnline && online
                isOnline = online

                if (gainedConnection) {
                    showOnlineSnackbar = true
                    loadLaunches(refreshRemote = true)
                }
            }
        }
    }

    /**
     * Triggers a data fetch from the repository.
     * @param refreshRemote If true, attempts to fetch fresh data from the SpaceX API.
     */
     fun loadLaunches(refreshRemote: Boolean = false) {
         viewModelScope.launch(Dispatchers.IO) {

             withContext(Dispatchers.Main) {
                 _isLoading.value = true
             }
             errorType = ERROR_NONE

             try {
                 if (refreshRemote && isOnline) {
                     repo.refreshFromRemote()
                 }

                 val currentLaunches = repo.getLaunchesFlow().first()
                 if (currentLaunches.isEmpty()) {
                     withContext(Dispatchers.Main) {
                         errorType = ERROR_EMPTY
                     }
                 }

             } catch (e: Exception) {
                 withContext(Dispatchers.Main) {
                     errorType = when (e) {
                         is java.net.SocketTimeoutException -> ERROR_TIMEOUT
                         is java.net.UnknownHostException -> ERROR_NETWORK
                         else -> ERROR_UNKNOWN
                     }
                 }
             } finally {
                 withContext(Dispatchers.Main) { _isLoading.value = false }
             }
         }
    }

    /**
     * Persists the favorite status of a launch to the local database.
     */
    fun toggleFavorite(launch: Launch) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repo.toggleFavorite(launch.id, !launch.isFavorite)
                }

            } catch (e: Exception) {
                Log.e("SpaceX", "Erreur lors du toggle favori", e)
            }
        }
    }
    /**
     * Blacklists a launch ID and removes it from the local database.
     */
    fun deleteLaunch(launchId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteLaunch(launchId = launchId)
        }
    }
    /**
     * Exports the current list of launches to a JSON formatted string.
     */
    suspend fun getFavoritesJson(): String {
        return manageFavoritesJsonUseCase.exportToJson(launches)
    }
    /**
     * Parses a JSON string and updates the local database with new favorite launches.
     */
    fun importFavorites(json: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                errorType = ERROR_NONE
                manageFavoritesJsonUseCase.importFromJson(json)
            } catch (e: Exception) {
                errorType = ERROR_UNKNOWN
            } finally {
                _isLoading.value = false
            }
        }
    }
    /**
     * Reactive stream of launches filtered by the same criteria but restricted
     * to items marked as favorites.
     */
    val filteredFavorites: StateFlow<List<Launch>> = snapshotFlow {
        Triple(searchQuery, statusFilter, dateSort)
    }.flatMapLatest { (q, s, d) ->
        val favoritesOnlyFlow = _launchesFlow.map { list -> list.filter { it.isFavorite } }
        getFilteredLaunches(favoritesOnlyFlow, q, d, s)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Reactive stream of launches filtered by search query, status, and date sort.
     * Collected by the main list UI.
     */
    val filteredLaunches: StateFlow<List<Launch>> = snapshotFlow {
        Triple(searchQuery, statusFilter, dateSort)
    }.flatMapLatest { (q, s, d) ->
        getFilteredLaunches(_launchesFlow,q, d, s)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    /**
     * Updates the date sorting preference in the DataStore.
     */
    fun updateDateSort(newSort: DateSort) {
        viewModelScope.launch { prefs.updateDateSort(newSort) }
    }
    /**
     * Updates the mission status filter (All, Success, or Failure) in the user preferences.
     * @param newFilter The specific [StatusFilter] to be applied to the launch lists.
     */
    fun updateStatusFilter(newFilter: StatusFilter) {
        viewModelScope.launch { prefs.updateStatusFilter(newFilter) }
    }

    /**
     * A cold [StateFlow] exposing the list of deleted launches.
     * It uses [SharingStarted.Lazily] to ensure database observation only happens
     * when the Trash screen is actually visible to the user.
     */
    val trashLaunches: StateFlow<List<Launch>> = repo.getTrashFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Triggers the restoration process for a launch.
     * Executed on a background thread ([Dispatchers.IO]) to ensure UI fluidity.
     *
     * @param launchId The ID of the launch to be recovered.
     */
    fun restoreLaunch(launchId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.restoreLaunch(launchId)
        }
    }

}