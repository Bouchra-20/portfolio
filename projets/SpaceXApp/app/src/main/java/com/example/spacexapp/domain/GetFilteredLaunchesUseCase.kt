package com.example.spacexapp.domain

import com.example.spacexapp.model.Launch
import com.example.spacexapp.viewmodel.DateSort
import com.example.spacexapp.viewmodel.StatusFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case responsible for filtering and sorting a list of launches.
 * * Encapsulates the business logic for searching by name, filtering by mission status,
 * and organizing the list chronologically.
 */
class GetFilteredLaunchesUseCase {
    /**
     * Applies filters and sorting to the provided flow of launches.
     * * @param launches The source flow containing the raw list of launches.
     * @param query The search string provided by the user.
     * @param sort The chronological sorting order (Newest/Oldest).
     * @param filter The mission status filter (All/Success/Failure).
     * @return A filtered and sorted [Flow] of [Launch] objects.
     */
    operator fun invoke(
        launches: Flow<List<Launch>>,
        query: String,
        sort: DateSort,
        filter: StatusFilter
    ): Flow<List<Launch>> {
        return launches.map { list ->
            list.filter { it.name.contains(query, ignoreCase = true) }
                .filter {
                    when(filter) {
                        StatusFilter.SUCCESS -> it.success == true
                        StatusFilter.FAILURE -> it.success == false
                        else -> true
                    }
                }
                .let { if (sort == DateSort.NEWEST) it.sortedByDescending { l -> l.dateUtc } else it.sortedBy { l -> l.dateUtc } }
        }
    }
}