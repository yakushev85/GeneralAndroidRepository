package com.alex.yakushev.app.torrentslistvisualizer

import android.app.Application
import com.alex.yakushev.app.torrentslistvisualizer.service.ServiceApi

/**
 * Created by Oleksandr on 11-Sep-17.
 */
class YtsServiceApplication : Application() {
    var serviceApi: ServiceApi? = null
        private set

    override fun onCreate() {
        super.onCreate()
        serviceApi = ServiceApi.Instance
    }

}