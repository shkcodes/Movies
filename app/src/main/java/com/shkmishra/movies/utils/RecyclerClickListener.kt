package com.shkmishra.movies.utils

import android.view.View
import android.widget.ImageView
import com.shkmishra.movies.models.MovieResult


interface RecyclerClickListener {
    fun onClick(position:Int, view: View)
}