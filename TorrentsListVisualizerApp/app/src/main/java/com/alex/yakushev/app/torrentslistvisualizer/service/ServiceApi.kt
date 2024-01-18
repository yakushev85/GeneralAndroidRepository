package com.alex.yakushev.app.torrentslistvisualizer.service

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Oleksandr on 10-Sep-17.
 */
enum class ServiceApi {
    Instance;

    private val endPointUrl = "https://yts.ag"
    val ytsApi: YtsApi

    init {
        val builder = Retrofit.Builder()
        val retrofit = builder.baseUrl(endPointUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        ytsApi = retrofit.create(YtsApi::class.java)
    }
}