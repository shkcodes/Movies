package com.shkmishra.movies.listscreen

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.shkmishra.movies.R
import com.shkmishra.movies.models.MovieResult
import com.shkmishra.movies.utils.RecyclerClickListener
import com.shkmishra.movies.utils.inflate
import com.shkmishra.movies.utils.loadImage
import kotlinx.android.synthetic.main.movie_list_item.view.*


class MoviesListAdapter(private val movies: MutableList<MovieResult>, private val clickListener: RecyclerClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MoviesViewHolder(parent.inflate(R.layout.movie_list_item))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movieHolder = holder as MoviesViewHolder
        movieHolder.bind(movies[position])
    }

    fun addMovies(moviesList: List<MovieResult>) {
        movies.addAll(moviesList)
        notifyDataSetChanged()
    }

    fun clearAll() {
        movies.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun getItemAt(position: Int): MovieResult {
        return movies[position]
    }

    inner class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: MovieResult) {

            itemView.setOnClickListener { clickListener.onClick(adapterPosition, itemView) }
            itemView.movieTitle.text = item.title
            itemView.movieYear.text = item.release_date
            itemView.averageRating.text = "${item.vote_average}"

            itemView.backdropImage.loadImage(item.backdrop_path, {
                if (item.bottom_color == 0) {
                    val bitmap = itemView.backdropImage.drawable as BitmapDrawable
                    val palette = Palette.from(bitmap.bitmap)
                    palette.generate { palette ->
                        item.bottom_color = palette.getDominantColor(ContextCompat.getColor(itemView.context, R.color.colorPrimaryDark))
                        item.text_color = palette.dominantSwatch?.titleTextColor ?: R.color.white
                        if (Color.alpha(item.bottom_color) < 255) {
                            item.bottom_color = palette.getDarkVibrantColor(ContextCompat.getColor(itemView.context, R.color.colorPrimaryDark))
                            item.text_color = palette.darkVibrantSwatch?.titleTextColor ?: R.color.white
                        }
                        item.muted_color = palette.getDarkMutedColor(ContextCompat.getColor(itemView.context, R.color.white))
                        setupItemColors(item)
                    }
                } else setupItemColors(item)
            })
        }

        private fun setupItemColors(item: MovieResult) {
            itemView.bottomLayout.setBackgroundColor(item.bottom_color)
            itemView.movieTitle.setTextColor(item.text_color)
            itemView.movieYear.setTextColor(item.text_color)
            itemView.averageRating.setTextColor(item.text_color)
        }
    }
}

