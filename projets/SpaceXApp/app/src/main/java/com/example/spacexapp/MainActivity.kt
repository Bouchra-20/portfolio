package com.example.spacexapp

import LaunchesViewModelFactory
import com.example.spacexapp.data.AppDatabase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.room.Room
import com.example.spacexapp.data.PreferencesManager
import com.example.spacexapp.ui.theme.SpaceXAppTheme
import com.example.spacexapp.network.NetworkMonitor
import com.example.spacexapp.room.LaunchRepository
import com.example.spacexapp.ui.navigation.MainNavigation

/**
 * The entry point of the application.
 *
 * This Activity is responsible for:
 * 1. Initializing the Room Database using a lazy delegate.
 * 2. Setting up the core infrastructure (Network Monitoring, Preferences, Repository).
 * 3. Bootstrapping the Dependency Injection via [LaunchesViewModelFactory].
 * 4. Hosting the Jetpack Compose UI and [MainNavigation].
 */
class MainActivity : ComponentActivity() {
    /**
     * Lazy-initialized Room database instance.
     * Includes [fallbackToDestructiveMigration] to handle schema changes by
     * recreating the database during development.
     */
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "spacex.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Called when the activity is starting.
     *
     * Sets up the edge-to-edge display and wires together the data layer
     * before providing the [LaunchesViewModelFactory] to the [MainNavigation] composable.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down, this contains the most recent data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val networkMonitor = NetworkMonitor(applicationContext)
        val prefsRepo = PreferencesManager(applicationContext)
        val repo = LaunchRepository(db.launchDao())
        val factory = LaunchesViewModelFactory(repo, prefsRepo, networkMonitor)

        setContent {
            SpaceXAppTheme {
                MainNavigation(factory)
            }
        }
    }
}
