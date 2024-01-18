package com.alex.yakushev.app.torrentslistvisualizer

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.alex.yakushev.app.torrentslistvisualizer.ListFragment.OnFragmentInteractionListener
import com.alex.yakushev.app.torrentslistvisualizer.model.MovieInfo

class MainActivity : AppCompatActivity(), OnFragmentInteractionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isNetworkConnected) {
            Toast.makeText(this, "No connection to internet!", Toast.LENGTH_LONG).show()
            return
        }

        val fm = supportFragmentManager

        if (fm.findFragmentById(R.id.main_fragment_container) == null) {
            fm.beginTransaction()
                    .add(R.id.main_fragment_container, ListFragment())
                    .commit()
        }
    }

    override fun onFragmentInteraction(movieInfo: MovieInfo) {
        val intent = Intent(this, DetailedActivity::class.java)
        intent.putExtra(MovieInfo::class.java.name, movieInfo)
        startActivity(intent)
    }

    private val isNetworkConnected: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }
}