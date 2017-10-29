package com.shkmishra.movies.personscreen

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import com.shkmishra.movies.BaseActivity
import com.shkmishra.movies.R
import com.shkmishra.movies.models.PersonResponse
import com.shkmishra.movies.utils.*
import kotlinx.android.synthetic.main.activity_person.*

class PersonActivity : BaseActivity(), PersonContract.View {

    private val presenter = PersonPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)
        val personId = intent.getIntExtra("person", 10990)
        profileImage.loadImage(intent.getStringExtra("profile_image"), {
            val bitmap = profileImage.drawable as BitmapDrawable
            val palette = Palette.from(bitmap.bitmap)
            palette.generate { palette ->
                profileBackdrop.setBackgroundColor(palette.getDarkVibrantColor(ContextCompat.getColor(this@PersonActivity, R.color.colorAccent)))
            }
        })
        presenter.loadPerson(personId)
        window.doOnAnimationEnd {
            personName.apply {
                text = intent.getStringExtra("name")
                translationY = 100f
                animate().translationY(0f).setDuration(600).setStartDelay(300).start()
            }
        }
    }

    override fun onBackPressed() {
        personName.animate().translationY(100f).setDuration(300).doOnAnimationEnd {
            super.onBackPressed()
            overridePendingTransition(0, 0)
        }
    }

    override fun showPerson(person: PersonResponse) {
        val moviesList = person.credits.cast.asSequence().filter { it.poster_path != null }.toList()
        if (moviesList.size > 12) {
            moviesGrid.setVisible()
            moviesGrid.adapter = PersonCreditsAdapter(moviesList)
            moviesGrid.isEnabled = false
        }
        if (person.biography.isNotEmpty())
            biography.text = "${person.biography}\n"
        else biography.text = "No biography found"
        birthday.text = person.birthday
        placeOfBirth.text = person.place_of_birth
    }

    override fun showLoading() {
        progressBar.setVisible()
    }

    override fun hideLoading() {
        progressBar.setGone()
    }

    override fun showError() {
        showToast("Unable to fetch person details")
    }
}
