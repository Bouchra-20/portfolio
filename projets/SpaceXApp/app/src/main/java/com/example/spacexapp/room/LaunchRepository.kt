package com.example.spacexapp.room

import android.util.Log
import com.example.spacexapp.data.DeletedIdEntity
import com.example.spacexapp.model.Launch
import com.example.spacexapp.network.RetrofitInstance
import com.example.spacexapp.data.toDomain
import com.example.spacexapp.data.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Single source of truth for launch data.
 * * This repository coordinates data between the [SpaceXService] (remote)
 * and the [LaunchDao] (local), handling the "Blacklist" logic and favoriting persistence.
 */
class LaunchRepository(
    private val dao: LaunchDao
) {
    /**
     * Synchronizes the local database with the remote SpaceX API.
     * * 1. Fetches the latest data from the network.
     * * 2. Filters out items present in the [DeletedIdEntity] table.
     * * 3. Preserves existing local 'favorite' states.
     * * 4. Updates the local database.
     * @throws Exception if the network request fails or database insertion is interrupted.
     */
    suspend fun refreshFromRemote() {
        try {
            val remoteList = RetrofitInstance.api.getLaunches()

            val bannedIds = dao.getDeletedIdsList().map { it.id }.toSet()
            val filteredRemote = remoteList.filter { it.id !in bannedIds }

            val favoriteIds = dao.getFavoriteIds().toSet()
            val entitiesToInsert = filteredRemote.map { launch ->
                launch.copy(
                    isFavorite = favoriteIds.contains(launch.id)).toEntity()
            }
            dao.insertAll(entitiesToInsert)
        } catch (e: Exception) {
            Log.e("Repo", "Erreur refresh", e)
        }
    }

    /**
     * Provides a stream of launches from the local database.
     *
     * Converts [LaunchEntity] objects from the Room database into domain [Launch] models.
     * The Flow will emit a new list automatically whenever the database is updated.
     *
     * @return A [Flow] emitting the latest list of non-deleted launches.
     */
    fun getLaunchesFlow(): Flow<List<Launch>> {
        return dao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    /**
     * Updates the favorite status of a specific launch.
     *
     * @param id The unique identifier of the launch.
     * @param newValue True to mark as favorite, false to remove.
     */
    suspend fun toggleFavorite(id: String, newValue: Boolean) {
        dao.setFavorite(id, newValue)
    }
    /**
     * Performs a permanent deletion from the user's perspective.
     * * Adds the ID to the blacklist and marks the local record as deleted.
     * @param launchId The ID of the launch to be removed.
     */
    suspend fun deleteLaunch(launchId: String) {
        val deletedIdEntity = DeletedIdEntity(id = launchId)
        dao.insertDeletedId(deletedIdEntity)
        dao.delete(launchId = launchId)
    }

    /**
     * Inserts or updates a launch record in the local database.
     *
     * Primarily used for manual data operations or when importing data
     * from external sources like JSON files.
     *
     * @param launch The domain [Launch] model to be persisted.
     */
    suspend fun insertLaunch(launch: Launch) {
        dao.insert(launch.toEntity())
    }

    /**
     * Provides a stream of launches currently residing in the Trash.
     * Converts database entities into domain models for the UI.
     *
     * @return A [Flow] of launches where the deleted flag is active.
     */
    suspend fun restoreLaunch(launchId: String) {
        dao.restoreLaunch(launchId)
        dao.removeFromBlacklist(launchId)
    }


    /**
     * Provides a stream of launches currently residing in the Trash.
     * Converts database entities into domain models for the UI.
     *
     * @return A [Flow] of launches where the deleted flag is active.
     */
    fun getTrashFlow(): Flow<List<Launch>> {
        return dao.getDeletedLaunches().map { entities ->
            entities.map { it.toDomain() }
        }
    }

}
