package com.example.spacexapp.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spacexapp.viewmodel.LaunchesViewModel

/**
 * Screen displaying the "Recycle Bin" of the application.
 *
 * Features:
 * - List of Deleted Items: Shows all launches previously removed by the user.
 * - Empty State: Uses [EmptyStateDisplay] if the trash is empty.
 * - Restoration: Provides a button for each item to return it to the main list.
 *
 * @param viewModel The shared [LaunchesViewModel] providing the trash data and restore logic.
 */
@Composable
fun TrashScreen(viewModel: LaunchesViewModel) {
    val trashItems by viewModel.trashLaunches.collectAsStateWithLifecycle()

    if (trashItems.isEmpty()) {
        EmptyStateDisplay("La corbeille est vide")
    } else {
        LazyColumn {
            items(trashItems) { launch ->
                ListItem(
                    headlineContent = { Text(launch.name) },
                    supportingContent = { Text("Supprimé le : ${formatDate(launch.dateUtc)}") },
                    trailingContent = {
                        IconButton(onClick = { viewModel.restoreLaunch(launch.id) }) {
                            Icon(Icons.Default.Restore, contentDescription = "Restaurer")
                        }
                    }
                )
            }
        }
    }
}
