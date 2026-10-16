package com.example.spacexapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spacexapp.viewmodel.LaunchesViewModel

/**
 * Screen dedicated to displaying the user's bookmarked (favorite) launches.
 *
 * Features:
 * - Reactive Updates: Uses [collectAsStateWithLifecycle] to listen to favorite
 *   status changes in the local database.
 * - Empty State: Displays a centered informational message if no favorites are saved.
 * - Shared Layout: Reuses [LaunchListContent] for a consistent look and feel with the main list.
 *
 * @param viewModel The [LaunchesViewModel] providing the [filteredFavorites] stream.
 * @param onLaunchClick Callback triggered when a favorite item is selected,
 * navigation usually leads to the detail screen.
 */
@Composable
fun FavoritesScreen(viewModel: LaunchesViewModel, onLaunchClick: (String) -> Unit) {
    val favorites = viewModel.filteredFavorites.collectAsStateWithLifecycle()

    if (favorites.value.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aucun favori enregistré")
        }
    } else {
        LaunchListContent(favorites.value, viewModel, onLaunchClick)
    }
}