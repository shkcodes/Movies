package com.shkmishra.movies.personscreen

import com.shkmishra.movies.models.PersonResponse
import io.reactivex.disposables.Disposable

interface PersonContract {
    interface View {
        fun showPerson(person: PersonResponse)
        fun showLoading()
        fun hideLoading()
        fun showError()
    }

    interface Presenter {
        fun loadPerson(personId: Int): Disposable
    }
}