package com.shkmishra.movies.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class MoviesResponse(
        var page: Int,
        var total_results: Int,
        var total_pages: Int,
        var search_results: List<MovieResult>,
        var results: List<MovieResult>
)

@Parcelize
data class MovieResult(
        var vote_count: Int,
        var id: Int,
        var video: Boolean,
        var vote_average: Double,
        var title: String,
        var popularity: Double,
        var poster_path: String,
        var original_language: String,
        var original_title: String,
        var genre_ids: List<Int>,
        var backdrop_path: String,
        var adult: Boolean,
        var overview: String,
        var release_date: String,
        var bottom_color: Int = 0,
        var text_color: Int = 0,
        var muted_color: Int = 0
) : Parcelable

data class CastResponse(var id: Int, var cast: List<Cast>)

data class Cast(
        var cast_id: Int,
        var character: String,
        var credit_id: String,
        var gender: Int,
        var id: Int,
        var name: String,
        var profile_path: String
)



data class PersonResponse(
        var birthday: String,
        var id: Int,
        var name: String,
        var credits: Credits,
        var biography: String,
        var place_of_birth: String,
        var profile_path: String
)

data class Credits(
        var cast: List<CreditsCast>,
        var id: Int
)
data class CreditsCast(
        var title: String,
        var id: Int,
        var release_date: String,
        var poster_path: String,
        var creditId: String
)