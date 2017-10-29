package com.shkmishra.movies.detailscreen

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.shkmishra.movies.R
import com.shkmishra.movies.models.Cast
import com.shkmishra.movies.utils.RecyclerClickListener
import com.shkmishra.movies.utils.inflate
import com.shkmishra.movies.utils.loadImage
import kotlinx.android.synthetic.main.cast_list_item.view.*

class MovieCastAdapter(private val cast: List<Cast>, private val clickListener: RecyclerClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieCastViewHolder(parent.inflate(R.layout.cast_list_item))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movieHolder = holder as MovieCastViewHolder
        movieHolder.bind(cast[position])
    }

    override fun getItemCount(): Int {
        return cast.size
    }

    inner class MovieCastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(cast: Cast) {
            itemView.setOnClickListener { clickListener.onClick(adapterPosition, itemView) }
            itemView.castName.text = cast.name
            itemView.castCharacter.text = cast.character
            itemView.castImage.loadImage(cast.profile_path)
        }
    }

    fun getItemAt(position: Int): Cast {
        return cast[position]
    }

}