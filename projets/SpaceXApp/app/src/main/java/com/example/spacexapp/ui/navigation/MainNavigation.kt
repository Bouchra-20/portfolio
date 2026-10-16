package com.example.spacexapp.ui.navigation

import LaunchesViewModelFactory
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spacexapp.ui.screens.FavoritesScreen
import com.example.spacexapp.ui.screens.LaunchDetailScreen
import com.example.spacexapp.ui.screens.SpaceXScreen
import com.example.spacexapp.ui.components.*
import com.example.spacexapp.ui.screens.TrashScreen
import com.example.spacexapp.viewmodel.LaunchesViewModel
import kotlinx.coroutines.launch

/**
 * The primary navigation entry point and layout orchestrator of the application.
 *
 * This component integrates several Material Design 3 structures:
 * - [ModalNavigationDrawer]: Provides a side menu for high-level navigation.
 * - [Scaffold]: Manages the TopBar, BottomBar, and a Snackbar host.
 * - [NavHost]: Handles the fragment-like transitions between screens (List, Favorites, Detail).
 *
 * Features:
 * - **State Synchronization**: Observes the [LaunchesViewModel] to display offline warnings or refresh notifications.
 * - **Dynamic UI**: Automatically hides/shows connectivity banners based on network status.
 * - **Safe Navigation**: Uses a centralized [NavRoutes] system to manage screen transitions and arguments.
 *
 * @param factory The [LaunchesViewModelFactory] used to provide a scoped instance of the ViewModel
 * to all child screens, ensuring data consistency.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(factory: LaunchesViewModelFactory) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val viewModel: LaunchesViewModel = viewModel(factory = factory)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(viewModel.showOnlineSnackbar) {
        if (viewModel.showOnlineSnackbar) {
            snackbarHostState.showSnackbar("Données rafraîchies (Retour en ligne)")
            viewModel.showOnlineSnackbar = false
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet (
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            ){
                Text("SpaceX App", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Accueil") },
                    selected = currentRoute == NavRoutes.LIST,
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(NavRoutes.LIST) {
                            popUpTo(NavRoutes.LIST) { inclusive = true }
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Mes Favoris") },
                    selected = currentRoute == NavRoutes.FAVORITES,
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(NavRoutes.FAVORITES)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Corbeille") },
                    selected = currentRoute == NavRoutes.TRASH,
                    icon = { Icon(Icons.Default.RestoreFromTrash, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(NavRoutes.TRASH)
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                MyTopBar(viewModel,
                    currentRoute,
                    navController,
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            bottomBar = {
                MyBottomBar(navController, currentRoute)
            }
        ) { paddingValues ->

            Column(modifier = Modifier.padding(paddingValues)) {
                if (!viewModel.isOnline) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Mode hors-ligne - Données locales",
                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            NavHost(
                navController = navController,
                startDestination = NavRoutes.LIST,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(NavRoutes.LIST) {
                    SpaceXScreen(viewModel = viewModel, onLaunchClick = { launchId ->
                        navController.navigate(NavRoutes.detailWithId(launchId))
                    })
                }
                composable(NavRoutes.FAVORITES) {
                    FavoritesScreen(viewModel = viewModel, onLaunchClick = { launchId ->
                        navController.navigate(NavRoutes.detailWithId(launchId))
                    })
                }
                composable(
                    route = NavRoutes.DETAIL,
                    arguments = listOf(navArgument("launchId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val launchId = backStackEntry.arguments?.getString("launchId") ?: ""
                    LaunchDetailScreen(launchId = launchId, viewModel = viewModel, onDeleted = { message ->
                        navController.popBackStack()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = message,
                                duration = SnackbarDuration.Short
                            )
                        }
                    })
                }
                composable(NavRoutes.TRASH) {
                    TrashScreen(viewModel = viewModel)
                }
            }
        }
    }
}