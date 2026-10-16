package com.example.spacexapp.ui.navigation

/**
 * Centralized navigation routes for the application.
 *
 * This object defines the route strings used by the [NavHost] and provides
 * helper functions for building routes with dynamic arguments.
 */
object NavRoutes {
    /** Route for the main launches list screen. */
    const val LIST = "list"
    /** Route for the user's favorite launches screen. */
    const val FAVORITES = "favorites"
    /**
     * Route template for the launch detail screen.
     * Expects a dynamic {launchId} parameter.
     */
    const val DETAIL = "detail/{launchId}"
    /** Route for the trash list screen. */
    const val TRASH = "trash"
    /**
     * Helper function to construct a detail route with a specific launch ID.
     *
     * @param id The unique identifier of the launch to display.
     * @return A formatted route string (e.g., "detail/5eb87cd9ffd86e000604b32a").
     */
    fun detailWithId(id: String) = "detail/$id"
}
