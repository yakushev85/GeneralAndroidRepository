package com.alex.yakushev.app.torrentslistvisualizer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.alex.yakushev.app.torrentslistvisualizer.model.MovieInfo
import com.squareup.picasso.Picasso

class DetailedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layoutView = inflater.inflate(R.layout.fragment_detailed, container, false)
        val bundle = arguments
        val movieInfo: MovieInfo? = bundle?.getParcelable(MovieInfo::class.java.name)

        if (movieInfo != null) {
            val imageView = layoutView.findViewById<ImageView>(R.id.imageDetailedView)
            val titleView = layoutView.findViewById<TextView>(R.id.titleDetailedView)
            val descriptionView = layoutView.findViewById<TextView>(R.id.descriptionDetailedView)

            Picasso.with(activity)
                    .load(movieInfo.largeCoverImage)
                    .into(imageView)

            titleView.text = movieInfo.title
            descriptionView.text = movieInfo.summary
        }

        return layoutView
    }

    companion object {
        fun create(movieInfo: MovieInfo?): DetailedFragment {
            val listFragment = DetailedFragment()
            val bundle = Bundle()

            bundle.putParcelable(MovieInfo::class.java.name, movieInfo)
            listFragment.arguments = bundle

            return listFragment
        }
    }
}