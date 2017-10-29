package com.shkmishra.movies.detailscreen

import com.shkmishra.movies.models.Cast
import io.reactivex.disposables.Disposable

interface MoviesDetailContract {

    interface View {
        fun showMoviesCast(cast: List<Cast>)
        fun showLoading()
        fun hideLoading()
        fun showEmpty()
    }

    interface Presenter {
        fun getMoviesCast(movieId: Int): Disposable
    }

}