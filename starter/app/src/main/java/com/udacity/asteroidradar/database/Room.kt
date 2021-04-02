package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Database

@Dao
interface AsteroidDao {
    @Query("SELECT * from table_asteroid ORDER by closeApproachDate ASC")
    fun getListAsteroid(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * from table_picture_of_the_day WHERE id=1")
    fun getPictureOfTheDay(): LiveData<DatabasePicture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAsteroids(asteroids: List<DatabaseAsteroid>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfTheDay(picture: DatabasePicture)

    @Query("DELETE FROM table_asteroid")
    fun deleteAllAsteroids()

    @Query("DELETE FROM table_picture_of_the_day")
    fun deleteAllPictures()
}

@Database(entities = [DatabaseAsteroid::class, DatabasePicture::class], version = 2)
abstract class AsteroidDatabase: RoomDatabase() {
    abstract val asteroid: AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

fun getInstance(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext, AsteroidDatabase::class.java, "asteroid")
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    return INSTANCE
}

