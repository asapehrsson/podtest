package se.asapehrsson.podtest.data

import com.google.gson.annotations.SerializedName

data class Pagination(
        @SerializedName("page") val page: Int = 0,
        @SerializedName("size") val size: Int = 0,
        @SerializedName("totalhits") val totalhits: Int = 0,
        @SerializedName("totalpages") val totalpages: Int = 0,
        @SerializedName("nextpage") val nextpage: String? = null
)
