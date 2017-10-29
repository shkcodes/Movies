package com.shkmishra.movies

import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.Disposable

open class BaseActivity : AppCompatActivity() {
    var disposable: Disposable? = null
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}