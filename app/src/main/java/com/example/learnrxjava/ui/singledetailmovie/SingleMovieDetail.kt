package com.example.learnrxjava.ui.singledetailmovie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.learnrxjava.R
import com.example.learnrxjava.data.api.TheMovieDBClient
import com.example.learnrxjava.data.api.TheMovieDBClient.POSTER_BASE_URL
import com.example.learnrxjava.data.api.TheMovieDBInterface
import com.example.learnrxjava.data.repository.NetworkState
import com.example.learnrxjava.data.viewobject.MovieDetails
import java.text.NumberFormat
import java.util.*

class SingleMovieDetail : AppCompatActivity() {
    private lateinit var viewModel: SingleMovieViewModel
    private lateinit var movieRepository: MovieDetailsRepository

    private lateinit var progressBar: ProgressBar
    private lateinit var txt_error: TextView
    private lateinit var movie_title: TextView
    private lateinit var movie_tagline: TextView
    private lateinit var movie_release_date: TextView
    private lateinit var movie_runtime: TextView
    private lateinit var movie_rating: TextView
    private lateinit var movie_overview: TextView
    private lateinit var movie_revenue: TextView
    private lateinit var movie_budget: TextView
    private lateinit var img_movie_poster: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_movie_detail)

        progressBar = findViewById(R.id.progressBar)
        txt_error = findViewById(R.id.txt_error)
        movie_title = findViewById(R.id.movie_title)
        movie_tagline = findViewById(R.id.movie_tagline)
        movie_release_date = findViewById(R.id.movie_release_date)
        movie_runtime = findViewById(R.id.movie_runtime)
        movie_rating = findViewById(R.id.movie_rating)
        movie_overview = findViewById(R.id.movie_overview)
        movie_revenue = findViewById(R.id.movie_revenue)
        movie_budget = findViewById(R.id.movie_budget)
        img_movie_poster = findViewById(R.id.img_movie_poster)
        
        val movieId: Int = intent.getIntExtra("id",1)

        val apiService : TheMovieDBInterface = TheMovieDBClient.getClient()
        movieRepository = MovieDetailsRepository(apiService)

        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })

        viewModel.networkState.observe(this, Observer {
            progressBar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })
    }
    fun bindUI( it: MovieDetails){
        movie_title.text = it.title
        movie_tagline.text = it.tagline
        movie_release_date.text = it.releaseDate
        movie_rating.text = it.rating.toString()
        movie_runtime.text = it.runtime.toString() + " minutes"
        movie_overview.text = it.overview

        val formatCurrency = NumberFormat.getCurrencyInstance(Locale.US)
        movie_budget.text = formatCurrency.format(it.budget)
        movie_revenue.text = formatCurrency.format(it.revenue)

        val moviePosterURL = POSTER_BASE_URL + it.posterPath
        Glide.with(this)
            .load(moviePosterURL)
            .into(img_movie_poster);


    }


    private fun getViewModel(movieId:Int): SingleMovieViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SingleMovieViewModel(movieRepository,movieId) as T
            }
        })[SingleMovieViewModel::class.java]
    }
}