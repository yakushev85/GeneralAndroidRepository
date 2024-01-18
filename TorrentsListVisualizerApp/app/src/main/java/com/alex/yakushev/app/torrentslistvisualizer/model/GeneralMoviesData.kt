package com.alex.yakushev.app.torrentslistvisualizer.model

import android.os.Parcel
import android.os.Parcelable
import com.alex.yakushev.app.torrentslistvisualizer.model.DataMovie
import com.google.gson.annotations.SerializedName

/**
 * Created by Oleksandr on 10-Sep-17.
 */
class GeneralMoviesData : Parcelable {
    @SerializedName("status")
    var status: String? = null
        private set

    @SerializedName("status_message")
    var statusMessage: String? = null
        private set

    @SerializedName("data")
    var data: DataMovie? = null
        private set

    constructor() {}
    protected constructor(`in`: Parcel) {
        status = `in`.readString()
        statusMessage = `in`.readString()
        data = `in`.readParcelable(DataMovie::class.java.classLoader)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(status)
        parcel.writeString(statusMessage)
        parcel.writeParcelable(data, i)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GeneralMoviesData?> = object : Parcelable.Creator<GeneralMoviesData?> {
            override fun createFromParcel(`in`: Parcel): GeneralMoviesData {
                return GeneralMoviesData(`in`)
            }

            override fun newArray(size: Int): Array<GeneralMoviesData?> {
                return arrayOfNulls(size)
            }
        }
    }
}