package com.udacity.asteroidradar.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getInstance
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class RefreshDataWork(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {
    companion object {
        const val WORKER_NAME = "RefreshDataWork"
    }
    override suspend fun doWork(): Result {
        val database = getInstance(applicationContext)
        val asteroidRepository = AsteroidRepository(database)

        return try {
            asteroidRepository.deleteAllTable()
            asteroidRepository.refreshAsteroid()
            asteroidRepository.refreshPictureOfDay()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}