package com.example.spacexapp.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

/**
 * Utility for interacting with the Android Clipboard.
 * Copies a given text string and displays a confirmation Toast.
 *
 * @param context Android context to access the Clipboard Service.
 * @param label User-visible label for the clip data.
 * @param text The actual content to be copied.
 */
fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copié dans le presse-papiers !", Toast.LENGTH_SHORT).show()
}