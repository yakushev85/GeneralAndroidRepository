package com.alex.yakushev.app.torrentslistvisualizer

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.alex.yakushev.app.torrentslistvisualizer.model.MovieInfo

class DetailedActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        val fm = supportFragmentManager
        val movieInfo: MovieInfo = intent.getParcelableExtra(MovieInfo::class.java.name)!!

        if (fm.findFragmentById(R.id.detailed_fragment_container) == null) {
            fm.beginTransaction()
                    .add(R.id.detailed_fragment_container, DetailedFragment.create(movieInfo))
                    .commit()
        }
    }
}