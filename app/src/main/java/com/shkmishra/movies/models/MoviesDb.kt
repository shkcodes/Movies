package com.shkmishra.movies.models

import android.arch.persistence.room.*
import android.content.Context
import io.reactivex.Single


@Dao
interface MoviesDao {

    @get:Query("SELECT * FROM favourites")
    val getFavourites: List<MovieResult>

    @Query("SELECT * FROM favourites where id LIKE :id LIMIT 1")
    fun getFavourite(id: Int): Single<MovieResult>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: MovieResult)

    @Delete
    fun delete(movie: MovieResult)
}

@Database(entities = arrayOf(MovieResult::class), version = 1)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun moviesDao(): MoviesDao

    companion object {
        private val DB_NAME = "favourites"
        private var moviesDatabase: MoviesDatabase? = null

        fun getDatabase(context: Context): MoviesDatabase? {
            if (moviesDatabase == null) {
                moviesDatabase = Room.databaseBuilder<MoviesDatabase>(context.applicationContext, MoviesDatabase::class.java, DB_NAME).build()
            }
            return moviesDatabase
        }
    }
}