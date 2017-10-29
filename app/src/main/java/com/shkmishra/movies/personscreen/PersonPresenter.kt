package com.shkmishra.movies.personscreen

import com.shkmishra.movies.models.MoviesRepository
import io.reactivex.disposables.Disposable

class PersonPresenter(private val personView: PersonContract.View) : PersonContract.Presenter {

    private val moviesRepo = MoviesRepository

    override fun loadPerson(personId: Int): Disposable {
        personView.showLoading()
        return moviesRepo.getPerson(personId).subscribe({ response ->
            personView.hideLoading()
            personView.showPerson(response)
        }, { t ->
            personView.showError()
            personView.hideLoading()
            t.printStackTrace()
        })
    }
}