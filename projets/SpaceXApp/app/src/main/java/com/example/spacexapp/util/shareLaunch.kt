package com.example.spacexapp.util

import android.content.Context
import android.content.Intent
import com.example.spacexapp.model.Launch

/**
 * Triggers the Android Share Sheet to share launch details.
 * Formats mission name, date, and article link into a plain text message.
 *
 * @param context Android context to start the activity.
 * @param launch The [Launch] object containing the data to share.
 */
fun shareLaunch(context: Context, launch: Launch) {
    val shareText = """
        🚀 SpaceX Launch: ${launch.name}
        📅 Date: ${launch.dateUtc}
        🔗 Article: ${launch.links?.article ?: "N/A"}
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Partager via"))
}