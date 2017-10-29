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
import android.view.View
import com.shkmishra.movies.BaseActivity
import com.shkmishra.movies.R
import com.shkmishra.movies.detailscreen.MoviesDetailActivity
import com.shkmishra.movies.models.MovieResult
import com.shkmishra.movies.utils.*
import kotlinx.android.synthetic.main.activity_movies_list.*
import kotlinx.android.synthetic.main.movie_list_item.view.*


class MoviesListActivity : BaseActivity(), MoviesListContract.View, RecyclerClickListener {

    private val presenter = MoviesListPresenter(this)
    private lateinit var adapter: MoviesListAdapter
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_list)
        disposable = presenter.loadMovies()
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build()) // bypass fileUirExposed on Nougat
        //        https://www.flaticon.com/free-icon/video-camera_457754
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
                if (!query.isNullOrEmpty()) {
                    disposable = presenter.searchMovies(query)
                    adapter.clearAll()
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.clearFocus()
            searchView.onActionViewCollapsed()
            searchView.isIconified = true
            disposable = presenter.loadMovies()
            adapter.clearAll()
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
