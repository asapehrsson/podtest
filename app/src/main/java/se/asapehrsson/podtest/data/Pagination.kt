package se.asapehrsson.podtest.data

import com.google.gson.annotations.SerializedName

data class Pagination(
        @SerializedName("page") val page: Int? = null,
        @SerializedName("size") val size: Int? = null,
        @SerializedName("totalhits") val totalhits: Int? = null,
        @SerializedName("totalpages") val totalpages: Int? = null,
        @SerializedName("nextpage") val nextpage: String? = null
)
