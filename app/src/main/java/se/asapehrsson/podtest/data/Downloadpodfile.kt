package se.asapehrsson.podtest.data

import com.google.gson.annotations.SerializedName

data class Downloadpodfile(
        @SerializedName("title") val title: String? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("filesizeinbytes") val filesizeinbytes: Int? = null,
        @SerializedName("program") val program: Program? = null,
        @SerializedName("duration") val duration: Int? = null,
        @SerializedName("publishdateutc") val publishdateutc: String? = null,
        @SerializedName("id") val id: Int? = null,
        @SerializedName("url") val url: String? = null,
        @SerializedName("statkey") val statkey: String? = null
)
