package com.example.learnrxjava.ui.popularmovie

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learnrxjava.R
import com.example.learnrxjava.data.api.TheMovieDBClient.POSTER_BASE_URL
import com.example.learnrxjava.data.repository.NetworkState
import com.example.learnrxjava.data.viewobject.Movie
import com.example.learnrxjava.ui.singledetailmovie.SingleMovieDetail


class PopularMoviePagedListAdapter(public val context: Context) : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    val MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        if (viewType == MOVIE_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.movie_list_item, parent, false)
            return MovieItemViewHolder(view)
        } else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MOVIE_VIEW_TYPE) {
            (holder as MovieItemViewHolder).bind(getItem(position),context)
        }
        else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }


    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            MOVIE_VIEW_TYPE
        }
    }




    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }


    class MovieItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var cv_movie_title: TextView
        private lateinit var cv_movie_release_date: TextView
        private lateinit var cv_iv_movie_poster: ImageView

        fun bind(movie: Movie?,context: Context) {
            cv_movie_title = itemView.findViewById(R.id.cv_movie_title)
            cv_movie_release_date = itemView.findViewById(R.id.cv_movie_release_date)
            cv_iv_movie_poster = itemView.findViewById(R.id.cv_iv_movie_poster)

            cv_movie_title.text = movie?.title
            cv_movie_release_date.text =  movie?.releaseDate

            val moviePosterURL = POSTER_BASE_URL + movie?.posterPath
            Glide.with(itemView.context)
                .load(moviePosterURL)
                .into(cv_iv_movie_poster);

            itemView.setOnClickListener{
                val intent = Intent(context, SingleMovieDetail::class.java)
                intent.putExtra("id", movie?.id)
                context.startActivity(intent)
            }

        }

    }

    class NetworkStateItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var progressbar: ProgressBar
        private lateinit var errormsg: TextView

        fun bind(networkState: NetworkState?) {

            progressbar = itemView.findViewById(R.id.progress_bar_item)
            errormsg = itemView.findViewById(R.id.error_msg_item)

            if (networkState != null && networkState == NetworkState.LOADING) {
                progressbar.visibility = View.VISIBLE;
            }
            else  {
                progressbar.visibility = View.GONE;
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                errormsg.visibility = View.VISIBLE;
                errormsg.text = networkState.msg;
            }
            else if (errormsg != null && networkState == NetworkState.ENDOFLIST) {
                errormsg.visibility = View.VISIBLE;
                errormsg.text = networkState.msg;
            }
            else {
                errormsg.visibility = View.GONE;
            }
        }
    }


    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {                             //hadExtraRow is true and hasExtraRow false
                notifyItemRemoved(super.getItemCount())    //remove the progressbar at the end
            } else {                                       //hasExtraRow is true and hadExtraRow false
                notifyItemInserted(super.getItemCount())   //add the progressbar at the end
            }
        } else if (hasExtraRow && previousState != newNetworkState) { //hasExtraRow is true and hadExtraRow true and (NetworkState.ERROR or NetworkState.ENDOFLIST)
            notifyItemChanged(itemCount - 1)       //add the network message at the end
        }

    }

}