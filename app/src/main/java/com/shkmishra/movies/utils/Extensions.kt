package com.shkmishra.movies.utils

import android.animation.Animator
import android.app.Activity
import android.transition.Transition
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


val baseUrl = "https://image.tmdb.org/t/p/w500"
val baseUrl2 = "https://image.tmdb.org/t/p/w150"

fun ImageView.loadImage(url: String, function: () -> Unit) {
    Picasso.with(context).load(baseUrl + url).into(this, object : Callback {
        override fun onSuccess() {
            function()
        }

        override fun onError() {}
    })
}

fun ImageView.loadImage(url: String) {
    Picasso.with(context).load(baseUrl + url).into(this)
}

fun ImageView.loadImageSmall(url: String, function: () -> Unit) {
    Picasso.with(context).load(baseUrl2 + url).into(this, object : Callback {
        override fun onSuccess() {
            function()
        }

        override fun onError() {
        }
    })
}

fun CircleImageView.loadImage(url: String) {
    Picasso.with(context).load(baseUrl + url).into(this)
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun Activity.showToast(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setGone() {
    visibility = View.GONE
}

fun Window.doOnAnimationEnd(function: () -> Unit) {
    enterTransition.addListener(object : Transition.TransitionListener {
        override fun onTransitionEnd(p0: Transition?) {
            function()
        }

        override fun onTransitionResume(p0: Transition?) {
        }

        override fun onTransitionPause(p0: Transition?) {
        }

        override fun onTransitionCancel(p0: Transition?) {
        }

        override fun onTransitionStart(p0: Transition?) {
        }

    })
}

fun ViewPropertyAnimator.doOnAnimationEnd(function: () -> Unit) {
    setListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            function()
            setListener(null)
        }

    }).start()
}

