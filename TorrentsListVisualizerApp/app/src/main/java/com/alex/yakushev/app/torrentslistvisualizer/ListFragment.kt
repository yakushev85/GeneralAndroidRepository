package com.alex.yakushev.app.torrentslistvisualizer

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.alex.yakushev.app.torrentslistvisualizer.model.GeneralMoviesData
import com.alex.yakushev.app.torrentslistvisualizer.model.MovieInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*


class ListFragment : Fragment() {
    private var mListener: OnFragmentInteractionListener? = null
    private var mRecyclerView: RecyclerView? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val viewFragment = inflater.inflate(R.layout.fragment_list, container, false)
        mRecyclerView = viewFragment.findViewById<View>(R.id.recyclerView) as RecyclerView
        initRecyclerView()
        initServiceApi()
        return viewFragment
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
        mCompositeDisposable.dispose()
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(movieInfo: MovieInfo)
    }

    private fun initRecyclerView() {
        val currentOrientation = activity?.resources?.configuration?.orientation
        val layoutManager: RecyclerView.LayoutManager
        val dividerItemDecoration: DividerItemDecoration

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            dividerItemDecoration =
                DividerItemDecoration(
                    mRecyclerView?.context,
                    DividerItemDecoration.HORIZONTAL
                )
        } else {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
            dividerItemDecoration =
                DividerItemDecoration(
                    mRecyclerView?.context,
                    DividerItemDecoration.VERTICAL
                )
        }

        mRecyclerView?.layoutManager = layoutManager
        mRecyclerView?.addItemDecoration(dividerItemDecoration)
        mRecyclerView?.itemAnimator =
            DefaultItemAnimator()
        mRecyclerView?.adapter = YtsRecycleListAdapter(ArrayList(), activity!!)
    }

    private fun initServiceApi() {
        val serviceApplication = activity?.applicationContext as YtsServiceApplication
        val moviesObservable = serviceApplication.serviceApi?.ytsApi?.listOfMovies!!

        mCompositeDisposable.add(
                moviesObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ inData: GeneralMoviesData? ->
                    val movieInfoList = inData?.data?.movies
                    val listAdapter = YtsRecycleListAdapter(movieInfoList!!, activity!!)

                    listAdapter.setOnClickListener(object : YtsRecycleListAdapter.MovieInfoOnClickListener {
                        override fun onClick(movieInfo: MovieInfo) {
                            mListener?.onFragmentInteraction(movieInfo)
                        }

                    })

                    mRecyclerView!!.adapter = listAdapter
                }) { exception: Throwable ->
                    Toast.makeText(activity, exception.message, Toast.LENGTH_LONG).show()
                }
        )
    }
}