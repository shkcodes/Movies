package com.shkmishra.movies.listscreen

import com.shkmishra.movies.models.MoviesRepository
import io.reactivex.disposables.Disposable

class MoviesListPresenter(private val moviesListView: MoviesListContract.View) : MoviesListContract.Presenter {

    private val moviesRepo = MoviesRepository

    var page = 2
    var searchQuery = ""

    override fun loadMovies(): Disposable {
        page = 2
        searchQuery = ""
        moviesListView.showLoading(false)
        return moviesRepo.getMovies().subscribe({ response ->
            val list = response.results.asSequence().filter { it.backdrop_path != null && it.poster_path != null }.toList()
            if (list != null && list.isNotEmpty()) {
                moviesListView.hideLoading(false)
                moviesListView.showMovies(list)
            } else moviesListView.showEmpty()
        }, { t ->
            t.printStackTrace()
            moviesListView.hideLoading(false)
            moviesListView.showEmpty()
        })
    }


    override fun loadMoreMovies(): Disposable {
        moviesListView.showLoading(true)
        if (searchQuery.isNotEmpty())
            return moviesRepo.searchMovies(searchQuery, page++).subscribe({ response ->
                moviesListView.hideLoading(true)
                val list = response.results.asSequence().filter { it.backdrop_path != null && it.poster_path != null }.toList()
                if (list != null && list.isNotEmpty()) {
                    moviesListView.showMoreMovies(list)
                }
            }, { t ->
                t.printStackTrace()
                moviesListView.hideLoading(true)
                moviesListView.showEmpty()
            })

        return moviesRepo.getMovies(page++).subscribe({ response ->
            moviesListView.hideLoading(true)
            val list = response.results.asSequence().filter { it.backdrop_path != null && it.poster_path != null }.toList()
            if (list != null && list.isNotEmpty()) {
                moviesListView.showMoreMovies(list)
            }
        }, { t ->
            t.printStackTrace()
            moviesListView.hideLoading(true)
            moviesListView.showEmpty()
        })
    }

    override fun searchMovies(query: String): Disposable {
        page = 2
        searchQuery = query
        moviesListView.showLoading(false)
        return moviesRepo.searchMovies(query).subscribe({ response ->
            moviesListView.hideLoading(false)
            val list = response.results.asSequence().filter { it.backdrop_path != null && it.poster_path != null }.toList()
            if (list != null && list.isNotEmpty()) {
                moviesListView.showMoreMovies(list)
            } else moviesListView.showEmpty()
        }, { t ->
            t.printStackTrace()
            moviesListView.hideLoading(false)
        })
    }
}