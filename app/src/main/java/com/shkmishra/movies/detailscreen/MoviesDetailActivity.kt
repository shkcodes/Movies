package com.shkmishra.movies.detailscreen

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.shkmishra.movies.BaseActivity
import com.shkmishra.movies.R
import com.shkmishra.movies.models.Cast
import com.shkmishra.movies.models.MovieResult
import com.shkmishra.movies.models.MoviesDatabase
import com.shkmishra.movies.personscreen.PersonActivity
import com.shkmishra.movies.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_movies_detail.*
import kotlinx.android.synthetic.main.cast_list_item.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MoviesDetailActivity : BaseActivity(), MoviesDetailContract.View, RecyclerClickListener {

    private val presenter = MoviesDetailPresenter(this)
    private lateinit var item: MovieResult
    private lateinit var adapter: MovieCastAdapter
    private var showingMainDetail = true
    private var movieDb: MoviesDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_detail)
        item = intent.getParcelableExtra("movie")
        movieDb = MoviesDatabase.getDatabase(this@MoviesDetailActivity)

        setupDetails()

        castButton.setOnClickListener {
            flipBottomCard {
                mainDetailLayout.setGone()
                disposable = presenter.getMoviesCast(item.id)
                showingMainDetail = false
            }
        }

        favouriteButton.setOnClickListener {
            val dbTransaction: Observable<Unit?>
            if (!item.isFavourite) {
                dbTransaction = Observable.fromCallable {
                    movieDb?.moviesDao()?.insert(item)
                }.subscribeOn(Schedulers.io())
                item.isFavourite = true
                favouriteButton.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp))
            } else {
                dbTransaction = Observable.fromCallable {
                    movieDb?.moviesDao()?.delete(item)
                }.subscribeOn(Schedulers.io())
                item.isFavourite = false
                favouriteButton.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24dp))
            }
            dbTransaction.subscribe()
        }

        shareButton.setOnClickListener {
            val bitmap = (posterImage.drawable as BitmapDrawable).bitmap
            var bmpUri: Uri?
            try {
                val file = File(externalCacheDir, "share.png")
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
                out.close()
                bmpUri = Uri.fromFile(file)
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
                shareIntent.putExtra(Intent.EXTRA_TEXT, "${item.title} https://www.themoviedb.org/movie/${item.id}")
                shareIntent.type = "image/*"
                startActivity(Intent.createChooser(shareIntent, "Share Image"))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        window.doOnAnimationEnd {
            bottomCard.apply {
                translationY = bottomCard.height.toFloat()
                setVisible()
                animate().translationY(0f).setDuration(600).start()
            }
            overViewText.text = "${item.overview} \n"
            posterImage.loadImage(item.poster_path, {
                posterImage.animate().scaleX(1.0f).scaleY(1.0f).setDuration(600).start()
            })
        }

    }

    private fun setupDetails() {
        backdropImageDetail.loadImage(item.backdrop_path)
        bottomLayoutDetail.setBackgroundColor(item.bottom_color)
        background.setBackgroundColor(item.muted_color)
        movieTitleDetail.apply {
            text = item.title
            setTextColor(item.text_color)
        }
        movieYearDetail.apply {
            text = item.release_date
            setTextColor(item.text_color)
        }
        averageRatingDetail.apply {
            text = "${item.vote_average}"
            setTextColor(item.text_color)
        }
        favouriteButton.setColorFilter(item.bottom_color, PorterDuff.Mode.MULTIPLY)
        shareButton.setColorFilter(item.bottom_color, PorterDuff.Mode.MULTIPLY)
        castButton.setColorFilter(item.bottom_color, PorterDuff.Mode.MULTIPLY)
        movieDb?.moviesDao()?.getFavourite(item.id)?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    item.isFavourite = true
                    favouriteButton.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp))
                }, { t ->
                    t.printStackTrace()
                })
    }

    private fun flipBottomCard(function: () -> Unit) {
        bottomCard.animate().scaleX(0f).setDuration(300).doOnAnimationEnd {
            bottomCard.animate().scaleX(1f).setDuration(300).start()
            function()
        }
    }

    override fun onClick(position: Int, view: View) {
        val cast = adapter.getItemAt(position)
        val intent = Intent(this, PersonActivity::class.java)
        intent.putExtra("person", cast.id)
        intent.putExtra("name", cast.name)
        intent.putExtra("profile_image", cast.profile_path)
        val pair: Pair<View, String> = Pair.create(view.castImage, getString(R.string.person_transition))
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MoviesDetailActivity, pair)
        ActivityCompat.startActivity(this@MoviesDetailActivity, intent, options.toBundle())
    }


    override fun onBackPressed() {
        if (!showingMainDetail) {
            flipBottomCard {
                mainDetailLayout.setVisible()
                castRecycler.setGone()
                hideLoading()
                showingMainDetail = true
            }
        } else {
            posterImage.animate().scaleY(0f).scaleX(0f).setDuration(600).doOnAnimationEnd {
                super.onBackPressed()
                overridePendingTransition(0, 0)
            }
            bottomCard.animate().translationY(bottomCard.height.toFloat() + 200).setDuration(600).start()
        }
    }

    override fun showMoviesCast(cast: List<Cast>) {
        castRecycler.setVisible()
        adapter = MovieCastAdapter(cast, this)
        castRecycler.adapter = adapter
        castRecycler.layoutManager = LinearLayoutManager(this@MoviesDetailActivity)
    }

    override fun showLoading() {
        progressBarCast.setVisible()
    }

    override fun hideLoading() {
        progressBarCast.setGone()
    }

    override fun showEmpty() {
        showToast("Unable to fetch cast")
    }
}
