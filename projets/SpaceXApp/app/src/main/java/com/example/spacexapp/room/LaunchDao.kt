package com.example.spacexapp.room
import androidx.room.*
import com.example.spacexapp.data.DeletedIdEntity
import com.example.spacexapp.data.LaunchEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the 'launch' table.
 * * Defines the SQL queries and database interactions for the application.
 */
@Dao
interface LaunchDao {
    /**
     * Retrieves all launches that have not been soft-deleted.
     * Returns a [Flow] to observe real-time updates from the database.
     */
    @Query("SELECT * FROM launch WHERE isDeleted = 0 ORDER BY dateUtc DESC")
    fun getAll(): Flow<List<LaunchEntity>>

    /**
     * Inserts a list of launches into the database.
     * Uses [OnConflictStrategy.REPLACE] to update existing entries with fresh data from the API.
     *
     * @param list The list of [LaunchEntity] to persist.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<LaunchEntity>)

    /**
     * Updates the favorite status of a specific launch.
     *
     * @param launchId The unique identifier of the launch.
     * @param fav The new favorite state (true for favorite, false otherwise).
     */
    @Query("UPDATE launch SET isFavorite = :fav WHERE id = :launchId")
    fun setFavorite(launchId: String, fav: Boolean)

    /**
     * Clears the entire 'launch' table.
     * Primarily used for cache resets or full data refreshes.
     */
    @Query("DELETE FROM launch")
    fun deleteAll()

    /**
     * Marks a specific launch as deleted in the local database.
     * This prevents the launch from appearing in the main list while keeping its record.
     */
    @Query("UPDATE launch SET isDeleted = 1 WHERE id = :launchId")
    suspend fun delete(launchId: String)

    /**
     * Inserts or updates a single launch record.
     *
     * @param launch The [LaunchEntity] to save.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(launch: LaunchEntity)

    /**
     * Adds an ID to the blacklist table to prevent re-importing deleted data.
     * Uses [OnConflictStrategy.IGNORE] to avoid errors if the ID is already present.
     *
     * @param entity The [DeletedIdEntity] containing the ID to blacklist.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDeletedId(entity: DeletedIdEntity)

    /**
     * Retrieves the IDs of all launches currently marked as favorites.
     * Used during data synchronization to preserve user preferences.
     *
     * @return A list of launch ID strings.
     */
    @Query("SELECT id FROM launch WHERE isFavorite = 1")
    suspend fun getFavoriteIds(): List<String>

    /**
     * Observes the list of blacklisted IDs.
     * Returns a [Flow] to track IDs that should be excluded from the UI.
     */
    @Query("SELECT * FROM deleted_ids")
    fun getDeletedIds(): Flow<List<DeletedIdEntity>>

    /**
     * Retrieves a snapshot list of blacklisted IDs.
     * Typically used within a repository to filter incoming remote data.
     *
     * @return A list of [DeletedIdEntity] currently in the blacklist.
     */
    @Query("SELECT id FROM deleted_ids")
    suspend fun getDeletedIdsList(): List<DeletedIdEntity>

    /**
     * Restores a specific launch by resetting its deletion flag.
     *
     * @param launchId The unique identifier of the launch to restore.
     */
    @Query("UPDATE launch SET isDeleted = 0 WHERE id = :launchId")
    suspend fun restoreLaunch(launchId: String)

    /**
     * Removes a launch ID from the permanent blacklist.
     * This allows the launch to be re-downloaded or synchronized from the API in the future.
     *
     * @param launchId The ID to remove from the deleted_ids table.
     */
    @Query("DELETE FROM deleted_ids WHERE id = :launchId")
    suspend fun removeFromBlacklist(launchId: String)

    /**
     * Retrieves all launches currently marked as deleted.
     * Used to populate the Trash/Recycle Bin screen.*
     * @return A [Flow] emitting the list of [LaunchEntity] where isDeleted is true.
     */
    @Query("SELECT * FROM launch WHERE isDeleted = 1")
    fun getDeletedLaunches(): Flow<List<LaunchEntity>>

}