package com.shkmishra.movies.service

import com.google.gson.GsonBuilder
import com.shkmishra.movies.models.CastResponse
import com.shkmishra.movies.models.MoviesResponse
import com.shkmishra.movies.models.PersonResponse
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("search/movie")
    fun search(@Query("query") query: String, @Query("page") page: Int): Observable<MoviesResponse>

    @GET("discover/movie")
    fun discover(@Query("page") page: Int): Observable<MoviesResponse>

    @GET("movie/{id}/credits")
    fun movieCast(@Path("id") id: Int): Observable<CastResponse>

    @GET("person/{id}")
    fun person(@Path("id") id: Int, @Query("append_to_response") credits: String = "credits"): Observable<PersonResponse>


    companion object {
        private val baseUrl = "http://api.themoviedb.org/3/"

        fun create(): ApiService {
            var client = OkHttpClient.Builder()
            client.addInterceptor { chain ->
                var original = chain.request()
                var url = chain.request().url().newBuilder().addQueryParameter("api_key", "api_key_goes_here").build()
                chain.proceed(original.newBuilder().url(url).build())
            }
            client.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            val builder = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client.build())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                    .build()
            return builder.create(ApiService::class.java)
        }
    }
}