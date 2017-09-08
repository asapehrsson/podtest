package se.asapehrsson.podtest.data

import com.google.gson.annotations.SerializedName

data class Socialmediaplatform(

        @SerializedName("platform") val platform: String? = null,
        @SerializedName("platformurl") val platformurl: String? = null
)