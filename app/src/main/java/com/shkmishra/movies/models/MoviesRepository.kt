package com.shkmishra.movies.models

import com.shkmishra.movies.service.ApiService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object MoviesRepository {
    private val apiService by lazy { ApiService.create() }

    fun getMovies(page: Int = 1): Observable<MoviesResponse> {
        return apiService.discover(page).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun searchMovies(query: String, page: Int = 1): Observable<MoviesResponse> {
        return apiService.search(query, page).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getMovieCast(movieId: Int): Observable<CastResponse> {
        return apiService.movieCast(movieId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getPerson(personId: Int): Observable<PersonResponse> {
        return apiService.person(personId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }



}