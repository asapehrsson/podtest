package se.asapehrsson.podtest.data

import com.google.gson.annotations.SerializedName

data class Broadcastfile(
        @SerializedName("duration") val duration: Int? = null,
        @SerializedName("publishdateutc") val publishDateUTC: String? = null,
        @SerializedName("id") val id: Int? = null,
        @SerializedName("url") val url: String? = null,
        @SerializedName("statkey") val statkey: String? = null
)
