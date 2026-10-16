package com.example.spacexapp.model

import androidx.compose.runtime.snapshots.SnapshotApplyResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * Main domain model representing a SpaceX launch.
 * * This data class is used both for Moshi JSON parsing and throughout the UI.
 * It includes high-level information about the mission, rocket, and local state.
 * * @property isFavorite Local flag indicating if the user saved this launch.
 * @property isDeleted Local flag used for soft-deletion logic.
 */
@JsonClass(generateAdapter = true)
data class Launch(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "date_utc") val dateUtc: String,
    @Json(name = "links") val links: Links?,
    @Json(name = "success") val success: Boolean? = null,
    val isFavorite: Boolean = false,
    val isDeleted: Boolean = false,

    @Json(name = "flight_number") val flightNumber: Int?,
    @Json(name = "details") val details: String?,
    @Json(name = "upcoming") val upcoming: Boolean,
    @Json(name = "rocket") val rocket: String?,
    @Json(name = "cores") val cores: List<Core>?,
    @Json(name = "payloads") val payloads: List<String>?,
    @Json(name = "staticFireDateUtc") val staticFireDateUtc: String?,
    @Json(name = "failures") val failures: List<Failure>?
)
/**
 * Container for various external resources and media related to a launch.
 */
@JsonClass(generateAdapter = true)
data class Links(
    @Json(name = "patch") val patch: Patch?,
    @Json(name = "reddit") val reddit: Reddit?,
    @Json(name = "flickr") val flickr: Flickr?,
    @Json(name = "presskit") val presskit: String?,
    @Json(name = "webpage") val webpage: String?,
    @Json(name = "youtube_id") val youtubeId: String?,
    @Json(name = "article") val article: String?,
    @Json(name = "wikipedia") val wikipedia: String?
)
/**
 * Visual patches associated with the mission.
 *
 * @property small URL to the small version of the mission patch.
 * @property large URL to the high-resolution version of the mission patch.
 */
@JsonClass(generateAdapter = true)
data class Patch(
    @Json(name = "small") val small: String?,
    @Json(name = "large") val large: String?
)

/**
 * Links to various Reddit discussion threads for the launch.
 *
 * @property campaign Link to the overall mission campaign thread.
 * @property launch Link to the specific launch day thread.
 * @property media Link to the community-curated media thread.
 * @property recovery Link to the booster recovery discussion thread.
 */
@JsonClass(generateAdapter = true)
data class Reddit(
    @Json(name = "campaign") val campaign: String?,
    @Json(name = "launch") val launch: String?,
    @Json(name = "media") val media: String?,
    @Json(name = "recovery") val recovery: String?
)
/**
 * Image galleries provided by Flickr.
 *
 * @property small Collection of URLs for small preview images.
 * @property original Collection of URLs for high-resolution original photos.
 */
@JsonClass(generateAdapter = true)
data class Flickr(
    @Json(name = "small") val small: List<String>?,
    @Json(name = "original") val original: List<String>?
)

/**
 * Represents a rocket booster (core) used during the launch.
 * Includes information about its flight history and recovery hardware.
 *
 * @property core Unique identifier of the core.
 * @property flight Number of times this specific core has flown (including this mission).
 * @property gridfins Indicates if grid fins were present for stabilization.
 * @property legs Indicates if landing legs were deployed.
 * @property reused True if the core has been flown in a previous mission.
 */
@JsonClass(generateAdapter = true)
data class Core(
    @Json(name = "core") val core: String?,
    @Json(name = "flight") val flight: Int?,
    @Json(name = "gridfins") val gridfins: Boolean?,
    @Json(name = "legs") val legs: Boolean?,
    @Json(name = "reused") val reused: Boolean?
)

/**
 * Details regarding a mission failure.
 *
 * @property time Seconds into the flight when the failure occurred (T+ seconds).
 * @property altitude Altitude in kilometers at the time of failure.
 * @property reason Detailed description of the technical anomaly.
 */
@JsonClass(generateAdapter = true)
data class Failure(
    @Json(name = "time") val time: Int?,
    @Json(name = "altitude") val altitude: Int?,
    @Json(name = "reason") val reason: String?
)
