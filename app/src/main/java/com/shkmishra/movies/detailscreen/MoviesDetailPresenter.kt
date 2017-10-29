package com.shkmishra.movies.detailscreen

import com.shkmishra.movies.models.CastResponse
import com.shkmishra.movies.models.MoviesRepository
import io.reactivex.disposables.Disposable


class MoviesDetailPresenter(private val moviesDetailView: MoviesDetailContract.View) : MoviesDetailContract.Presenter {

    private val moviesRepo = MoviesRepository

    override fun getMoviesCast(movieId: Int): Disposable {
        moviesDetailView.showLoading()
        return moviesRepo.getMovieCast(movieId).subscribe({ caseResponse: CastResponse? ->
            moviesDetailView.hideLoading()
            val list = caseResponse?.cast?.asSequence()?.filter { it.profile_path != null }?.toList()
            if (list != null && list.isNotEmpty())
                moviesDetailView.showMoviesCast(list)
            else moviesDetailView.showEmpty()
        }, { t: Throwable? ->
            t?.printStackTrace()
            moviesDetailView.hideLoading()
            moviesDetailView.showEmpty()
        })
    }
}