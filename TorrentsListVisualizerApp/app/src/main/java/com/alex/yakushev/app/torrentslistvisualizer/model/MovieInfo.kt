package com.alex.yakushev.app.torrentslistvisualizer.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Oleksandr on 10-Sep-17.
 */
class MovieInfo : Parcelable {
    @SerializedName("id")
    var id = 0
        private set

    @SerializedName("url")
    var url: String? = null
        private set

    @SerializedName("title")
    var title: String? = null
        private set

    @SerializedName("title_english")
    var titleEnglish: String? = null
        private set

    @SerializedName("title_long")
    var titleLong: String? = null
        private set

    @SerializedName("year")
    var year = 0
        private set

    @SerializedName("rating")
    var rating = 0f
        private set

    @SerializedName("runtime")
    var runtime = 0
        private set

    @SerializedName("genres")
    val genres: List<String>? = null
        get() = field ?: emptyList()

    @SerializedName("summary")
    var summary: String? = null
        private set

    @SerializedName("background_image")
    var backgroundImage: String? = null
        private set

    @SerializedName("background_image_orginal")
    var backgroundImageOriginal: String? = null
        private set

    @SerializedName("small_cover_image")
    var smallCoverImage: String? = null
        private set

    @SerializedName("medium_cover_image")
    var mediumCoverImage: String? = null
        private set

    @SerializedName("large_cover_image")
    var largeCoverImage: String? = null
        private set

    constructor() {}
    protected constructor(`in`: Parcel) {
        id = `in`.readInt()
        url = `in`.readString()
        title = `in`.readString()
        titleEnglish = `in`.readString()
        titleLong = `in`.readString()
        year = `in`.readInt()
        rating = `in`.readFloat()
        runtime = `in`.readInt()
        summary = `in`.readString()
        backgroundImage = `in`.readString()
        backgroundImageOriginal = `in`.readString()
        smallCoverImage = `in`.readString()
        mediumCoverImage = `in`.readString()
        largeCoverImage = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(url)
        dest.writeString(title)
        dest.writeString(titleEnglish)
        dest.writeString(titleLong)
        dest.writeInt(year)
        dest.writeFloat(rating)
        dest.writeInt(runtime)
        dest.writeString(summary)
        dest.writeString(backgroundImage)
        dest.writeString(backgroundImageOriginal)
        dest.writeString(smallCoverImage)
        dest.writeString(mediumCoverImage)
        dest.writeString(largeCoverImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MovieInfo> = object : Parcelable.Creator<MovieInfo> {
            override fun createFromParcel(`in`: Parcel): MovieInfo {
                return MovieInfo(`in`)
            }

            override fun newArray(size: Int): Array<MovieInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}