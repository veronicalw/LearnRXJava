package com.example.learnrxjava.ui.singledetailmovie

import androidx.lifecycle.LiveData
import com.example.learnrxjava.data.api.TheMovieDBInterface
import com.example.learnrxjava.data.repository.MovieDetailsNetworkDataSource
import com.example.learnrxjava.data.repository.NetworkState
import com.example.learnrxjava.data.viewobject.MovieDetails
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsRepository (private val apiService : TheMovieDBInterface) {

    lateinit var movieDetailsNetworkDataSource: MovieDetailsNetworkDataSource

    fun fetchSingleMovieDetails (compositeDisposable: CompositeDisposable, movieId: Int) : LiveData<MovieDetails> {

        movieDetailsNetworkDataSource = MovieDetailsNetworkDataSource(apiService,compositeDisposable)
        movieDetailsNetworkDataSource.fetchMovieDetails(movieId)

        return movieDetailsNetworkDataSource.downloadedMovieResponse

    }

    fun getMovieDetailsNetworkState(): LiveData<NetworkState> {
        return movieDetailsNetworkDataSource.networkState
    }



}