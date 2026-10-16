package com.example.spacexapp.domain

import android.util.Log
import com.example.spacexapp.model.Launch
import com.example.spacexapp.room.LaunchRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Use case for exporting and importing favorite launches via JSON.
 * * Leverages the Moshi library for high-performance JSON serialization/deserialization.
 */
class ManageFavoritesJsonUseCase(private val repo: LaunchRepository) {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, Launch::class.java)
    private val adapter = moshi.adapter<List<Launch>>(listType)

    /**
     * Serializes favorite launches into a JSON string for export.
     * @param launchesFlow Flow containing the current list of launches.
     * @return A JSON formatted string of favorites.
     */
    suspend fun exportToJson(launchesFlow: Flow<List<Launch>>): String = withContext(Dispatchers.IO) {
        val favorites = launchesFlow.first().filter { it.isFavorite }
        return@withContext adapter.toJson(favorites)
    }

    /**
     * Deserializes a JSON string and imports the launches into the local repository.
     * Items are automatically marked as favorites upon import.
     * @param json The JSON string to import.
     * @throws Exception if the JSON format is invalid.
     */
    suspend fun importFromJson(json: String) = withContext(Dispatchers.IO) {
        try {
            val importedList = adapter.fromJson(json)

            if (importedList.isNullOrEmpty()) {
                Log.e("Import", "Le fichier JSON est vide ou mal formé")
                return@withContext
            }

            importedList.forEach { launch ->
                if (launch.id.isNotEmpty()) {
                    repo.insertLaunch(launch.copy(isFavorite = true))
                }
            }
        } catch (e: Exception) {
            Log.e("Import", "Erreur Moshi : ${e.message}")
            throw e
        }

    }
}
