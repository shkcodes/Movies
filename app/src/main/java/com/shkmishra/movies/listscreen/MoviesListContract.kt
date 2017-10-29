package com.shkmishra.movies.listscreen

import com.shkmishra.movies.models.MovieResult
import io.reactivex.disposables.Disposable

interface MoviesListContract {
    interface View {
        fun showLoading(bottom: Boolean)
        fun showMovies(moviesList: List<MovieResult>)
        fun showEmpty()
        fun hideLoading(bottom: Boolean)
        fun showMoreMovies(moviesList: List<MovieResult>)
    }

    interface Presenter {
        fun loadMovies(): Disposable
        fun loadMoreMovies(): Disposable
        fun searchMovies(query: String): Disposable
    }
}