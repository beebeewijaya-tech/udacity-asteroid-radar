package com.udacity.asteroidradar.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.DatabasePicture

@JsonClass(generateAdapter = true)
data class NetworkPictureOfDay(
    val title: String,

    @Json(name = "media_type")
    val mediaType: String,

    val url: String
)

fun NetworkPictureOfDay.asDomainModel(): PictureOfDay {
    return let {
        PictureOfDay(
            url = it.url,
            mediaType = it.mediaType,
            title = it.title
        )
    }
}

fun NetworkPictureOfDay.asDatabaseModel(): DatabasePicture {
    return let {
        DatabasePicture(
            id = 1,
            url = it.url,
            mediaType = it.mediaType,
            title = it.title
        )
    }
}