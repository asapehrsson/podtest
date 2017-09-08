package se.asapehrsson.podtest.data

import com.google.gson.annotations.SerializedName

data class Programcategory(
        @SerializedName("id") val id: Int = -1,
        @SerializedName("name") val name: String? = null
)
