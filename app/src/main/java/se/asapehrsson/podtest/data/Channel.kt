package se.asapehrsson.podtest.data

import com.google.gson.annotations.SerializedName

data class Channel(
        @SerializedName("image") val image: String? = null,
        @SerializedName("imagetemplate") val imagetemplate: String? = null,
        @SerializedName("color") val color: String? = null,
        @SerializedName("siteurl") val siteurl: String? = null,
        @SerializedName("liveaudio") val liveaudio: Liveaudio? = null,
        @SerializedName("scheduleurl") val scheduleurl: String? = null,
        @SerializedName("channeltype") val channeltype: String? = null,
        @SerializedName("xmltvid") val xmltvid: String? = null,
        @SerializedName("id") val id: Int? = null,
        @SerializedName("name") val name: String? = null
)