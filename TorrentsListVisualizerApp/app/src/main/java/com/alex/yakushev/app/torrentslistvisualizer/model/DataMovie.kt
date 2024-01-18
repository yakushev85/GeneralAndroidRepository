package com.alex.yakushev.app.torrentslistvisualizer.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Oleksandr on 10-Sep-17.
 */
class DataMovie : Parcelable {
    @SerializedName("movie_count")
    var movieCount = 0
        private set

    @SerializedName("limit")
    var limit = 0
        private set

    @SerializedName("page_number")
    var pageNumber = 0
        private set

    @SerializedName("movies")
    val movies: List<MovieInfo>? = null
        get() = field ?: emptyList()

    constructor() {}
    protected constructor(`in`: Parcel) {
        movieCount = `in`.readInt()
        limit = `in`.readInt()
        pageNumber = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(movieCount)
        parcel.writeInt(limit)
        parcel.writeInt(pageNumber)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DataMovie> = object : Parcelable.Creator<DataMovie> {
            override fun createFromParcel(`in`: Parcel): DataMovie {
                return DataMovie(`in`)
            }

            override fun newArray(size: Int): Array<DataMovie?> {
                return arrayOfNulls(size)
            }
        }
    }
}