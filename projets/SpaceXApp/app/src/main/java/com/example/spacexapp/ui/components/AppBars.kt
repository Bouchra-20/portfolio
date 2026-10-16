package com.example.spacexapp.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.spacexapp.ui.navigation.NavRoutes
import com.example.spacexapp.viewmodel.DateSort
import com.example.spacexapp.viewmodel.LaunchesViewModel
import com.example.spacexapp.viewmodel.StatusFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Bottom navigation bar for the application.
 *
 * Provides quick access to the main launch list and the favorites screen.
 * Handles state-aware navigation to ensure a smooth user experience.
 *
 * @param navController The [NavHostController] used to handle screen transitions.
 * @param currentRoute The current active navigation route to highlight the correct icon.
 */
@Composable
fun MyBottomBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Accueil") },
            selected = currentRoute == NavRoutes.LIST,
            onClick = {
                navController.navigate(NavRoutes.LIST) {
                    popUpTo(NavRoutes.LIST) { inclusive = true }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
            label = { Text("Favoris") },
            selected = currentRoute == NavRoutes.FAVORITES,
            onClick = {
                navController.navigate(NavRoutes.FAVORITES) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}
/**
 * Highly dynamic Top Bar that adapts its content based on the current navigation route.
 *
 * Features:
 * - Dynamic Title: Shows "Lancements", "Favoris", or "Détails".
 * - Search Mode: Replaces the title with a [TextField] for real-time filtering.
 * - Import/Export: Provides JSON data persistence for favorite launches.
 * - Contextual Navigation: Switches between a Drawer menu icon, a Back arrow, or a Close icon.
 *
 * @param viewModel The [LaunchesViewModel] to access search queries and import/export logic.
 * @param currentRoute The current navigation path used to adapt the UI.
 * @param navController Controller to handle back-stack operations.
 * @param onOpenDrawer Callback triggered when the hamburger menu is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(viewModel: LaunchesViewModel,
             currentRoute: String?,
             navController: NavHostController,
             onOpenDrawer: () -> Unit)
{
    val context = LocalContext.current.applicationContext

    val isTopLevelScreen = currentRoute == NavRoutes.LIST || currentRoute == NavRoutes.FAVORITES

    var isSearchActive by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val jsonData = viewModel.getFavoritesJson()

                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(it)?.use { outputStream ->
                            outputStream.write(jsonData.toByteArray())
                        }
                    }
                    Toast.makeText(context, "Favoris exportés !", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Erreur d'exportation", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val json = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(it)?.bufferedReader()?.use { it.readText() }
                    }
                    if (json != null) {
                        viewModel.importFavorites(json)
                        Toast.makeText(context, "Favoris importés !", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Erreur d'importation", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    CenterAlignedTopAppBar(
        title = {
            if (isSearchActive && isTopLevelScreen) {
                TextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.searchQuery = it },
                    placeholder = { Text("Rechercher un lancement...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            } else {
                Text(when(currentRoute) {
                    NavRoutes.LIST -> "Lancements"
                    NavRoutes.FAVORITES -> "Mes Favoris"
                    else -> "Détails"
                })
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                if (isSearchActive) {
                    isSearchActive = false
                    viewModel.searchQuery = ""
                } else if (isTopLevelScreen) {
                    onOpenDrawer()
                } else {
                    navController.popBackStack()
                }
            }) {
                Icon(
                    imageVector = if (isSearchActive) Icons.Default.Close else if (isTopLevelScreen) Icons.Default.Menu else Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
            LaunchedEffect(currentRoute) {
                isSearchActive = false
                viewModel.searchQuery = ""
            }
        },
        actions = {
            if (currentRoute == NavRoutes.FAVORITES) {
                IconButton(onClick = { importLauncher.launch(arrayOf("application/json")) }) {
                    Icon(Icons.Default.FileUpload, contentDescription = "Importer")
                }
                IconButton(onClick = { exportLauncher.launch("spacex_favorites.json") }) {
                    Icon(Icons.Default.FileDownload, contentDescription = "Exporter")
                }
            }
            if (isTopLevelScreen && !isSearchActive) {
                FilterIcon(viewModel)
            }

            if (isTopLevelScreen && !isSearchActive) {
                IconButton(onClick = { isSearchActive = true }) {
                    Icon(Icons.Default.Search, contentDescription = "Rechercher")
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

/**
 * A dropdown menu component specifically for filtering and sorting the launch list.
 *
 * Offers options for:
 * - Date Sorting: Newest first or Oldest first.
 * - Status Filtering: All missions, Success only, or Failures only.
 *
 * The UI indicates the currently active filter with a checkmark icon.
 *
 * @param viewModel The [LaunchesViewModel] used to update filter states globally.
 */
@Composable
fun FilterIcon(viewModel: LaunchesViewModel, ){
    var showFilterMenu by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { showFilterMenu = true }) {
            Icon(Icons.Default.FilterList, contentDescription = "Filtrer")
        }

        DropdownMenu(
            expanded = showFilterMenu,
            onDismissRequest = { showFilterMenu = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Trier par date", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

            DropdownMenuItem(
                text = { Text("Plus récents d'abord") },
                leadingIcon = { if(viewModel.dateSort == DateSort.NEWEST) Icon(Icons.Default.Check, null) },
                onClick = { //viewModel.dateSort = DateSort.NEWEST;
                    viewModel.updateDateSort(DateSort.NEWEST);
                    showFilterMenu = false }
            )
            DropdownMenuItem(
                text = { Text("Plus anciens d'abord") },
                leadingIcon = { if(viewModel.dateSort == DateSort.OLDEST) Icon(Icons.Default.Check, null) },
                onClick = { //viewModel.dateSort = DateSort.OLDEST;
                    viewModel.updateDateSort(DateSort.OLDEST);
                    showFilterMenu = false }
            )

            HorizontalDivider()

            Text("Statut du lancement", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

            DropdownMenuItem(
                text = { Text("Tous") },
                leadingIcon = { if(viewModel.statusFilter == StatusFilter.ALL) Icon(Icons.Default.Check, null) },
                onClick = { //viewModel.statusFilter = StatusFilter.ALL;
                    viewModel.updateStatusFilter(StatusFilter.ALL);
                    showFilterMenu = false }
            )
            DropdownMenuItem(
                text = { Text("Succès uniquement") },
                leadingIcon = { if(viewModel.statusFilter == StatusFilter.SUCCESS) Icon(Icons.Default.Check, null) },
                onClick = {
                    viewModel.updateStatusFilter(StatusFilter.SUCCESS);
                    showFilterMenu = false }
            )
            DropdownMenuItem(
                text = { Text("Échecs uniquement") },
                leadingIcon = { if(viewModel.statusFilter == StatusFilter.FAILURE) Icon(Icons.Default.Check, null) },
                onClick = {
                    viewModel.updateStatusFilter(StatusFilter.FAILURE);
                    showFilterMenu = false }
            )
        }
    }
}