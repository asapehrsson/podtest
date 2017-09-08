package se.asapehrsson.podtest.data

import com.google.gson.annotations.SerializedName

data class Episodes(
        @SerializedName("episodes") val episodes: List<Episode>? = null,
        @SerializedName("pagination") val pagination: Pagination? = null
)
