package se.asapehrsson.podtest.data

import com.google.gson.annotations.SerializedName

data class Broadcast(
        @SerializedName("availablestoputc") val availablestoputc: String? = null,
        @SerializedName("playlist") val playlist: Playlist? = null,
        @SerializedName("broadcastfiles") val broadcastfiles: List<Broadcastfile>? = null
)
