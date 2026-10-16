package com.example.spacexapp.util

import com.example.spacexapp.model.Core
import com.example.spacexapp.model.Failure
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * A utility object responsible for JSON serialization and deserialization using the Moshi library.
 *
 * This utility facilitates the conversion between Domain models and JSON strings,
 * which is essential for storing complex objects (like Lists) into Room database columns.
 */
object MoshiUtil {
    /**
     * The central Moshi instance configured with [KotlinJsonAdapterFactory]
     * to support Kotlin data classes.
     */
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    /**
     * Converts a simple object of type [T] into its JSON string representation.
     *
     * @param data The object to serialize.
     * @return A JSON string, or null if the input data is null.
     */
    inline fun <reified T> toJson(data: T?): String? {
        if (data == null) return null
        return moshi.adapter(T::class.java).toJson(data)
    }

    /**
     * Parses a JSON string back into an object of type [T].
     *
     * @param json The JSON string to deserialize.
     * @return An object of type [T], or null if the string is empty or invalid.
     */
    inline fun <reified T> fromJson(json: String?): T? {
        if (json.isNullOrBlank()) return null
        return try {
            moshi.adapter(T::class.java).fromJson(json)
        } catch (e: Exception) {
            null
        }
    }
    /** Specific adapter for handling List<Core> to bypass type erasure. */
    private val coresAdapter = moshi.adapter<List<Core>>(
        Types.newParameterizedType(List::class.java, Core::class.java)
    )
    /** Serializes a list of [Core] objects into a JSON string. */
    fun coresToJson(cores: List<Core>?): String? = coresAdapter.toJson(cores)
    /** Deserializes a JSON string into a list of [Core] objects. */
    fun coresFromJson(json: String?): List<Core>? = if (json != null) coresAdapter.fromJson(json) else null
    /** Specific adapter for handling List<Failure> to bypass type erasure. */
    private val failuresAdapter = moshi.adapter<List<Failure>>(
        Types.newParameterizedType(List::class.java, Failure::class.java)
    )
    /** Serializes a list of [Failure] objects into a JSON string. */
    fun failuresToJson(failures: List<Failure>?): String? = failuresAdapter.toJson(failures)
    /** Deserializes a JSON string into a list of [Failure] objects. */
    fun failuresFromJson(json: String?): List<Failure>? = if (json != null) failuresAdapter.fromJson(json) else null

}