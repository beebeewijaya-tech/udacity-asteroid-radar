package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.DEFAULT_END_DATE_DAYS
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {
    private val apiKey = "" // Insert API KEY Here
    private val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    private val calendar = Calendar.getInstance()
    private val currentTime = calendar.time
    private val startDate = dateFormat.format(currentTime)

    val asteroidList: LiveData<List<Asteroid>> = Transformations.map(database.asteroid.getListAsteroid()) {
        it?.asDomainModel()
    }

    val pictureOfTheDayData: LiveData<PictureOfDay> = Transformations.map(database.asteroid.getPictureOfTheDay()) {
        it?.let {
            it.asDomainModel()
        }
    }

    suspend fun refreshAsteroid() {
        withContext(Dispatchers.IO) {
            try {
                calendar.add(Calendar.DAY_OF_YEAR, DEFAULT_END_DATE_DAYS)
                val endDate = dateFormat.format(calendar.time)

                val asteroidList = AsteroidApi.retrofitService.getAsteroidListAsync(startDate, endDate, apiKey).await()

                val asteroidParsed = parseAsteroidsJsonResult(JSONObject(asteroidList))

                database.asteroid.insertAllAsteroids(asteroidParsed)
            } catch (e: HttpException) {
                Log.e("refreshAsteroid", e.localizedMessage)
            }
        }
    }

    suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            try {
                val pictureOfTheDay = AsteroidApi.retrofitService.getPictureOfTheDayAsync(apiKey).await()
                database.asteroid.insertPictureOfTheDay(pictureOfTheDay.asDatabaseModel())
            } catch (e: HttpException) {
                Log.e("refreshPictureOfDay", e.localizedMessage)
            }
        }
    }

    suspend fun deleteAllTable() {
        withContext(Dispatchers.IO) {
            database.asteroid.deleteAllAsteroids()
            database.asteroid.deleteAllPictures()
        }
    }
}