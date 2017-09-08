package se.asapehrsson.podtest.data

import com.google.gson.annotations.SerializedName

data class Liveaudio(
        @SerializedName("id") val id: Int? = null,
        @SerializedName("url") val url: String? = null,
        @SerializedName("statkey") val statkey: String? = null
)