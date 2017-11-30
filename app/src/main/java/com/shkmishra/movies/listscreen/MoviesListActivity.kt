package com.shkmishra.movies.listscreen

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.shkmishra.movies.BaseActivity
import com.shkmishra.movies.R
import com.shkmishra.movies.detailscreen.MoviesDetailActivity
import com.shkmishra.movies.models.MovieResult
import com.shkmishra.movies.models.MoviesDatabase
import com.shkmishra.movies.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_movies_list.*
import kotlinx.android.synthetic.main.movie_list_item.view.*


class MoviesListActivity : BaseActivity(), MoviesListContract.View, RecyclerClickListener {

    private val presenter = MoviesListPresenter(this)
    private lateinit var adapter: MoviesListAdapter
    lateinit var searchView: SearchView
    private var showingFavourites: Boolean = false
    private var movieDb: MoviesDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_list)
        movieDb = MoviesDatabase.getDatabase(this@MoviesListActivity)
        disposable = presenter.loadMovies()
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build()) // bypass fileUirExposed on Nougat
    }

    override fun onClick(position: Int, view: View) {
        val item = adapter.getItemAt(position)
        val intent = Intent(this@MoviesListActivity, MoviesDetailActivity::class.java)
        intent.putExtra("movie", item)
        val pair1: Pair<View, String> = Pair.create(view.backdropImage, getString(R.string.backdrop_transition))
        val pair2: Pair<View, String> = Pair.create(view.bottomLayout, getString(R.string.bottom_layout_transition))
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MoviesListActivity, pair1, pair2)
        ActivityCompat.startActivity(this@MoviesListActivity, intent, options.toBundle())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_movies_list_activity, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!query.isEmpty()) {
                    disposable = presenter.searchMovies(query)
                    adapter.clearAll()
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = true

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_favourites) {
            showingFavourites = true
            supportActionBar?.title = getString(R.string.action_favourites)
            adapter.clearAll()
            movieDb?.moviesDao()?.getFavourites?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({ favourites ->
                        adapter = MoviesListAdapter(favourites.toMutableList(), this@MoviesListActivity)
                        moviesRecycler.layoutManager = LinearLayoutManager(this@MoviesListActivity)
                        moviesRecycler.adapter = adapter
                    }, { t ->
                        t.printStackTrace()
                    })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.clearFocus()
            searchView.onActionViewCollapsed()
            searchView.isIconified = true
            disposable = presenter.loadMovies()
            adapter.clearAll()
        } else if (showingFavourites) {
            showingFavourites = false
            supportActionBar?.title = getString(R.string.app_name)
            adapter.clearAll()
            disposable = presenter.loadMovies()
        } else super.onBackPressed()
    }


    override fun showLoading(bottom: Boolean) {
        if (bottom) progressBarBottom.setVisible()
        else progressBar.setVisible()
    }

    override fun hideLoading(bottom: Boolean) {
        if (bottom) progressBarBottom.setGone()
        else progressBar.setGone()
    }

    override fun showMovies(moviesList: List<MovieResult>) {
        adapter = MoviesListAdapter(moviesList.toMutableList(), this@MoviesListActivity)
        val layoutManager = LinearLayoutManager(this@MoviesListActivity)
        moviesRecycler.layoutManager = layoutManager
        moviesRecycler.adapter = adapter
        moviesRecycler.addOnScrollListener(object : EndlessRecyclerOnScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                disposable = presenter.loadMoreMovies()
            }
        })
    }

    override fun showEmpty() {
        showToast("No movies found!")
    }

    override fun showMoreMovies(moviesList: List<MovieResult>) {
        adapter.addMovies(moviesList)
        adapter.notifyDataSetChanged()
    }

}
