package com.example.spacexapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.spacexapp.viewmodel.LaunchesViewModel


/**
 * Detailed view for a specific SpaceX launch.
 *
 * Features:
 * - Reactive search: Finds the launch in the filtered list provided by the ViewModel.
 * - Mission details: Displays patch image, success status, flight number, and technical details.
 * - External Links: Provides buttons to open Wikipedia or YouTube via [ExternalButton].
 * - Actions: Integration of [LaunchActionButtons] with a deletion callback.
 *
 * @param launchId The unique identifier of the launch to display.
 * @param viewModel The [LaunchesViewModel] containing the data stream.
 * @param onDeleted Callback triggered after a successful deletion to navigate back and show a notification.
 */
@Composable
fun LaunchDetailScreen(launchId: String, viewModel: LaunchesViewModel, onDeleted: (String) -> Unit) {
    val launches by viewModel.launches.collectAsStateWithLifecycle(initialValue = emptyList())
    val launch = launches.find { it.id == launchId }

    if (launch == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val context = LocalContext.current
    val placeholderUrl = "https://camo.githubusercontent.com/b8a7ecf38cbdc3b4b50699890ed76ea96932eaf3adb794c4efee909392867da2/68747470733a2f2f6c6976652e737461746963666c69636b722e636f6d2f36353533352f34393138353134393132325f333766356335326534335f6b2e6a7067"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(280.dp).background(Color.Black)) {
            AsyncImage(
                model = launch.links?.patch?.large ?: launch.links?.patch?.small ?: placeholderUrl
                ,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentScale = ContentScale.Fit
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = launch.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = formatDate(launch.dateUtc) ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                LaunchActionButtons(launch, viewModel, context, true, onDeleteAction = {
                    onDeleted("Supprimé")
                })
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRowElegant("Vehicle", launch.rocket ?: "Unknown")
                    DetailRowElegant("Flight Number", "${launch.flightNumber}")
                    DetailRowElegant("Status", if(launch.success == true) "Success" else "Pending")
                    DetailRowElegant("Details", launch.details?: "No details")
                    DetailRowElegant("Upcoming", if(launch.upcoming== true) "Yes" else "No")

                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "TECHNICAL SPECIFICATIONS",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.05f)).padding(12.dp)
                    ) {
                        TableCellElegant("Serial", weight = 1.2f, isHeader = true)
                        TableCellElegant("Flight", weight = 0.8f, isHeader = true)
                        TableCellElegant("Reused", weight = 1f, isHeader = true)
                        TableCellElegant("Landing", weight = 1f, isHeader = true)
                    }

                    launch.cores?.forEach { core ->
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            TableCellElegant(core.core ?: "N/A", weight = 1.2f)
                            TableCellElegant("${core.flight ?: 1}", weight = 0.8f)
                            TableCellElegant(if (core.reused == true) "Yes" else "No", weight = 1f)
                            TableCellElegant(if (core.legs == true) "Recover" else "None", weight = 1f)
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }

            if (!launch.failures.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "MISSION FAILURES",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(12.dp)
                        ) {
                            TableCellElegant("Time (s)", weight = 0.8f, isHeader = true)
                            TableCellElegant("Alt (km)", weight = 0.8f, isHeader = true)
                            TableCellElegant("Reason", weight = 2.4f, isHeader = true)
                        }

                        launch.failures.forEach { failure ->
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                TableCellElegant(
                                    text = failure.time?.let { "T+${it}s" } ?: "N/A",
                                    weight = 0.8f
                                )

                                TableCellElegant(
                                    text = failure.altitude?.let { "${it}km" } ?: "N/A",
                                    weight = 0.8f
                                )

                                TableCellElegant(
                                    text = failure.reason ?: "Unknown reason",
                                    weight = 2.4f
                                )
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(24.dp))
            LaunchLinksSection(launch.links)
        }
    }
}
/**
 * Displays a styled row containing a key-value pair.
 *
 * Used primarily within Info Cards to present mission data (e.g., "Vehicle: Falcon 9").
 * The title is assigned a fixed width to ensure vertical alignment across multiple rows.
 *
 * @param title The label for the data point (e.g., "Vehicle").
 * @param value The actual data string to be displayed.
 */
@Composable
fun DetailRowElegant(title: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = "$title: ", color = MaterialTheme.colorScheme.outline, modifier = Modifier.width(120.dp))
        Text(text = value, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
/**
 * A customized text cell designed for tabular data layouts.
 *
 * Must be used within a [RowScope] (like a [Row]) to properly utilize the [Modifier.weight].
 * Supports distinct styling for header rows versus data rows.
 *
 * @param text The content to display inside the cell.
 * @param weight The proportional width of the cell relative to other cells in the same row.
 * @param isHeader If true, applies primary color and bold weight to distinguish it as a column title.
 */
@Composable
fun RowScope.TableCellElegant(text: String, weight: Float, isHeader: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = if (isHeader) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodyMedium,
        color = if (isHeader) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal
    )
}


/**
 * A comprehensive section providing external resources and visual media for a launch.
 *
 * This component handles two main types of content:
 * 1. **Interactive Links**: Uses a [FlowRow] to display buttons for YouTube, Wikipedia, Reddit, etc.
 *    The flow layout ensures buttons wrap to the next line on smaller screens.
 * 2. **Media Gallery**: If Flickr images are available, it displays a horizontal [LazyRow]
 *    allowing users to browse high-resolution mission photos.
 *
 * @param links The [Links] domain model containing URLs and image collections.
 */
@OptIn(ExperimentalLayoutApi::class) // Pour FlowRow
@Composable
fun LaunchLinksSection(links: com.example.spacexapp.model.Links?) {
    if (links == null) return

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "RESOURCES",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            links.youtubeId?.let { id ->
                ExternalButton("YouTube", "https://www.youtube.com/watch?v=$id")
            }

            links.article?.let { url ->
                ExternalButton("Article", url)
            }

            links.wikipedia?.let { url ->
                ExternalButton("Wikipedia", url)
            }

            links.reddit?.campaign?.let { url ->
                ExternalButton("Reddit", url)
            }

            links.presskit?.let { url ->
                ExternalButton("Presskit", url)
            }
        }
        if (!links.flickr?.original.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "GALLERY",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(links.flickr?.original ?: emptyList()) { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Mission image",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
