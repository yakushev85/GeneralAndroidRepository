package com.alex.yakushev.app.torrentslistvisualizer

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.alex.yakushev.app.torrentslistvisualizer.model.MovieInfo
import com.squareup.picasso.Picasso

/**
 * Created by Oleksandr on 10-Sep-17.
 */
class YtsRecycleListAdapter(private val mMovies: List<MovieInfo>, private val mContext: Context) : RecyclerView.Adapter<YtsRecycleListAdapter.ViewHolder>() {
    private var onClickListener: MovieInfoOnClickListener? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageListItem: ImageView = itemView.findViewById(R.id.imageListView)
        var titleListItem: TextView = itemView.findViewById(R.id.titleListView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleListItem.text = mMovies[position].title
        Picasso.with(mContext)
                .load(mMovies[position].mediumCoverImage)
                .into(holder.imageListItem)
        if (onClickListener != null) {
            holder.itemView.setOnClickListener { v: View? -> onClickListener!!.onClick(mMovies[position]) }
        }
    }

    override fun getItemCount(): Int {
        return mMovies.size
    }

    interface MovieInfoOnClickListener {
        fun onClick(movieInfo: MovieInfo)
    }

    fun setOnClickListener(onClickListener: MovieInfoOnClickListener) {
        this.onClickListener = onClickListener
    }

}