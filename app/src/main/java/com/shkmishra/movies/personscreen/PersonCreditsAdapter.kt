package com.shkmishra.movies.personscreen

import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.BaseAdapter
import com.shkmishra.movies.R
import com.shkmishra.movies.models.CreditsCast
import com.shkmishra.movies.utils.inflate
import com.shkmishra.movies.utils.loadImageSmall
import kotlinx.android.synthetic.main.person_credits_item.view.*
import java.util.*


class PersonCreditsAdapter(private val castList: List<CreditsCast>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = parent.inflate(R.layout.person_credits_item)
        view.creditsMovie.loadImageSmall(castList[position].poster_path, {
            view.scaleX = 0f
            view.scaleY = 0f
            view.animate().scaleX(1f).scaleY(1f).setStartDelay(Random().nextInt(500).toLong())
                    .setInterpolator(OvershootInterpolator(1f))
                    .setDuration(600).start()
        })
        return view
    }

    override fun getItem(position: Int): Any {
        return castList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return castList.size
    }

}