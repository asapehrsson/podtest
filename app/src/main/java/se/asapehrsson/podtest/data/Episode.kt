package se.asapehrsson.podtest.data

import com.google.gson.annotations.SerializedName

data class Episode(
        @SerializedName("id") val id: Int? = null,
        @SerializedName("title") val title: String? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("url") val url: String? = null,
        @SerializedName("program") val program: Program? = null,
        @SerializedName("audiopreference") val audiopreference: String? = null,
        @SerializedName("publishdateutc") val publishdateutc: String? = null,
        @SerializedName("imageurl") val imageurl: String? = null,
        @SerializedName("imageurltemplate") val imageurltemplate: String? = null,
        @SerializedName("broadcast") val broadcast: Broadcast? = null,
        @SerializedName("listenpodfile") val listenpodfile: Listenpodfile? = null,
        @SerializedName("downloadpodfile") val downloadpodfile: Downloadpodfile? = null
)
