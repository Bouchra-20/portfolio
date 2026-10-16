package com.example.spacexapp.ui.screens

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.spacexapp.model.Launch
import com.example.spacexapp.viewmodel.LaunchesViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.core.net.toUri
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spacexapp.util.copyToClipboard
import com.example.spacexapp.util.shareLaunch
import com.example.spacexapp.viewmodel.*

/**
 * Main screen of the application displaying the list of all SpaceX launches.
 *
 * It manages three distinct UI states:
 * 1. **Loading**: Shows a centered progress indicator when data is being fetched for the first time.
 * 2. **Error**: Displays an error message with a retry button using [ErrorDisplay].
 * 3. **Success**: Displays the list of launches using [LaunchListContent].
 *
 * @param viewModel The [LaunchesViewModel] providing data and state.
 * @param onLaunchClick Callback invoked when a user selects a launch from the list.
 */
@Composable
fun SpaceXScreen(viewModel: LaunchesViewModel, onLaunchClick: (String) -> Unit) {

    val launches = viewModel.filteredLaunches.collectAsStateWithLifecycle()

    val errorType = viewModel.errorType
    val isLoading = viewModel.isLoading
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading && launches.value.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                errorType != ERROR_NONE && launches.value.isEmpty() -> {
                    ErrorDisplay(
                        modifier = Modifier.align(Alignment.Center),
                        errorType = errorType,
                        onRetry = { viewModel.loadLaunches(refreshRemote = true) }
                    )
                }

            launches.value.isEmpty() -> {
                EmptyStateDisplay(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Aucun lancement ne correspond à vos critères."
                )
            }

                else -> {
                    LaunchListContent(launches.value, viewModel, onLaunchClick)
                }
            }
        }
    }
}

/**
 * High-level Card component representing a single launch.
 * Includes a custom elevation, rounded corners, and a specific surface tint.
 * It wraps the [LaunchCardMode] to adapt its internal content to the screen orientation.
 *
 * @param launch The [Launch] data model to display.
 * @param viewModel The ViewModel required by child components.
 * @param onClick Callback triggered when the entire card is tapped.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchCard(launch: Launch, viewModel: LaunchesViewModel, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        LaunchCardMode(launch, viewModel)
    }
}

/**
 * Utility function to format ISO 8601 UTC dates into a human-readable string.
 *
 * @param dateUtc The raw UTC date string from the API.
 * @return A formatted date (e.g., "24 March 2006") or the first 10 characters if parsing fails.
 */
fun formatDate(dateUtc: String): String {
    return try {
        val instant = Instant.parse(dateUtc)
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        dateUtc.substring(0, 10)
    }
}

/**
 * A styled [OutlinedButton] used to open external web resources.
 * Triggers an [Intent.ACTION_VIEW] when clicked.
 *
 * @param label The text to display on the button (e.g., "Wikipedia").
 * @param url The destination web address.
 */
@Composable
fun ExternalButton(label: String, url: String) {
    val context = LocalContext.current

    OutlinedButton(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        },
        modifier = Modifier.padding(end = 8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.primary)
    }
}

/**
 * A responsive container for the launch list.
 *
 * Adapts its layout based on screen orientation:
 * - **Portrait**: Uses a [LazyColumn] for a vertical list.
 * - **Landscape**: Uses a [LazyVerticalGrid] with 2 columns to optimize space.
 *
 * @param launches The list of [Launch] domain models to display.
 * @param viewModel Scoped ViewModel for child component actions.
 * @param onLaunchClick Callback for item selection.
 */
@Composable
fun LaunchListContent(
    launches: List<Launch>,
    viewModel: LaunchesViewModel,
    onLaunchClick: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(launches) { LaunchCard(it, viewModel, onClick = { onLaunchClick(it.id) }) }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            items(launches) { LaunchCard(it, viewModel, onClick = { onLaunchClick(it.id) }) }
        }
    }
}

@Composable
fun LaunchCardMode(launch: Launch, viewModel: LaunchesViewModel) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current

    val patchUrl = launch.links?.patch?.small
        ?: launch.links?.patch?.large
        ?: "https://camo.githubusercontent.com/b8a7ecf38cbdc3b4b50699890ed76ea96932eaf3adb794c4efee909392867da2/68747470733a2f2f6c6976652e737461746963666c69636b722e636f6d2f36353533352f34393138353134393132325f333766356335326534335f6b2e6a7067"

    if (isLandscape) {
        Column(modifier = Modifier.padding(8.dp)) {
            LaunchImage(patchUrl, launch.name, Modifier.fillMaxWidth().height(100.dp), ContentScale.Crop)
            LaunchContent(launch)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LaunchActionButtons(launch, viewModel, context, false)
            }
        }
    } else {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LaunchImage(patchUrl, launch.name, Modifier.size(80.dp), ContentScale.Crop)

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                LaunchContent(launch)
            }
            Column (
                modifier = Modifier
            ) {
                LaunchActionButtons(launch, viewModel, context, false)
            }
        }
    }
}
/**
 * Internal component to display the mission patch image.
 * Uses Coil's [AsyncImage] for efficient asynchronous loading and caching.
 * * @param url The image URL to load (small or large patch).
 * @param name The mission name, used for accessibility (ContentDescription).
 * @param modifier Styling and layout parameters.
 * @param scale How the image should be scaled to fit its container.
 */
@Composable
private fun LaunchImage(url: String, name: String, modifier: Modifier, scale : ContentScale) {
    AsyncImage(
        model = url,
        contentDescription = "Patch de la mission $name",
        modifier = modifier,
        contentScale = scale
    )
}
/**
 * Internal component to display the core textual information of a launch.
 * Shows the mission name, formatted date, and success/failure status with color coding.
 */
@Composable
private fun LaunchContent(launch: Launch) {

    Text(
        text = launch.name,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.secondary
    )
    Text(
        text = formatDate(launch.dateUtc),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.surface
    )
    launch.success?.let { success ->
        Text(
            text = if (success) "Succès" else "Échec",
            color = if (success) Color.Green else Color.Red
        )
    }
}
/**
 * Displays a set of action icons (Favorite, Copy, Delete, Share) for a launch.
 *
 * Includes a confirmation [AlertDialog] before performing a deletion.
 *
 * @param launch The [Launch] object the actions apply to.
 * @param viewModel The ViewModel to trigger data operations.
 * @param context Android context for clipboard and sharing intents.
 * @param showShare Whether to display the share icon (usually true in Detail view).
 * @param onDeleteAction Optional callback executed after a launch is deleted.
 */
@Composable
fun LaunchActionButtons(launch: Launch, viewModel: LaunchesViewModel, context: Context, showShare : Boolean = false, onDeleteAction: (() -> Unit)? = null) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(
        onClick = { viewModel.toggleFavorite(launch) },
        modifier = Modifier.size(32.dp)) {
        Icon(
            imageVector = if (launch.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Favori",
            tint = MaterialTheme.colorScheme.secondary
        )
    }

    IconButton(onClick = {
        val infoToCopy =
            "Lancement : ${launch.name}\n ${launch.dateUtc}\n ${launch.links?.article ?: ""}"
        copyToClipboard(context, "SpaceX Info", infoToCopy)
    },
        modifier = Modifier.size(32.dp)) {
        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copier",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Supprimer le lancement") },
            text = { Text("Êtes-vous sûr de vouloir supprimer ce lancement de votre liste ?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteLaunch(launch.id)
                    onDeleteAction?.invoke()
                    showDialog = false
                }) { Text("Supprimer", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Annuler") }
            },
            titleContentColor = MaterialTheme.colorScheme.secondary
        )
    }
    IconButton({ showDialog = true },
        modifier = Modifier.size(32.dp)) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Supprimer",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.error
        )
    }
    if(showShare){
        IconButton(onClick = { shareLaunch(context, launch) },
            modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Partager",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * Visual feedback component for data fetching failures.
 *
 * @param errorType The specific error code (Network, Timeout, Empty, etc.).
 * @param onRetry Callback to re-trigger the data loading process.
 * @param modifier Modifier for positioning and styling.
 */
@Composable
fun ErrorDisplay(errorType: Int, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val message = when (errorType) {
        ERROR_NETWORK -> "Pas de connexion internet."
        ERROR_TIMEOUT -> "Le serveur est trop lent à répondre."
        ERROR_AUTH -> "Problème d'autorisation (Token)."
        ERROR_EMPTY -> "Aucun lancement trouvé."
        else -> "Une erreur est survenue."
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(16.dp),) {
        Text(text = message)
        Button(onClick = onRetry) {
            Text("Réessayer")
        }
    }
}
/**
 * A generic UI component used to display a placeholder when a list or screen is empty.
 *
 * Features:
 * - Displays a large "SearchOff" icon to visually signal the lack of data.
 * - Shows a customizable message provided by the [text] parameter.
 * - Adapts its style to the current [MaterialTheme] using secondary color variants.
 *
 * @param text The descriptive message to show the user (e.g., "No favorites found").
 * @param modifier [Modifier] used to adjust the layout, such as centering it in the parent container.
 */
@Composable
fun EmptyStateDisplay(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(160.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}